package official.zamil.dropall.mixin;

import official.zamil.dropall.PendingDrop;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** All inventory mutations go through vanilla server-validated THROW actions. */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow protected Slot focusedSlot;
    @Shadow @Final protected ScreenHandler handler;
    @Unique private final Deque<PendingDrop> dropall$pending = new ArrayDeque<>();
    @Unique private boolean dropall$qHeld;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void dropall$keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (input.key() != GLFW.GLFW_KEY_Q
                || (input.modifiers() & GLFW.GLFW_MOD_ALT) == 0) return;
        // Consume the shortcut even if there is no eligible stack: never fall through to Q.
        cir.setReturnValue(true);
        if (dropall$qHeld) return;
        dropall$qHeld = true;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null
                || client.player.currentScreenHandler != handler) return;
        if ((Object) this instanceof CreativeInventoryScreen) {
            client.player.sendMessage(Text.literal("DropAll: use the survival inventory or an open container."), true);
            return;
        }
        if (!dropall$pending.isEmpty()) return;
        if (!handler.getCursorStack().isEmpty()) {
            client.player.sendMessage(Text.literal("DropAll: put down the item held by your cursor first."), true);
            return;
        }
        if (focusedSlot == null || !focusedSlot.hasStack()) return;
        Item target = focusedSlot.getStack().getItem();
        for (Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();
            // Output/preview slots are deliberately excluded to avoid crafting or trading.
            if (!stack.isEmpty() && stack.isOf(target) && slot.isEnabled()
                    && slot.canTakeItems(client.player) && slot.canInsert(stack)) {
                dropall$pending.addLast(new PendingDrop(slot.id, stack.copy()));
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void dropall$tick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_Q) == GLFW.GLFW_RELEASE) {
            dropall$qHeld = false;
        }
        if (dropall$pending.isEmpty()) return;
        if (client.currentScreen != (Object) this || client.player == null
                || client.interactionManager == null || client.player.currentScreenHandler != handler
                || !handler.getCursorStack().isEmpty()) {
            dropall$pending.clear();
            return;
        }
        // At most one stack per tick; snapshot checks prevent dropping replacement items.
        PendingDrop next = dropall$pending.removeFirst();
        if (next.slotId() < 0 || next.slotId() >= handler.slots.size()) return;
        Slot slot = handler.getSlot(next.slotId());
        ItemStack stack = slot.getStack();
        if (slot.isEnabled() && slot.canTakeItems(client.player) && slot.canInsert(stack)
                && !stack.isEmpty() && stack.getCount() == next.stack().getCount()
                && ItemStack.areItemsAndComponentsEqual(stack, next.stack())) {
            client.interactionManager.clickSlot(handler.syncId, slot.id, 1,
                    SlotActionType.THROW, client.player);
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void dropall$removed(CallbackInfo ci) {
        dropall$pending.clear();
        dropall$qHeld = false;
    }

}
