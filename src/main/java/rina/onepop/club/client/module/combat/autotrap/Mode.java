package rina.onepop.club.client.module.combat.autotrap;

import net.minecraft.util.math.BlockPos;

public enum Mode {
    FULLY(ModuleAutoTrap.MASK_FULLY), CITY(ModuleAutoTrap.MASK_CITY);

    BlockPos[] mask;

    Mode(final BlockPos[] mask) {
        this.mask = mask;
    }

    public BlockPos[] getMask() {
        return mask;
    }
}
