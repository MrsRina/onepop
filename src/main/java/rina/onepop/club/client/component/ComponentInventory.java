package rina.onepop.club.client.component;

import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * @author SrRina
 * @since 04/04/2021 at 18:57
 **/
public class ComponentInventory extends Component {
    /* Misc settings. */
   public static ComponentSetting<Integer> settingAlpha = new ComponentSetting<>("Alpha", "Alpha", "The background alpha!", 100, 0, 255);

    public ComponentInventory() {
        super("Inventory", "Inventory", "Inventory preview.", StringType.NOT_USE);
    }

    @Override
    public void onRender(float partialTicks) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();

        RenderHelper.enableGUIStandardItemLighting();
        RenderItem renderItem = mc.getRenderItem();

        this.render(0,0, this.rect.getWidth(), this.rect.getHeight(), new Color(0, 0, 0, settingAlpha.getValue()));

        for (int i = 0; i < 27; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i + 9);

            int x = (int) (this.rect.getX() + (i % 9) * 16);
            int y = (int) (this.rect.getY() + (i / 9) * 16);

            if (itemStack.getItem() != Items.AIR) {
                renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
            }
        }

        renderItem.zLevel = -5f;

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        this.rect.setWidth(144);
        this.rect.setHeight(48);
    }
}
