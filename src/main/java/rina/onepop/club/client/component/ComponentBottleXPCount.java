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
 * @since 08/06/2021 at 01:24
 **/
public class ComponentBottleXPCount extends Component {
    ComponentSetting<XPType> settingType = new ComponentSetting<>("Type", "Type", "The type.", XPType.TEXT);

    public int bottleXPs;

    public ComponentBottleXPCount() {
        super("Bottle XP Count", "BottleXPCount", "Counts bottle xp for you!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        this.bottleXPs = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == Items.EXPERIENCE_BOTTLE).mapToInt(ItemStack::getCount).sum();

        if (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            this.bottleXPs += mc.player.getHeldItemOffhand().getCount();
        }

        if (this.settingType.getValue() == XPType.TEXT) {
            String count = "Bottle XP " + ChatFormatting.GRAY + this.bottleXPs;

            this.render(count, 0, 0);

            this.rect.setWidth(this.getStringWidth(count));
            this.rect.setHeight(this.getStringHeight(count));
        } else {
            TurokGL.pushMatrix();
            GlStateManager.enableTexture2D();
            RenderHelper.enableGUIStandardItemLighting();

            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.EXPERIENCE_BOTTLE), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY());
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(Items.END_CRYSTAL), (int) this.rect.getX() + this.verifyDock(0, 16), (int) this.rect.getY(), null);
            renderItem.zLevel = -5f;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableTexture2D();
            TurokGL.popMatrix();

            String formatted = "" + this.bottleXPs;

            this.render(formatted,(float) 16 + 1, 6);

            this.rect.setWidth(16 + 1 + this.getStringWidth(formatted));
            this.rect.setHeight(14);
        }
    }
}
