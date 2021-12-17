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
public class ComponentGoldenAppleCount extends Component {
    private final ComponentSetting<GoldenAppleType> settingType = new ComponentSetting<>("Type", "Type", "The type.", GoldenAppleType.TEXT);
    public int goldenApples;

    public ComponentGoldenAppleCount() {
        super("Golden Apple Count", "GoldenAppleCount", "Counts golden apples for you!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        this.goldenApples = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();

        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            this.goldenApples += mc.player.getHeldItemOffhand().getCount();
        }

        if (this.settingType.getValue() == GoldenAppleType.TEXT) {
            String count = "Golden Apples " + ChatFormatting.GRAY + this.goldenApples;

            this.render(count, 0, 0);

            this.rect.setWidth(this.getStringWidth(count));
            this.rect.setHeight(this.getStringHeight(count));
        } else {
            TurokGL.pushMatrix();
            GlStateManager.enableTexture2D();
            RenderHelper.enableGUIStandardItemLighting();

            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLDEN_APPLE), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY());
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(Items.GOLDEN_APPLE), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY(), null);
            renderItem.zLevel = -5f;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableTexture2D();
            TurokGL.popMatrix();

            String formatted = "" + this.goldenApples;

            this.render(formatted,(float) 16 + 1, 6);

            this.rect.setWidth(16 + 1 + this.getStringWidth(formatted));
            this.rect.setHeight(14);
        }
    }
}
