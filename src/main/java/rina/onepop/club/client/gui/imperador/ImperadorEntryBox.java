package rina.onepop.club.client.gui.imperador;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import me.rina.turok.util.TurokTick;
import net.minecraft.util.ChatAllowedCharacters;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.client.gui.imperador.util.ClipboardUtil;

import java.awt.*;

/**
 * @author SrRina
 * @since 16/08/2021 at 14:50
 **/
public class ImperadorEntryBox extends Widget {
    private String text;
    private String save;

    private int indexA;
    private int indexB;

    public int[] string = new int[] {255, 255, 255, 255};
    public int[] pressed = new int[] {255, 255, 255, 0};
    public int[] focused = new int[] {255, 255, 255, 255};
    public int[] outline = new int[] {255, 255, 255, 200};
    public int[] background = new int[] {0, 0, 0, 0};
    public int[] background_selected = new int[] {0, 0, 255, 100};

    public float pressedAlpha;
    public float lastTickPressedAlpha;

    public float focusedAlpha;
    public float lastTickFocusedAlpha;

    public float outlineAlpha;
    public float lastTickOutlineAlpha;

    private float kerning = 0f;
    private float partialTicks = 1f;

    private boolean isShadow;
    private boolean isRendering;

    private boolean isToDrawOutline;
    private boolean isFocused;

    private boolean isSplitRendering;
    private boolean isMouseOver;

    private boolean isPressed;
    private boolean isReleased;

    private boolean isDragging;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private float scroll;
    private TurokFont font;

    protected int lastIndexCurrent;
    protected float lastSize;

    private final TurokTick splitTick = new TurokTick();
    private final TurokTick pressTick = new TurokTick();

    private final TurokRect scissor = new TurokRect(0, 0);

    public ImperadorEntryBox(TurokFont font, String text) {
        super("Imperador:Entry:Box");

        this.font = font;
        this.text = text;

        this.setIsShadow(true);
    }

    public TurokRect getScissor() {
        return scissor;
    }

    public void setKerning(float kerning) {
        this.kerning = kerning;
    }

    public float getKerning() {
        return kerning;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setFont(TurokFont font) {
        this.font = font;
    }

    public TurokFont getFont() {
        return font;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        if (this.text.isEmpty()) {
            return true;
        }

        boolean empty = true;

        for (String c : this.text.split("")) {
            if (!c.equals(" ")) {
                empty = false;

                break;
            }
        }

        return empty;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getSave() {
        return save;
    }

    public void setMouseOver(boolean isMouseOver) {
        this.isMouseOver = isMouseOver;
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }

    public void setIsShadow(boolean isShadow) {
        this.isShadow = isShadow;
    }

    public boolean getIsShadow() {
        return isShadow;
    }

    public void setSplitRendering(boolean splitRendering) {
        this.isSplitRendering = splitRendering;
    }

    public boolean isSplitRendering() {
        return isSplitRendering;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void setToDrawOutline(boolean toDrawOutline) {
        isToDrawOutline = toDrawOutline;
    }

    public boolean isToDrawOutline() {
        return isToDrawOutline;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetW(float offsetW) {
        this.offsetW = offsetW;
    }

    public float getOffsetW() {
        return offsetW;
    }

    public void setOffsetH(float offsetH) {
        this.offsetH = offsetH;
    }

    public float getOffsetH() {
        return offsetH;
    }

    public void setIndexA(int indexA) {
        this.indexA = indexA;
    }

    public int getIndexA() {
        return indexA;
    }

    public void setIndexB(int indexB) {
        this.indexB = indexB;
    }

    public int getIndexB() {
        return indexB;
    }

    public float getLastSize() {
        return lastSize;
    }

    public void scissor() {
        this.scissor.copy(this.rect);
    }

    public void center() {
        this.offsetX = (this.rect.getWidth() / 2f) - (TurokFontManager.getStringWidth(this.getFont(), this.text) / 2f);
    }

    public void left(float difference) {
        this.offsetX = difference;
    }

    public void right(float difference) {
        this.offsetX = this.rect.getWidth() - TurokFontManager.getStringWidth(this.getFont(), this.text) - difference;
    }

    public int getIndexByPosition(float x) {
        int index = this.text.isEmpty() ? 0 : -1;
        int i = 0;

        float size = 0f;

        for (String c : this.text.split("")) {
            float w = TurokFontManager.getStringWidth(this.getFont(), c) / 2f;

            boolean k = x >= this.rect.getX() + this.offsetX + w && x <= this.rect.getX() + this.offsetX + w + size;
            boolean l = x >= this.rect.getX() + this.offsetX + (w * 2f) && x <= this.rect.getX() + this.offsetX + (w * 2f) + size;

            if (k) {
                index = i;

                break;
            }

            if (l) {
                index = (i + 1);

                break;
            }

            size += (w * 2f) + this.getKerning();
            i++;
        }

        return index;
    }

    public float getSizeByIndex(int index) {
        if (index == -1) {
            return 0;
        }

        int i = -1;
        float size = 0f;

        for (String c : this.text.split("")) {
            float w = TurokFontManager.getStringWidth(this.getFont(), c);

            if (i == index) {
                break;
            }

            size += w + this.getKerning();
            i++;
        }

        return size;
    }

    public void doMouseOver(TurokMouse mouse) {
        this.isMouseOver = this.rect.collideWithMouse(mouse) && this.isRendering();
    }

    public boolean isCollidingWithMouse(int mouseX, int mouseY, float s, float size) {
        return mouseX >= this.rect.getX() + this.offsetX + s && mouseX <= this.rect.getX() + this.offsetX + s + size;
    }

    public void doSetIndexAB(TurokMouse mouse) {
        if (!this.isMouseOver()) {
            return;
        }

        this.splitTick.reset();

        int index = this.getIndexByPosition(mouse.getX());

        if (index == -1) {
            if (this.text.isEmpty()) {
                index = 0;
            } else {
                float size = this.size() / 2f;

                if (mouse.getX() >= this.rect.getX() + this.offsetX + size) {
                    index = this.text.length();
                }
            }
        }

        this.offsetW = TurokMath.clamp(mouse.getX(), this.rect.getX() + this.offsetX, this.rect.getX() + this.rect.getWidth());
        this.offsetH = index;

        this.setIndexA(index);
        this.setIndexB(index);
    }

    public void doMouseScroll(TurokMouse mouse) {
        float m = (this.rect.getWidth() - this.offsetX) - TurokMath.min(this.lastSize, this.rect.getWidth());
        float x = TurokMath.clamp(mouse.getX(), this.rect.getX() + this.offsetX, this.rect.getX() + this.rect.getWidth());

        float size = this.offsetX + this.getSizeByIndex(this.indexA);

        if (this.offsetX + this.lastSize < this.rect.getWidth()) {
            this.scroll = this.offsetX;
        }

        if ((this.rect.getX() + this.offsetX + size <= this.rect.getX() + this.offsetX || this.rect.getX() + this.offsetX + size >= this.rect.getX() + this.rect.getWidth()) && !this.isDragging()) {
            float value = this.rect.getWidth() - TurokMath.min(size, this.rect.getWidth());

            this.scroll = value + (value >= 0 ? 2f : 0f);
        }

        if (this.scroll >= this.offsetX) {
            this.scroll = 1f;
        } else if (this.scroll <= m) {
            this.scroll = m;
        }

        this.offsetX = TurokMath.lerp(this.offsetX, this.scroll, this.getPartialTicks());

        if (!this.isDragging()) {
            this.lastIndexCurrent = -1;

            this.offsetW = x;
            this.offsetH = -1;

            return;
        }

        if (this.offsetH == -1) {
            return;
        }

        // If you did not know, the split is not rendered when you drag.
        this.splitTick.reset();

        int indexCurrent = this.getIndexByPosition(x);

        if (indexCurrent != -1) {
            this.lastIndexCurrent = indexCurrent;
        } else {
            if (x > this.rect.getX() + this.getOffsetX() + this.getSizeByIndex(this.lastIndexCurrent)) {
                this.lastIndexCurrent = this.text.length();
            } else {
                this.lastIndexCurrent = 0;
            }
        }

        // 250ms delay again. '// No more 250ms delay. '// Without the 250ms delay, the double click for select all never will works right.''
        if (this.pressTick.isPassedMS(250)) {
            if (this.lastIndexCurrent > this.offsetH) {
                this.indexA = (int) this.offsetH;
                this.indexB = this.lastIndexCurrent;
            } else {
                this.indexA = this.lastIndexCurrent;
                this.indexB = (int) this.offsetH;
            }
        }

        if (mouse.getX() >= this.rect.getX() + this.rect.getWidth() - 1f && this.lastSize + 2f >= this.rect.getWidth()) {
            final float speed = (this.rect.getX() + this.rect.getWidth() - 1f) - mouse.getX();

            this.scroll -= TurokMath.sqrt(speed * speed);
        }

        if (mouse.getX() <= this.rect.getX() + 1f) {
            final float speed = this.rect.getX() - mouse.getX();

            this.scroll += TurokMath.sqrt(speed * speed);
        }
    }

    public float size() {
        if (this.text.isEmpty()) {
            return -1f;
        }

        float s = 0f;

        for (int i = 0; i < this.text.length(); i++) {
            final String c = CharUtils.toString(this.text.charAt(i));
            final float w = TurokFontManager.getStringWidth(this.getFont(), c);

            s += w + this.getKerning();
        }

        return s;
    }

    public void split(float k) {
        if (!this.splitTick.isPassedMS(500)) {
            this.setSplitRendering(true);
        }

        if (this.splitTick.isPassedMS(500)) {
            this.setSplitRendering(false);
        }

        if (this.splitTick.isPassedMS(1000)) {
            this.splitTick.reset();
        }

        if (this.isSplitRendering()) {
            TurokGL.color(TurokGL.arrayColorToColorClass(this.string));
            TurokRenderGL.drawSolidRect(this.rect.getX() + this.offsetX + k, this.rect.getY() + 1f, 1f, this.rect.getHeight() - 2f);
        }
    }

    @Override
    public void onScreenClosed() {

    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onScreenOpened() {

    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char character, int key) {
        try {
            if (this.isFocused()) {
                String cache = this.text;
                int a = this.indexA;

                switch (key) {
                    case Keyboard.KEY_ESCAPE: {
                        this.setFocused(false);

                        break;
                    }

                    case Keyboard.KEY_RETURN: {
                        this.setFocused(false);

                        break;
                    }

                    case Keyboard.KEY_LEFT: {
                        a--;

                        break;
                    }

                    case Keyboard.KEY_RIGHT: {
                        a++;

                        break;
                    }

                    case Keyboard.KEY_BACK: {
                        if (this.getIndexA() != this.getIndexB()) {
                            String affectedBySubString = cache.substring(0, this.indexA) + cache.substring(this.indexB, this.text.length());;

                            cache = affectedBySubString;

                            this.indexB = this.indexA;

                            break;
                        }

                        String memory;
                        int skip = 0;

                        if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                            memory = cache.substring(0, this.indexA);

                            if (memory.length() != 0) {
                                boolean foundFirstSpace = false;
                                boolean foundLetter = false;

                                int index = memory.length() - 1;

                                for (int i = 0; i < memory.length(); i++) {
                                    index = memory.length() - 1 - i;

                                    String c = CharUtils.toString(memory.charAt(index));

                                    if (!c.equals(" ")) {
                                        foundLetter = true;
                                    }

                                    if (c.equals(" ")) {
                                        if (foundLetter) {
                                            break;
                                        }

                                        if (foundFirstSpace) {
                                            break;
                                        }

                                        foundFirstSpace = true;
                                    }
                                }

                                for (String c : memory.substring(index, memory.length()).split("")) {
                                    skip++;
                                }

                                memory = memory.substring(0, index);
                            }
                        } else {
                            memory = StringUtils.chop(cache.substring(0, this.indexA));
                            skip = 1;
                        }

                        cache = memory + cache.substring(this.indexA, this.text.length());
                        a = a - skip;

                        break;
                    }

                    case Keyboard.KEY_DELETE: {
                        if (this.getIndexA() != this.getIndexB()) {
                            String affectedBySubString = cache.substring(0, this.indexA) + cache.substring(this.indexB, this.text.length());

                            cache = affectedBySubString;

                            this.indexB = this.indexA;

                            break;
                        }

                        String memory = cache.substring(this.indexA, this.text.length());

                        if (memory.length() != 0) {
                            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                                int i = 0;

                                boolean foundFirstSpace = false;
                                boolean foundLetter = false;

                                for (String s : memory.split("")) {
                                    if (!s.equals(" ")) {
                                        foundLetter = true;
                                    }

                                    if (s.equals(" ")) {
                                        if (foundLetter) {
                                            break;
                                        }

                                        if (foundFirstSpace) {
                                            break;
                                        }

                                        foundFirstSpace = true;
                                    }

                                    i++;
                                }

                                memory = memory.substring(i, memory.length());
                            } else {
                                memory = memory.substring(1);
                            }

                            cache = cache.substring(0, this.indexA) + memory;
                        }

                        break;
                    }

                    default: {
                        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                            if (key == Keyboard.KEY_A) {
                                this.setIndexA(0);
                                this.setIndexB(cache.length());

                                a = 0;
                            }

                            final String copy = ClipboardUtil.get();

                            if (key == Keyboard.KEY_V && copy != null) {
                                for (String i : copy.split("")) {
                                    a++;
                                }

                                if (this.getIndexA() != this.getIndexB()) {
                                    cache = cache.substring(0, this.indexA) + cache.substring(this.indexB,  cache.length());

                                    this.indexB = this.indexA;
                                }

                                cache = cache.substring(0, this.indexA) + copy + cache.substring(this.indexA, cache.length());
                            }

                            if (key == Keyboard.KEY_C && this.getIndexA() != this.getIndexB()) {
                                ClipboardUtil.set(cache.substring(this.indexA, this.indexB));
                            }

                            if (key == Keyboard.KEY_X && this.getIndexA() != this.getIndexB()) {
                                ClipboardUtil.set(cache.substring(this.indexA, this.indexB));

                                cache = cache.substring(0, this.indexA) + cache.substring(this.indexB, this.text.length());

                                this.indexB = this.indexA;
                            }

                            break;
                        }

                        if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                            if (this.getIndexA() != this.getIndexB()) {
                                cache = cache.substring(0, this.indexA) + cache.substring(this.indexB, cache.length());

                                this.indexB = this.indexA;
                            }

                            cache = cache.substring(0, this.indexA) + CharUtils.toString(character) + cache.substring(this.indexA, cache.length());
                            a++;
                        }

                        break;
                    }
                }

                if (!cache.equals(this.text)) {
                    this.text = cache;
                    this.splitTick.reset();
                }

                if (a != this.indexA) {
                    this.indexA = TurokMath.clamp(a, 0, this.text.length());
                    this.indexB = this.indexA;

                    this.splitTick.reset();
                }
            }
        } catch (StringIndexOutOfBoundsException exc) {
            // Actually, THE CODE RUN VERY CLEAN NO PROBLEMS, BUT FOR SOME REASON, ITS EXPLODE!
        }
    }

    @Override
    public void onCustomKeyboardPressed(char character, int key) {

    }

    @Override
    public void onMouseReleased(int button) {
        if (!this.isRendering()) {
            return;
        }

        if (this.isPressed()) {
            this.setReleased(this.isMouseOver());

            this.setDragging(false);
            this.setPressed(false);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (!this.isRendering()) {
            return;
        }

        if (!this.isMouseOver() && this.isFocused()) {
            this.setFocused(false);
        }

        if (this.isMouseOver() && (button == 0 || button == 1)) {
            this.splitTick.reset();
            this.setFocused(this.isMouseOver());

            if (this.pressTick.isPassedMS(500)) {
                this.pressTick.reset();
            } else {
                this.setIndexA(0);
                this.setIndexB(this.text.length());
            }

            this.setPressed(true);
            this.setDragging(true);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        if (!this.isRendering()) {
            return;
        }

        this.focusedAlpha = this.isFocused() ? this.focused[3] : 0f;
        this.pressedAlpha = this.isPressed() ? this.pressed[3] : 0f;
        this.outlineAlpha = this.outline[3];

        this.lastTickFocusedAlpha = TurokMath.lerp(this.lastTickFocusedAlpha, this.focusedAlpha, this.getPartialTicks());
        this.lastTickPressedAlpha = TurokMath.lerp(this.lastTickPressedAlpha, this.pressedAlpha, this.getPartialTicks());
        this.lastTickOutlineAlpha = TurokMath.lerp(this.lastTickOutlineAlpha, this.outlineAlpha, this.getPartialTicks());

        final boolean flag = !GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        TurokGL.color(TurokGL.arrayColorToColorClass(this.background));
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.lastTickOutlineAlpha != 0f && this.isToDrawOutline()) {
            TurokGL.color(this.outline[0], this.outline[1], this.outline[2], this.lastTickOutlineAlpha);
            TurokRenderGL.drawOutlineRect(this.rect);
        }

        if (this.lastTickFocusedAlpha != 0f) {
            TurokGL.color(this.focused[0], this.focused[1], this.focused[2], this.lastTickFocusedAlpha);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        if (this.lastTickPressedAlpha != 0f) {
            TurokGL.color(this.pressed[0], this.pressed[1], this.pressed[2], this.lastTickPressedAlpha);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        if (flag) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }

        if (!this.isFocused()) {
            this.save = this.text;
        }

        if (this.isFocused()) {
            this.lastSize = (int) this.size();
        }

        final Color color = TurokGL.arrayColorToColorClass(this.string);

        TurokShaderGL.drawScissor(this.scissor);

        float l = 0;
        float k = 0;

        int tl = this.text.isEmpty() ? -1 : this.text.length();

        int i = 0;
        int j = 0;

        for (String c : this.text.split("")) {
            j++;

            if (tl == -1) {
                if (this.isFocused()) this.split(0f);

                continue;
            }

            float w = TurokFontManager.getStringWidth(this.getFont(), c);
            boolean r = true;

            if (j > this.getIndexA() && j <= this.getIndexB() && this.getIndexB() != this.getIndexA() && this.isFocused()) {
                TurokGL.color(TurokGL.arrayColorToColorClass(this.background_selected));
                TurokRenderGL.drawSolidRect(this.rect.getX() + this.offsetX + k, this.rect.getY() + this.offsetY / 2, w + this.getKerning(), this.rect.getHeight() - (this.offsetY / 2 * 2));

                l = k + w + this.getKerning();
                r = false;
            }

            TurokFontManager.render(this.getFont(), c, this.rect.getX() + this.offsetX + k, this.rect.getY() + this.offsetY, this.getIsShadow(), color);

            if (r && i == this.getIndexA() && this.isFocused()) {
                this.split(k);
            }

            k += w + this.getKerning();
            i++;
        }

        if (this.indexA == tl && this.isFocused()) {
            this.split(k);
        }

        if (flag) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public void onCustomRender() {

    }
}