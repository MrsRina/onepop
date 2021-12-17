package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import net.minecraft.util.EnumFacing;

/**
 * @author SrRina
 * @author Manesko
 * @since 04/04/2021 at 18:31
 **/
public class ComponentCoordinates extends Component {
    public static ComponentSetting<Direction> settingDirection = new ComponentSetting<>("Direction", "Direction", "Direction of the player.", Direction.XZ);
    public static ComponentSetting<Placement> settingPlacement = new ComponentSetting<>("Mode","mode","Mode of display", Placement.Flat);

    public ComponentCoordinates() {
        super("Coordinates", "Coordinates", "Shows current player coordinates.", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        String x = String.format("%.1f", mc.player.posX);
        String y = String.format("%.1f", mc.player.posY);
        String z = String.format("%.1f", mc.player.posZ);

        final float value = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell") ? 8 : 0.125f;

        String xNether = String.format("%.1f", mc.player.posX * value);
        String zNether = String.format("%.1f", mc.player.posZ * value);

        String coordinatesFlat = this.getDirection() + "XYZ " + ChatFormatting.GRAY + x + ", " + y + ", " + z + ChatFormatting.RESET + " [" + ChatFormatting.GRAY + xNether + ", " + zNether + ChatFormatting.RESET + "]";
        String directionStacked = this.getDirection();
        String coordinatesStackedX = "X " + ChatFormatting.GRAY + x + ChatFormatting.RESET + " [" + ChatFormatting.GRAY + xNether + ChatFormatting.RESET + "]";
        String coordinatesStackedY = "X " + ChatFormatting.GRAY + y;
        String coordinatesStackedZ = "X " + ChatFormatting.GRAY + z + ChatFormatting.RESET + " [" + ChatFormatting.GRAY + zNether + ChatFormatting.RESET + "]";

        switch ((Placement) settingPlacement.getValue()) {
            case Flat: {
                this.render(coordinatesFlat, 0, 0);

                this.rect.setWidth(getStringWidth(coordinatesFlat));
                this.rect.setHeight(getStringHeight(coordinatesFlat));
                break;
            }
            case Stacked: {
                this.render(directionStacked,0,0);
                this.render(coordinatesStackedX,0,10);
                this.render(coordinatesStackedY,0,20);
                this.render(coordinatesStackedZ,0,30);

                this.rect.setWidth(getStringWidth(coordinatesStackedX));

                switch ((Direction) settingDirection.getValue()) {
                    case XZ: {
                        this.rect.setHeight(40);
                        break;
                    }
                    case NSWE: {
                        this.rect.setHeight(40);
                        break;
                    }

                    case NONE: {
                        this.rect.setHeight(30);
                        break;
                    }
                }
            }
        }

    }

    public String getDirection() {
        String the = "";

        switch ((Direction) settingDirection.getValue()) {
            case XZ: {
                the = this.getFaceDirection(true, false);

                break;
            }

            case NSWE: {
                the = this.getFaceDirection(false, true);

                break;
            }
        }

        return the;
    }

    public String getFaceDirection(boolean xz, boolean nswe) {
        EnumFacing facing = mc.getRenderViewEntity().getHorizontalFacing();

        String value = "Invalid";

        String l = ChatFormatting.RESET + "[" + ChatFormatting.GRAY;
        String r = ChatFormatting.RESET + "]";

        switch (facing) {
            case NORTH: value = xz ? l + "-Z" + r : nswe ? l + "N" + r : "North " + l + "-Z" + r; break;
            case SOUTH: value = xz ? l + "+Z" + r : nswe ? l + "S" + r : "South " + l + "+Z" + r; break;
            case WEST: value  = xz ? l + "-X" + r : nswe ? l + "W" + r : "West " + l + "-X" + r; break;
            case EAST: value  = xz ? l + "+X" + r : nswe ? l + "E" + r : "East " + l + "+X" + r;
        }

        return value + " ";
    }

}