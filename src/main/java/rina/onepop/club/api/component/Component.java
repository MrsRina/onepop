package rina.onepop.club.api.component;

import com.google.gson.*;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.util.TurokClass;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.client.module.client.ModuleHUD;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 30/11/20 at 8:32pm
 */
public class Component {
    private String name, description;

    public TurokRect rect;

    public int offsetX;
    public int offsetY;

    private boolean isEnabled;
    private boolean isDragging;

    private ArrayList<ComponentSetting> settingList;

    private int[] colorRGB = {0, 0, 0};
    private int[] colorHUD = {0, 0, 0};

    public Dock dock;
    public StringType type;

    public ComponentSetting<Boolean> customFont;
    public ComponentSetting<Boolean> shadowFont;
    public ComponentSetting<ColorMode> colorMode;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public Component(String name, String tag, String description, StringType type) {
        this.name = name;
        this.description = description;

        this.rect = new TurokRect(tag, 0, 0);

        this.dock = Dock.TOP_LEFT;
        this.type = type;

        if (this.type == StringType.USE) {
            this.registry(this.customFont = new ComponentSetting<>("Custom Font", "CustomFont", "Enable smooth font render.", false));
            this.registry(this.shadowFont = new ComponentSetting<>("Shadow Font", "ShadowFont", "Render shadow effect in font.", true));
            this.registry(this.colorMode = new ComponentSetting<>("Color Mode", "ColorMode", "Color modes", ColorMode.HUD));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTag(String tag) {
        this.rect.setTag(tag);
    }

    public String getTag() {
        return rect.getTag();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public ArrayList<ComponentSetting> getSettingList() {
        return settingList;
    }

    public void setSettingList(ArrayList<ComponentSetting> settingList) {
        this.settingList = settingList;
    }

    public void setRect(TurokRect rect) {
        this.rect = rect;
    }

    public TurokRect getRect() {
        return rect;
    }

    public void setDock(Dock dock) {
        this.dock = dock;
    }

    public Dock getDock() {
        return dock;
    }

    public void setType(StringType type) {
        this.type = type;
    }

    public StringType getType() {
        return type;
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

    /*
     * Tools.
     */
    public void registry(ComponentSetting<?> setting) {
        if (this.settingList == null) {
            this.settingList = new ArrayList<>();
        }

        this.settingList.add(setting);
    }

    public ComponentSetting<?> get(Class clazz) {
        for (ComponentSetting<?> settings : this.settingList) {
            if (settings.getClass() == clazz) {
                return settings;
            }
        }

        return null;
    }

    public ComponentSetting<?> get(String tag) {
        for (ComponentSetting<?> settings : this.settingList) {
            if (settings.getTag().equalsIgnoreCase(tag)) {
                return settings;
            }
        }

        return null;
    }

    public void render(int x, int y, float w, float h, Color color) {
        int realX = (int) (this.rect.getX() + this.offsetX);
        int realY = (int) (this.rect.getY() + this.offsetY);

        Gui.drawRect(realX + verifyDock(x, w), realY + y, (int) (realX + verifyDock(x, w) + w), (int) (realY + y + h), color.getRGB());
    }

    public void render(String string, float x, float y) {
        int factor = 101 - ModuleHUD.settingSpeedHUE.getValue().intValue();

        if (this.type == StringType.USE) {
            int realX = (int) (this.rect.getX() + this.offsetX);
            int realY = (int) (this.rect.getY() + this.offsetY);

            Color color = new Color(0, 0, 0);

            if (this.colorMode.getValue() == ColorMode.RGB) {
                color = new Color(this.colorRGB[0], this.colorRGB[1], this.colorRGB[2]);
            } else if (this.colorMode.getValue() == ColorMode.HUD) {
                color = new Color(this.colorHUD[0], this.colorHUD[1], this.colorHUD[2]);
            }

            if (customFont.getValue()) {
                if (this.colorMode.getValue() == ColorMode.HUE) {
                    TurokFontManager.render(Onepop.getComponentManager().font, string, realX + verifyDock(x, getStringWidth(string)), realY + y, shadowFont.getValue(), factor);
                } else {
                    TurokFontManager.render(Onepop.getComponentManager().font, string, realX + verifyDock(x, getStringWidth(string)), realY + y, shadowFont.getValue(), color);
                }
            } else {
                if (this.colorMode.getValue() == ColorMode.HUE) {
                    TurokFontManager.render(string, realX + verifyDock(x, getStringWidth(string)), realY + y, shadowFont.getValue(), factor);
                } else {
                    TurokFontManager.render(string, realX + verifyDock(x, getStringWidth(string)), realY + y, shadowFont.getValue(), color);
                }
            }
        }
    }

    public int getStringWidth(String string) {
        if (this.type == StringType.USE) {
            if (customFont.getValue()) {
                return TurokFontManager.getStringWidth(Onepop.getComponentManager().font, string);
            } else {
                return mc.fontRenderer.getStringWidth(string);
            }
        }

        return 0;
    }

    public int getStringHeight(String string) {
        if (this.type == StringType.USE) {
            if (customFont.getValue()) {
                return TurokFontManager.getStringHeight(Onepop.getComponentManager().font, string) + 2;
            } else {
                return mc.fontRenderer.FONT_HEIGHT;
            }
        }

        return 0;
    }

    public int verifyDock(float x, float w) {
        int position = 0;

        if (this.dock == Dock.TOP_LEFT) {
            position = (int) x;
        }

        if (this.dock == Dock.TOP_RIGHT) {
            position = (int) (this.rect.getWidth() - w - this.offsetX - x);
        }

        if (this.dock == Dock.BOTTOM_LEFT) {
            position = (int) x;
        }

        if (this.dock == Dock.BOTTOM_RIGHT) {
            position = (int) (this.rect.getWidth() - w - this.offsetX - x);
        }

        return position;
    }

    public void cornerDetector() {
        final TurokDisplay display = new TurokDisplay(mc);

        int diff = 0;

        if (this.rect.getX() <= diff) {
            if (this.dock == Dock.TOP_RIGHT) {
                this.dock = Dock.TOP_LEFT;
            } else if (this.dock == Dock.BOTTOM_RIGHT) {
                this.dock = Dock.BOTTOM_LEFT;
            }
        }

        if (this.rect.getY() <= diff) {
            if (this.dock == Dock.BOTTOM_LEFT) {
                this.dock = Dock.TOP_LEFT;
            } else if (this.dock == Dock.BOTTOM_RIGHT) {
                this.dock = Dock.TOP_RIGHT;
            }
        }

        if (this.rect.getX() + this.rect.getWidth() >= display.getScaledWidth() - diff) {
            if (this.dock == Dock.TOP_LEFT) {
                this.dock = Dock.TOP_RIGHT;
            } else if (this.dock == Dock.BOTTOM_LEFT) {
                this.dock = Dock.BOTTOM_RIGHT;
            }
        }

        if (this.rect.getY() + this.rect.getHeight() >= display.getScaledHeight() - diff) {
            if (this.dock == Dock.TOP_LEFT) {
                this.dock = Dock.BOTTOM_LEFT;
            } else if (this.dock == Dock.BOTTOM_RIGHT) {
                this.dock = Dock.BOTTOM_RIGHT;
            }
        }

        float dx = this.rect.getX();
        float dy = this.rect.getY();

        float w = this.rect.getWidth();
        float h = this.rect.getHeight();

        if (dx <= diff) dx = diff;
        if (dy <= diff) dy = diff;
        if (dx >= display.getScaledWidth() - w - diff) dx = display.getScaledWidth() - w - diff;
        if (dy >= display.getScaledHeight() - h - diff) dy = display.getScaledHeight() - h - diff;

        this.rect.setX(dx);
        this.rect.setY(dy);
    }

    public void onSave() {
        try {
            String pathFolder = Onepop.PATH_CONFIG + "HUD/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(Paths.get(pathFolder))) {
                Files.createDirectories(Paths.get(pathFolder));
            }

            if (Files.exists(Paths.get(pathFile))) {
                java.io.File file = new java.io.File(pathFile);
                file.delete();
            }

            Files.createFile(Paths.get(pathFile));

            JsonObject mainJson = new JsonObject();

            mainJson.add("enabled", new JsonPrimitive(this.isEnabled));
            mainJson.add("x", new JsonPrimitive(this.rect.getX()));
            mainJson.add("y", new JsonPrimitive(this.rect.getY()));

            JsonObject jsonSettingList = new JsonObject();

            for (ComponentSetting<?> settings : this.settingList) {
                if (settings.getValue() instanceof Boolean) {
                    ComponentSetting<Boolean> componentSetting = (ComponentSetting<Boolean>) settings;

                    jsonSettingList.add(settings.getTag(), new JsonPrimitive(componentSetting.getValue()));
                }

                if (settings.getValue() instanceof Number) {
                    ComponentSetting<Number> componentSetting = (ComponentSetting<Number>) settings;

                    jsonSettingList.add(settings.getTag(), new JsonPrimitive(componentSetting.getValue()));
                }

                if (settings.getValue() instanceof Enum) {
                    ComponentSetting<Enum> componentSetting = (ComponentSetting<Enum>) settings;

                    jsonSettingList.add(settings.getTag(), new JsonPrimitive(componentSetting.getValue().name()));
                }
            }

            mainJson.add("settings", jsonSettingList);

            String stringJson = gsonBuilder.toJson(new JsonParser().parse(mainJson.toString()));

            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(pathFile), "UTF-8");

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void onLoad() {
        try {
            String pathFolder = Onepop.PATH_CONFIG + "HUD/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            if (!Files.exists(Paths.get(pathFile))) {
                return;
            }

            InputStream file = Files.newInputStream(Paths.get(pathFile));

            JsonObject mainJson = new JsonParser().parse(new InputStreamReader(file)).getAsJsonObject();

            if(mainJson.get("enabled") != null) this.setEnabled(mainJson.get("enabled").getAsBoolean());
            if (mainJson.get("x") != null) this.rect.setX(mainJson.get("x").getAsInt());
            if (mainJson.get("y") != null) this.rect.setY(mainJson.get("y").getAsInt());

            if (mainJson.get("settings") != null) {
                JsonObject jsonSettingList = mainJson.get("settings").getAsJsonObject();

                for (ComponentSetting<?> settings : this.settingList) {
                    if (jsonSettingList.get(settings.getTag()) == null) {
                        continue;
                    }

                    if (settings.getValue() instanceof Boolean) {
                        ComponentSetting<Boolean> componentSetting = (ComponentSetting<Boolean>) settings;

                        componentSetting.setValue(jsonSettingList.get(settings.getTag()).getAsBoolean());
                    }

                    if (settings.getValue() instanceof Integer) {
                        ComponentSetting<Integer> componentSetting = (ComponentSetting<Integer>) settings;

                        componentSetting.setValue(jsonSettingList.get(settings.getTag()).getAsInt());
                    }

                    if (settings.getValue() instanceof Double) {
                        ComponentSetting<Double> componentSetting = (ComponentSetting<Double>) settings;

                        componentSetting.setValue(jsonSettingList.get(settings.getTag()).getAsDouble());
                    }

                    if (settings.getValue() instanceof Float) {
                        ComponentSetting<Float> componentSetting = (ComponentSetting<Float>) settings;

                        componentSetting.setValue(jsonSettingList.get(settings.getTag()).getAsFloat());
                    }

                    if (settings.getValue() instanceof Enum) {
                        ComponentSetting<Enum> componentSetting = (ComponentSetting<Enum>) settings;

                        componentSetting.setValue(TurokClass.getEnumByName(componentSetting.getValue(), jsonSettingList.get(settings.getTag()).getAsString()));
                    }
                }
            }

            file.close();
        } catch (IllegalStateException | IOException exc) {
            exc.printStackTrace();
        }
    }

    /*
     * Overrides.
     */
    public void onRender() {
        float partialTicks = mc.getRenderPartialTicks();

        float[] currentColor360 = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int cycleColor = Color.HSBtoRGB(currentColor360[0], 1, 1);

        this.colorRGB = new int[] {
            ((cycleColor >> 16) & 0xFF),
            ((cycleColor >> 8) & 0xFF),
            ((cycleColor) & 0xFF)
        };

        this.colorHUD = new int[] {
                ModuleHUD.settingColor.getR(),
                ModuleHUD.settingColor.getG(),
                ModuleHUD.settingColor.getB()
        };

        onRender(partialTicks);
    }

    public void onRender(float partialTicks) {}
}
