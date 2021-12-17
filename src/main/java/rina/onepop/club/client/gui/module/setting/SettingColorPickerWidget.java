package rina.onepop.club.client.gui.module.setting;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import me.rina.turok.util.TurokTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.setting.value.Picker;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.client.gui.imperador.ImperadorEntryBox;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;
import rina.onepop.club.client.gui.rocan.RocanSlider;
import rina.onepop.club.client.gui.rocan.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * @author SrRina
 * @since 06/07/2021 at 09:29
 * 
 * The color picker process render and math:
 * Doctor Swag
 **/
public class SettingColorPickerWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private ModuleWidget module;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int animationX;
    private int animationY;

    private int animationApplierY;

    private ValueColor setting;
    private int alphaAnimationPressed;

    private float circleX;
    private float circleY;

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedRight;

    private boolean isRendering;
    private boolean isOpen;

    private boolean isStarted;

    private float openedHeight;
    private float closedHeight;

    private final TurokRect rectClick = new TurokRect("Click!", 0, 0);
    private final TurokRect rectColorInfo = new TurokRect("The Color Info Rect!", 0, 0);
    private final TurokRect rectColorChooser = new TurokRect("The Color Chooser Rect!", 0, 0);
    private final TurokRect rectColorHUE = new TurokRect("The Nigger HUE!", 0, 0);

    private final RocanSlider alphaSlider;
    private final TurokTick doubleClick = new TurokTick();

    public Flag flagMouse = Flag.MOUSE_NOT_OVER;
    public Flag flagMouseButton = Flag.MOUSE_NOT_OVER;

    // lol sorry.
    private float colorWidth;
    private float colorHeight;
    private boolean selecting;
    private boolean hueSelecting;
    private boolean alphaSelecting;
    private float hue;
    private float alpha;
    private Color color;

    DynamicTexture ALPHA_SLIDER;

    private ImperadorEntryBox entryBox;
    private boolean isUpdate;

    private String lastStringFixedRGB;
    private String currentStringRGB;

    private Color theColorFromEntryBox;

    public SettingColorPickerWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, ModuleWidget module, final ValueColor setting) {
        super(setting.getName());

        this.master = master;
        this.frame = frame;

        this.container = container;
        this.module = module;

        this.setting = setting;
        this.alphaSlider = new RocanSlider("AlphaSlider", null, this.master.getMouse());

        this.init();

        try {
            ALPHA_SLIDER = new DynamicTexture(ImageIO.read(SettingColorPickerWidget.class.getResourceAsStream("/assets/onepop/images/transparent.png")));
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void init() {
        this.rect.setWidth(this.module.getRect().getWidth() - this.offsetX);
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);

        this.rectClick.setHeight(this.rect.getHeight());

        this.entryBox = new ImperadorEntryBox(this.master.fontWidgetModule, "");
        this.entryBox.getRect().setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);
    }

    @Override
    public boolean isEnabled() {
        return this.setting.isEnabled();
    }

    public ValueColor getSetting() {
        return setting;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetWidth(int offsetWidth) {
        this.offsetWidth = offsetWidth;
    }

    public int getOffsetWidth() {
        return offsetWidth;
    }

    public void setOffsetHeight(int offsetHeight) {
        this.offsetHeight = offsetHeight;
    }

    public int getOffsetHeight() {
        return offsetHeight;
    }

    public void setAnimationX(int animationX) {
        this.animationX = animationX;
    }

    public int getAnimationX() {
        return animationX;
    }

    public void setAnimationY(int animationY) {
        this.animationY = animationY;
    }

    public int getAnimationY() {
        return animationY;
    }

    public void setAnimationApplierY(int animationApplierY) {
        this.animationApplierY = animationApplierY;
    }

    public int getAnimationApplierY() {
        return animationApplierY;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpenedHeight(int openedHeight) {
        this.openedHeight = openedHeight;
    }

    public float getOpenedHeight() {
        return openedHeight;
    }

    public void setClosedHeight(int closedHeight) {
        this.closedHeight = closedHeight;
    }

    public float getClosedHeight() {
        return closedHeight;
    }

    @Override
    public void onScreenOpened() {

    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onScreenClosed() {
        this.isMouseClickedLeft = false;
        this.alphaSlider.setMouseClickedLeft(false);
    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {
        this.entryBox.onKeyboardPressed(charCode, keyCode);
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onMouseReleased(int button) {
        this.entryBox.onMouseReleased(button);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (this.isMouseClickedLeft && this.setting.getPicker() == Picker.BOOLEAN) {
                this.setting.setValue(!this.setting.getValue());
                this.container.refresh(this.module.getModule(), this.setting);

                this.isMouseClickedLeft = false;
            }

            if (this.isMouseClickedRight) {
                this.isOpen = !this.isOpen;

                this.rect.setHeight(this.isOpen() ? this.getOpenedHeight() : this.getClosedHeight());
                this.container.refresh(this.module.getModule(), this.setting);

                this.isMouseClickedRight = false;
            }
        } else {
            this.isMouseClickedLeft = false;
        }

        selecting = false;
        hueSelecting = false;
        alphaSelecting = false;
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (this.entryBox.isFocused() && !this.entryBox.isMouseOver()) {
            this.entryBox.setFocused(false);
        }

        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (this.isOpen() && this.module.isWidgetOpened()) {
                if (this.rectColorChooser.collideWithMouse(this.master.getMouse())) {
                    selecting = true;
                }

                if (this.rectColorHUE.collideWithMouse(this.master.getMouse())) {
                    hueSelecting = true;
                }

                if (this.alphaSlider.getRect().collideWithMouse(this.master.getMouse())) {
                    alphaSelecting = true;
                }
            }

            if (this.rectColorInfo.collideWithMouse(this.master.getMouse()) && button == 0) {
                if (!this.doubleClick.isPassedMS(750)) {
                    this.setting.setCycloned(!this.setting.isCycloned());
                } else {
                    this.doubleClick.reset();
                }
            }

            this.isMouseClickedLeft = button == 0 && this.rectClick.collideWithMouse(this.master.getMouse()) && !this.rectColorInfo.collideWithMouse(this.master.getMouse());
            this.isMouseClickedRight = button == 1;
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        this.entryBox.onMouseClicked(button);
        this.entryBox.doSetIndexAB(this.master.getMouse());
    }

    @Override
    public void onRender() {
        double diffValue = this.animationApplierY - this.animationY;
        double diffFinal = TurokMath.sqrt(diffValue * diffValue);

        if (diffFinal < 10f) {
            this.animationApplierY = this.animationY;
        } else {
            this.animationApplierY = (int) TurokMath.serp(this.animationApplierY, this.animationY, this.master.getDisplay().getPartialTicks());
        }

        this.alphaSlider.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.alphaSlider.setMouse(this.master.getMouse());

        this.alphaSlider.setType(Type.UP);

        this.alphaSlider.getRect().setWidth(12);
        this.alphaSlider.getRect().setHeight(50);

        this.alphaSlider.setMinimum(0);
        this.alphaSlider.setMaximum(255);

        this.alphaSlider.colorBackground = new int[] {
                0, 0, 0, 0
        };

        this.alphaSlider.colorBackgroundSlider = new int[] {
                0, 0, 0, 0
        };

        if (this.isStarted) {
            this.color = new Color(this.setting.getR(), this.setting.getG(), this.setting.getB(), this.setting.getA());
        } else {
            this.color = new Color(this.setting.getR(), this.setting.getG(), this.setting.getB(), this.setting.getA());
            this.setCoordinatesByColor(this.color);

            this.lastStringFixedRGB = this.write(this.setting.getR(), this.setting.getG(), this.setting.getB(), this.setting.getA());
            this.currentStringRGB = this.lastStringFixedRGB;

            this.isStarted = true;
        }

        this.offsetX = 2;

        this.rect.setX(this.module.getRect().getX() + this.animationX);
        this.rect.setY(this.container.getRect().getY() + this.module.getOffsetY() + this.animationApplierY);

        this.rect.setWidth(this.container.getRect().getWidth() - this.offsetX);
        this.rectClick.set(this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rectClick.getHeight());

        this.rectColorInfo.setWidth(9);
        this.rectColorInfo.setHeight(9);

        this.rectColorInfo.setX(this.rect.getX() + this.rect.getWidth() - this.rectColorInfo.getWidth() - 2f);
        this.rectColorInfo.setY(this.rect.getY() + (this.rectClick.getHeight() / 2) - (this.rectColorInfo.getHeight() / 2));

        this.rectColorChooser.setX(this.rect.getX() + 2);
        this.rectColorChooser.setY(this.alphaSlider.getRect().getY());
        this.rectColorChooser.setWidth(this.rect.getWidth() - (this.rect.getWidth() / 3));
        this.rectColorChooser.setHeight(this.alphaSlider.getRect().getHeight());

        this.alphaSlider.getRect().setX(this.rectColorHUE.getX() + this.rectColorHUE.getWidth() + 1f);
        this.alphaSlider.getRect().setY(this.rect.getY() + this.rectClick.getHeight() + 1f);

        this.rectColorHUE.set(this.rectColorChooser.getX() + this.rectColorChooser.getWidth() + 1, this.rectColorChooser.getY(), 15, this.rectColorChooser.getHeight());

        float offsetWidthRGB = 2f + TurokFontManager.getStringWidth(this.master.fontWidgetModule, "RGB: ") + 1f;

        this.entryBox.getRect().setX(this.rect.getX() + offsetWidthRGB);
        this.entryBox.getRect().setY(this.rectColorChooser.getY() + this.rectColorChooser.getHeight() + 1f);

        this.entryBox.getRect().setWidth(this.rect.getWidth() - (offsetWidthRGB));

        if ((this.setting.getValue() || this.setting.getPicker() == Picker.NORMAL) && this.module.isWidgetOpened()) {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, this.master.guiColor.base[3], this.master.getDisplay().getPartialTicks());
        } else {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, 0, this.master.getDisplay().getPartialTicks());
        }

        if (this.entryBox.isFocused()) {
            this.master.setCanceledCloseGUI(true);
            this.isUpdate = true;

            this.entryBox.doMouseScroll(this.master.getMouse());
            this.entryBox.string = new int[] {
                    0, 0, 0, 255
            };

            this.theColorFromEntryBox = this.read(this.entryBox.getText(), this.color);
            this.currentStringRGB = this.write(this.theColorFromEntryBox.getRed(), this.theColorFromEntryBox.getGreen(), this.theColorFromEntryBox.getBlue(), this.theColorFromEntryBox.getAlpha());

            this.color = theColorFromEntryBox;
            this.setCoordinatesByColor(this.color);
        } else {
            this.entryBox.string = new int[] {
                    255, 255, 255, 255
            };

            if (this.isUpdate) {
                this.master.setCanceledCloseGUI(false);
                this.isUpdate = false;
            }

            this.entryBox.setText(this.write(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha()));
        }

        this.entryBox.getScissor().set(this.rect.getX() < this.frame.getRect().getX() ? this.frame.getRect().getX() : this.rect.getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), (this.rect.getX() + this.rect.getWidth() >= this.master.getClosedWidth() ? this.master.getClosedWidth() - (this.rect.getX() + this.rect.getWidth()) : this.rect.getWidth()), this.frame.getRect().getHeight() - this.frame.getOffsetHeight());
        this.entryBox.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.entryBox.setOffsetY(3f);
        this.entryBox.setRendering(true);
        this.entryBox.setIsShadow(false);
        this.entryBox.setToDrawOutline(false);
        this.entryBox.setFont(this.master.fontWidgetModule);

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.alphaAnimationPressed);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);

            if (!this.isOpen()) {
                this.master.refreshDescriptionViewer(this.setting.getDescription());
            }
        }

        if (this.isOpen() && this.module.isWidgetOpened()) {
            if (selecting) {
                colorWidth = (this.master.getMouse().getX() - (this.rectColorChooser.getX())) / (this.rectColorChooser.getWidth());
                colorHeight = (this.master.getMouse().getY() - (this.rectColorChooser.getY())) / (this.rectColorChooser.getHeight());
            }

            if (hueSelecting) {
                hue = (this.master.getMouse().getY() - (this.rectColorHUE.getY())) / (this.rectColorHUE.getHeight());

                Color theNewestColor = getColorFromCoordinates();

                getSetting().setR(theNewestColor.getRed());
                getSetting().setG(theNewestColor.getGreen());
                getSetting().setB(theNewestColor.getBlue());
                getSetting().setA(theNewestColor.getAlpha());
            }

            if (alphaSelecting) {
                alpha = (this.master.getMouse().getY() - (this.alphaSlider.getRect().getY())) / (this.alphaSlider.getRect().getHeight());

                Color theNewestColor = getColorFromCoordinates();

                getSetting().setR(theNewestColor.getRed());
                getSetting().setG(theNewestColor.getGreen());
                getSetting().setB(theNewestColor.getBlue());
                getSetting().setA(theNewestColor.getAlpha());
            }

            if (colorWidth > 1.0f) colorWidth = 1.0f;
            if (colorHeight > 1.0f) colorHeight = 1.0f;
            if (colorWidth < 0) colorWidth = 0;
            if (colorHeight < 0) colorHeight = 0;
            if (hue > 1.0f) hue = 1.0f;
            if (hue < 0.0f) hue = 0.0f;
            if (alpha > 1.0f) alpha = 1.0f;
            if (alpha < 0.0f) alpha = 0.0f;

            if (this.isStarted) {
                Color theNewestColor = getColorFromCoordinates();

                getSetting().setR(theNewestColor.getRed());
                getSetting().setG(theNewestColor.getGreen());
                getSetting().setB(theNewestColor.getBlue());
                getSetting().setA(theNewestColor.getAlpha());
            }

            this.drawSquareColorPicker(this.rectColorChooser.getX(), this.rectColorChooser.getY(), this.rectColorChooser.getWidth(), this.rectColorChooser.getHeight(), this.rectColorChooser.getX() + colorWidth * (this.rectColorChooser.getWidth()), this.rectColorChooser.getY() + (colorHeight * this.rectColorChooser.getHeight()), 1.0f, Color.getHSBColor(hue, 1.0f, 1.0f));
            this.drawAlphaSlider(this.alphaSlider.getRect().getX(), this.alphaSlider.getRect().getY(), this.alphaSlider.getRect().getWidth(), this.alphaSlider.getRect().getHeight(), alpha);
            this.drawHueSliderBetter(this.rectColorHUE.getX(), this.rectColorHUE.getY(), this.rectColorHUE.getWidth(), this.rectColorHUE.getHeight(), hue);

            GL11.glEnable(GL11.GL_BLEND);

            this.entryBox.onRender();

            TurokShaderGL.drawScissor(this.frame.getRect().getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), (this.frame.getRect().getX() + this.frame.getRect().getWidth() >= this.master.getClosedWidth() ? this.master.getClosedWidth() - (this.frame.getRect().getX() + this.frame.getRect().getWidth()) : this.frame.getRect().getWidth()), this.frame.getRect().getHeight() - this.frame.getOffsetHeight());
            TurokFontManager.render(this.master.fontWidgetModule, "RGB: ", this.rect.getX() + 2f, this.entryBox.getRect().getY() + 3f, true, new Color(255, 255, 255, 255));
        }

        // Now you understand what are the 1f? Nigga?
        float theOffsetThatDivideWidgetByWidget = 1f;

        this.openedHeight = this.rectClick.getHeight() + theOffsetThatDivideWidgetByWidget + this.rectColorChooser.getHeight() + theOffsetThatDivideWidgetByWidget + this.entryBox.getRect().getHeight() + theOffsetThatDivideWidgetByWidget;
        this.closedHeight = this.rectClick.getHeight();

        TurokRenderGL.color(this.setting.getColor());
        TurokRenderGL.drawSolidRect(this.rectColorInfo);

        /*
         * Render module name.
         */
        TurokFontManager.render(this.master.fontWidgetModule, this.rect.getTag(), this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    public void setCoordinatesByColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        this.alpha = color.getAlpha() / 255f;
        this.hue = hsb[0];
        this.colorWidth = hsb[1];
        this.colorHeight = 1f - hsb[2];
    }

    public String write(int r, int g, int b, int a) {
        int red = TurokMath.clamp(r, 0, 255);
        int green = TurokMath.clamp(g, 0, 255);
        int blue = TurokMath.clamp(b, 0, 255);
        int alpha = TurokMath.clamp(a, 0, 255);

        return red + " " + green + " " + blue + " " + alpha;
    }

    public Color read(String format, Color last) {
        int count = 0;

        int red = last.getRed();
        int green = last.getGreen();
        int blue = last.getBlue();
        int alpha = last.getAlpha();

        for (String modules : format.split(" ")) {
            if (modules.equals("")) {
                continue;
            }

            int v = -1;

            try {
                v = Integer.parseInt(modules);
            } catch (NumberFormatException exc) {}

            if (v != -1) {
                if (count == 0) {
                    red = v;
                } else if (count == 1) {
                    green = v;
                } else if (count == 2) {
                    blue = v;
                } else if (count == 3) {
                    alpha = v;
                }
            }

            count++;
        }

        return new Color(TurokMath.clamp(red, 0, 255), TurokMath.clamp(green, 0, 255), TurokMath.clamp(blue, 0, 255), TurokMath.clamp(alpha, 0, 255));
    }

    public void drawSquareColorPicker(float x, float y, float width, float height, float circleX, float circleY, float radius, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0d).color(255, 255, 255, 255).endVertex();
        builder.pos(x, y + height, 0.0d).color(255, 255, 255, 255).endVertex();
        builder.pos(x + width, y + height, 0.0d).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        builder.pos(x + width, y, 0.0d).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        tessellator.draw();

        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0d).color(0, 0, 0, 0).endVertex();
        builder.pos(x, y + height, 0.0d).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width, y + height, 0.0d).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width, y, 0.0d).color(0, 0, 0, 0).endVertex();
        tessellator.draw();

        drawCircle(circleX, circleY, radius, Color.BLACK);

        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public void drawCompleteImage(float posX, float posY, float width, float height, ResourceLocation location) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(width, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(width, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawAlphaSlider(float x, float y, float width, float height, float progress) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("onepop/", ALPHA_SLIDER);
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawCompleteImage(x, y, width, height, location);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0d).color(255, 255, 255, 0).endVertex();
        builder.pos(x, y + height, 0.0d).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width, y + height, 0.0d).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width, y, 0.0d).color(255, 255, 255, 0).endVertex();
        tessellator.draw();

        this.drawRectangle(x, y + (height * progress), width, 1.0f, 1.0f, Color.GRAY);

        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public void drawHueSliderBetter(float x, float y, float width, float height, float progress) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        Color color1 = new Color(255, 0, 0, 255); // 0.0
        Color color2 = new Color(255, 255, 0, 255); // 0.1666
        Color color3 = new Color(0, 255, 0, 255); // 0.3333
        Color color4 = new Color(0, 255, 255, 255); // 0.5
        Color color5 = new Color(0, 0, 255, 255); // 0.6666
        Color color6 = new Color(255, 0, 255, 255); // 0.8333
        float offset = height / 6;

        GL11.glColor4f(color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, color1.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y);
        GL11.glColor4f(color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, color2.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + offset);
        GL11.glColor4f(color3.getRed() / 255f, color3.getGreen() / 255f, color3.getBlue() / 255f, color3.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + offset * 2);
        GL11.glColor4f(color4.getRed() / 255f, color4.getGreen() / 255f, color4.getBlue() / 255f, color4.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + offset * 3);
        GL11.glColor4f(color5.getRed() / 255f, color5.getGreen() / 255f, color5.getBlue() / 255f, color5.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + offset * 4);
        GL11.glColor4f(color6.getRed() / 255f, color6.getGreen() / 255f, color6.getBlue() / 255f, color6.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + offset * 5);
        GL11.glColor4f(color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, color1.getAlpha() / 255f);
        GL11.glVertex2f(x + width / 2, y + height);
        GL11.glEnd();

        drawRectangle(x, y + (height * progress), width, 2, 1.0f, Color.BLACK);

        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    public void drawRectangle(float x, float y, float width, float height, float lineWidth, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GlStateManager.glLineWidth(lineWidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION);
        builder.pos(x, y, 0.0d).endVertex();
        builder.pos(x, y + height, 0.0d).endVertex();
        builder.pos(x + width, y + height, 0.0d).endVertex();
        builder.pos(x + width, y, 0.0d).endVertex();
        tessellator.draw();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public void drawCircle(float x, float y, float radius, Color color) {
        double ps;
        double cs;
        double i;
        double[] outer;
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.001f);
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glBegin(9);
        for (i = 0.0; i < 36.0; i += 1.0) {
            cs = i * 10.0 * 3.141592653589793 / 180.0;
            ps = (i * 10.0 - 1.0) * 3.141592653589793 / 180.0;
            outer = new double[]{Math.cos(cs) * (double) radius, (-Math.sin(cs)) * (double) radius, Math.cos(ps) * (double) radius, (-Math.sin(ps)) * (double) radius};
            GL11.glVertex2d((double) x + outer[0], (double) y + outer[1]);
        }
        GL11.glEnd();
        GL11.glEnable(2848);
        GL11.glBegin(3);
        for (i = 0.0; i < 37.0; i += 1.0) {
            cs = i * 10.0 * 3.141592653589793 / 180.0;
            ps = (i * 10.0 - 1.0) * 3.141592653589793 / 180.0;
            outer = new double[]{Math.cos(cs) * (double) radius, (-Math.sin(cs)) * (double) radius, Math.cos(ps) * (double) radius, (-Math.sin(ps)) * (double) radius};
            GL11.glVertex2d((double) x + outer[0], (double) y + outer[1]);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GlStateManager.resetColor();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.popMatrix();
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        this.entryBox.setMouseOver(this.entryBox.rect.collideWithMouse(this.master.getMouse()) && this.flagMouse != Flag.MOUSE_NOT_OVER);
    }

    public Color getColorFromCoordinates() {
        float saturation = colorWidth;
        float brightness = 1.0f - colorHeight;

        this.setting.setSaturation(saturation);
        this.setting.setBrightness(brightness);

        Color color1 = Color.getHSBColor(hue, saturation, brightness);
        return new Color(MathHelper.clamp(color1.getRed(), 0, 255), MathHelper.clamp(color1.getGreen(), 0, 255), MathHelper.clamp(color1.getBlue(), 0, 255), (int) MathHelper.clamp(alpha * 255, 0, 255));
    }
}
