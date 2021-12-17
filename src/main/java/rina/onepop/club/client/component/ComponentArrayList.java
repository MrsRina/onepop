package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.Dock;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.module.Module;
import me.rina.turok.render.opengl.deprecated.TurokRenderGL;
import rina.onepop.club.api.component.impl.ComponentSetting;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SrRina
 * @since 08/04/2021 at 15:12
 **/
public class ComponentArrayList extends Component {
    /**
     * Its a custom rect class to states like enabled, tag, all for one better performance.
     */
    public static class Rect extends TurokRect {
        private Module module;

        public Rect(Module module, int x, int y) {
            super(module.getTag(), x, y);

            this.module = module;
        }

        public Module getModule() {
            return module;
        }
    }

    /* Misc. */
    public static ComponentSetting<Boolean> settingAnimation = new ComponentSetting<>("Animation", "Animation", "Smooth animation!", false);
    public static ComponentSetting<Integer> settingBackgroundAlpha = new ComponentSetting<>("Background Alpha", "Background Alpha", "Background alpha!", 100, 0, 255);
    public static ComponentSetting<Boolean> settingStyle = new ComponentSetting<>("[<- Detail ->]", "Detail", "Cool detail!", true);

    private final List<Rect> rectList = new ArrayList<>();

    public ComponentArrayList() {
        super("Array List", "ArrayList", "List of enabled modules.", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        final Comparator<Module> comparatorModule = (module1, module2) -> {
            String k = module1.getTag() + (module1.getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + module1.getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + module1.getStatus()));
            String s = module2.getTag() + (module2.getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + module2.getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + module2.getStatus()));

            float diff = getStringWidth(s) - getStringWidth(k);

            if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
                return diff != 0 ? (int) diff : s.compareTo(k);
            } else {
                return (int) diff;
            }
        };

        final Comparator<Rect> comparatorRect = (rect1, rect2) -> {
            String k = rect1.getModule().getTag() + (rect1.getModule().getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + rect1.getModule().getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + rect1.getModule().getStatus()));
            String s = rect2.getModule().getTag() + (rect2.getModule().getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + rect2.getModule().getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + rect2.getModule().getStatus()));

            float diff = getStringWidth(s) - getStringWidth(k);

            if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
                return diff != 0 ? (int) diff : s.compareTo(k);
            } else {
                return (int) diff;
            }
        };

        boolean top = true;

        List<Module> hackList;

        if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
            hackList = Onepop.getModuleManager().getModuleList().stream().filter(Module::shouldRenderOnArrayList).sorted(comparatorModule).collect(Collectors.toList());
        } else {
            top = false;

            hackList = Onepop.getModuleManager().getModuleList().stream().filter(Module::shouldRenderOnArrayList).sorted(Comparator.comparing(module -> getStringWidth(module.getTag() + (module.getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + module.getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + module.getStatus()))))).collect(Collectors.toList());
        }

        if (!settingAnimation.getValue()) {
            this.rectList.clear();
        }

        for (Module modules : hackList) {
            if (modules.isEnabled()) {
                TurokRect rect = this.getRect(modules.getTag());

                if (rect == null) {
                    this.rectList.add(new Rect(modules, 0, 0));
                }
            }
        }

        if (top) {
            this.rectList.sort(comparatorRect);
        } else {
            this.rectList.sort(Comparator.comparing(rects -> getStringWidth(rects.getModule().getTag() + (rects.getModule().getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + rects.getModule().getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + rects.getModule().getStatus())))));
        }

        TurokDisplay display = new TurokDisplay(mc);

        TurokRenderGL.enableState(GL11.GL_SCISSOR_TEST);
        TurokRenderGL.drawScissor(this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rect.getHeight(), display);

        int cache = 0;

        int offsetX = (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) ? 0 : 0;
        int offsetW = (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) ? 0 : 0;

        for (Rect rects : this.rectList) {
            if (rects.getModule() == null || !rects.getModule().shouldRenderOnArrayList()) {
                continue;
            }

            String tag = rects.getModule().getTag() + (rects.getModule().getStatus() == null ? "" : (settingStyle.getValue() ? (ChatFormatting.GRAY + " [" + rects.getModule().getStatus() + ChatFormatting.GRAY + "]") : ChatFormatting.GRAY + rects.getModule().getStatus()));

            rects.setWidth(this.getStringWidth(tag));
            rects.setHeight(this.getStringHeight(tag));

            if (settingBackgroundAlpha.getValue() != 0) {
                this.render((int) rects.getX() + offsetW, cache, this.getStringWidth(tag) + offsetX, this.getStringHeight(tag), new Color(0, 0, 0, settingBackgroundAlpha.getValue()));
            }

            this.render(tag, rects.getX(), cache);

            if (rects.getWidth() >= this.rect.getWidth()) {
                this.rect.setWidth(rects.getWidth());
            }

            if (!rects.getModule().isEnabled()) {
                rects.setX(TurokMath.lerp(rects.getX(), verifyDock(this.getClamp(rects, 0), rects.getWidth()), partialTicks * 0.1f));
            } else {
                rects.setX(TurokMath.lerp(rects.getX(),0, partialTicks * 0.1f));
            }

            if (!this.verifyClamp(rects, 2)) {
                cache += rects.getHeight();
            }
        }

        this.rect.setHeight(cache);

        TurokRenderGL.disable(GL11.GL_SCISSOR_TEST);
    }

    public TurokRect getRect(String tag) {
        for (TurokRect rects : this.rectList) {
            if (rects.getTag().equalsIgnoreCase(tag)) {
                return rects;
            }
        }

        return null;
    }

    public boolean verifyClamp(Rect k, int diff) {
        boolean flag = false;

        if (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) {
            int clamp = this.getClamp(k, diff);

            if (k.getX() <= clamp) {
                flag = true;
            }
        }

        if (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) {
            int clamp = this.getClamp(k, diff);

            if (verifyDock(k.getX(), k.getWidth()) >= clamp) {
                flag = true;
            }
        }

        return flag;
    }

    public int getClamp(Rect k, int diff) {
        if (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) {
            return (int) ((-k.getWidth()) + diff);
        }

        if (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) {
            return (int) ((this.rect.getWidth() * 2) - diff);
        }

        return (int) this.rect.getX();
    }
}
