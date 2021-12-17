package rina.onepop.club.client.component;

import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

/**
 * @author SrRina
 * @since 04/04/2021 at 18:57
 **/
public class ComponentArmor extends Component {
    /* Misc settings. */
    public static ComponentSetting<Boolean> settingHotbar = new ComponentSetting<>("Hotbar Position", "HotbarPosition", "Set the position in the hotbar.", true);
    public static ComponentSetting<Boolean> settingReverse = new ComponentSetting<>("Reverse", "Reverse", "Reverse armor.", false);

    private int offset;

    public ComponentArmor() {
        super("Armor", "Armor", "Armor preview.", StringType.NOT_USE);
    }

    @Override
    public void onRender(float partialTicks) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        BlockPos selfPosition = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

        this.offset = (int) TurokMath.serp(this.offset, mc.world.getBlockState(selfPosition.up()).getBlock() == Blocks.WATER ? 10 : 0, partialTicks);

        this.render(0,0, this.rect.getWidth(), this.rect.getHeight(), new Color(0, 0, 0, 0));

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();

        RenderHelper.enableGUIStandardItemLighting();

        RenderItem renderItem = mc.getRenderItem();

        // :troll_face:
        final TurokDisplay display = new TurokDisplay(mc);

        final int l = display.getScaledWidth() / 2;
        final int k = display.getScaledHeight() - 55 - (this.offset);

        if (settingHotbar.getValue()) {
            this.rect.setX(l + 8);
            this.rect.setY(k);
        }

        int x = (int) this.rect.getX();
        int y = (int) this.rect.getY();

        if (settingReverse.getValue()) {
            for (int i = 3; i >= 0; --i) {
                ItemStack itemStack = mc.player.inventory.armorItemInSlot(i);

                if (itemStack.getItem() != Items.AIR) {
                    renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
                    renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
                }

                x += 16;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                ItemStack itemStack = mc.player.inventory.armorItemInSlot(i);

                if (itemStack.getItem() != Items.AIR) {
                    renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
                    renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
                }

                x += 16;
            }
        }

        renderItem.zLevel = -5f;

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        this.rect.setHeight(16);
        this.rect.setWidth(16 * 4);
    }
}
