package official.zamil.dropall;

import net.minecraft.item.ItemStack;

/** A snapshot of one slot; only unchanged contents may be dropped. */
public record PendingDrop(int slotId, ItemStack stack) { }
