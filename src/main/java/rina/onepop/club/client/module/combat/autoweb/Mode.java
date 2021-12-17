package rina.onepop.club.client.module.combat.autoweb;

import net.minecraft.util.math.BlockPos;

public enum Mode {
    TRAP(ModuleAutoWeb.MASK_FULLY), POSITION(ModuleAutoWeb.EMPTY);

    BlockPos[] mask;

    Mode(final BlockPos[] mask) {
        this.mask = mask;
    }

    public BlockPos[] getMask() {
        return mask;
    }
}
