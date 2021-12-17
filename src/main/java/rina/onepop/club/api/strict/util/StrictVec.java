package rina.onepop.club.api.strict.util;

import net.minecraft.util.math.Vec3d;

/**
 * @author SrRina
 * @since 06/07/2021 at 18:13
 *
 * - Doctor swag :heart_eyes:
 *
 **/
public class StrictVec {
    private Vec3d vec;
    private Rotation rotation;

    public StrictVec(Vec3d vec, Rotation rotation) {
        this.setVec(vec);
        this.setRotation(rotation);
    }

    public Vec3d getVec() {
        return this.vec;
    }

    public void setVec(Vec3d vec) {
        this.vec = vec;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}
