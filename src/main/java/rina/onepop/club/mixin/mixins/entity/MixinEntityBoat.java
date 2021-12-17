package rina.onepop.club.mixin.mixins.entity;

import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author SrRina
 * @since 27/06/2021 at 04:09
 **/
@Mixin(EntityBoat.class)
public class MixinEntityBoat {
    //@Redirect(method = "getBoatStatus", )
}
