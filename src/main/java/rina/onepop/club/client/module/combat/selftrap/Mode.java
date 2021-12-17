package rina.onepop.club.client.module.combat.selftrap;

import net.minecraft.util.math.BlockPos;

public enum Mode {
    FULLY(ModuleSelfTrap.MASK_FULLY), CITY(ModuleSelfTrap.MASK_CITY);

    BlockPos[] mask;

    Mode(final BlockPos[] mask) {
        this.mask = mask;
    }

    public BlockPos[] getMask() {
        return mask;
    }
}
