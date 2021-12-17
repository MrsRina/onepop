package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import me.rina.turok.render.opengl.TurokGL;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author SrRina
 * @since 07/04/2021 at 13:52
 **/
public class ComponentCrystalCount extends Component {
    ComponentSetting<CrystalType> settingType = new ComponentSetting<>("Type", "Type", "The type.", CrystalType.TEXT);

    public int crystals;

    public ComponentCrystalCount() {
        super("Crystal Count", "CrystalCount", "Counts crystals for you!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        this.crystals = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();

        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            this.crystals += mc.player.getHeldItemOffhand().getCount();
        }

        if (this.settingType.getValue() == CrystalType.TEXT) {
            String count = "Crystals " + ChatFormatting.GRAY + this.crystals;

            this.render(count, 0, 0);

            this.rect.setWidth(this.getStringWidth(count));
            this.rect.setHeight(this.getStringHeight(count));
        } else {
            TurokGL.pushMatrix();
            GlStateManager.enableTexture2D();
            RenderHelper.enableGUIStandardItemLighting();

            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.END_CRYSTAL), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY());
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(Items.END_CRYSTAL), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY(), null);
            renderItem.zLevel = -5f;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableTexture2D();
            TurokGL.popMatrix();

            String formatted = "" + this.crystals;

            this.render(formatted,(float) 16 + 1, 6);

            this.rect.setWidth(16 + 1 + this.getStringWidth(formatted));
            this.rect.setHeight(14);
        }
    }
}