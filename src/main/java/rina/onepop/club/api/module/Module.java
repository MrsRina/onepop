package rina.onepop.club.api.module;

import com.google.gson.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.rina.turok.util.TurokClass;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.api.setting.Setting;
import rina.onepop.club.api.setting.value.*;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.client.event.client.ModuleStatusEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 15/11/20 at 4:51pm
 */
public class Module implements ISLClass {
    private String name = getRegistry().name();
    private String tag = getRegistry().tag();

    private String description = getRegistry().description();
    private ModuleCategory category = getRegistry().category();

    private String status;
    private ArrayList<Setting> settingList;

    private ValueBind settingKeyBinding = new ValueBind("", "KeyBind", "Key bind to active or disable module.", -1);

    public ValueBoolean settingWhilePressed = new ValueBoolean("While Pressed", "WhilePressed", "Only enable if key module is pressed.", false);
    public ValueBoolean settingArrayList = new ValueBoolean("ArrayList", "ArrayList", "Enable on array list.", true);
    public ValueBoolean settingStatus = new ValueBoolean("Status", "Status", "Show status on array list.", true);
    public ValueBoolean settingToggleMessage = new ValueBoolean("Toggle Message", "ToggleMessage", "Alert if is toggled.", true);

    /*
     * Frustum camera for render in 3D space.
     */
    public ICamera camera = new Frustum();

    public Module() {
        // We need registry the currents pre settings at own Module class.
        this.registry(this.settingKeyBinding);
        this.registry(this.settingWhilePressed);
        this.registry(this.settingArrayList);
        this.registry(this.settingStatus);
        this.registry(this.settingToggleMessage);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.settingStatus.getValue() ? status : null;
    }

    public void setCategory(ModuleCategory category) {
        this.category = category;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public void setEnabled(boolean enabled) {
        ModuleStatusEvent event = new ModuleStatusEvent(this, enabled);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (!event.isCanceled()) {
            if (this.settingKeyBinding.getValue() != enabled) {
                this.settingKeyBinding.setValue(enabled);

                this.onReload();
            }
        }
    }

    public boolean isEnabled() {
        return settingKeyBinding.getValue();
    }

    public void setKeyCode(int key) {
        this.settingKeyBinding.setKeyCode(key);
    }

    public int getKeyCode() {
        return settingKeyBinding.getKeyCode();
    }

    public void setSettingList(ArrayList<Setting> settingList) {
        this.settingList = settingList;
    }

    public ArrayList<Setting> getSettingList() {
        return settingList;
    }

    public Registry getRegistry() {
        Registry details = null;

        if (getClass().isAnnotationPresent(Registry.class)) {
            details = getClass().getAnnotation(Registry.class);
        }

        return details;
    }

    public void registry(Setting setting) {
        if (this.settingList == null) {
            this.settingList = new ArrayList<>();
        }

        this.settingList.add(setting);
    }

    public void unregister(Setting setting) {
        if (this.settingList == null) {
            this.settingList = new ArrayList<>();
        } else {
            if (this.get(setting.getClass()) != null) {
                this.settingList.remove(setting);
            }
        }
    }

    public Setting get(Class<?> clazz) {
        for (Setting settings : this.settingList) {
            if (settings.getClass() == clazz) {
                return settings;
            }
        }

        return null;
    }

    public Setting get(String tag) {
        for (Setting settings : this.settingList) {
            if (settings.getTag().equalsIgnoreCase(tag)) {
                return settings;
            }
        }

        return null;
    }

    public void onInput(int keyCode, InputType inputType) {
        for (Setting settings : this.settingList) {
            if (settings instanceof ValueBind) {
                ValueBind settingValueBind = (ValueBind) settings;

                if (settingValueBind.getTag().equalsIgnoreCase(this.settingKeyBinding.getTag()) && keyCode == settingValueBind.getKeyCode() && settingValueBind.getInputType() == inputType) {
                    if (this.settingWhilePressed.getValue()) {
                        boolean flag = ((inputType == InputType.MOUSE && Mouse.isButtonDown(keyCode)) || (inputType == InputType.KEYBOARD && Keyboard.isKeyDown(keyCode)));

                        this.setEnabled(flag);
                    } else {
                        if (settingValueBind.isEnabled() && settingValueBind.getInputType() == inputType && settingValueBind.getKeyCode() == keyCode) {
                            if ((inputType == InputType.MOUSE && Mouse.getEventButtonState()) || (inputType == InputType.KEYBOARD && Keyboard.getEventKeyState())) {
                                this.toggle();
                            }
                        }
                    }
                } else {
                    if (settingValueBind.isEnabled() && settingValueBind.getInputType() == inputType && settingValueBind.getKeyCode() == keyCode) {
                        if ((inputType == InputType.MOUSE && Mouse.getEventButtonState()) || (inputType == InputType.KEYBOARD && Keyboard.getEventKeyState())) {
                            settingValueBind.setValue(!settingValueBind.getValue());
                        }
                    }
                }
            }
        }
    }

    public boolean shouldRenderOnArrayList() {
        return this.settingArrayList.getValue();
    }

    public void toggle() {
        this.setEnabled(!this.settingKeyBinding.getValue());
    }

    public void onReload() {
        if (this.settingKeyBinding.getValue()) {
            this.setEnabled();
        } else {
            this.setDisabled();
        }
    }

    public void setEnabled() {
        this.settingKeyBinding.setValue(true);

        if (settingToggleMessage.getValue()) {
            ChatUtil.refreshPrint(ChatFormatting.GRAY + this.name + " " + ChatFormatting.GREEN + "Enabled");
        }

        this.onEnable();

        Onepop.getPomeloEventManager().addEventListener(this);
    }

    public void setDisabled() {
        this.settingKeyBinding.setValue(false);

        if (settingToggleMessage.getValue()) {
            ChatUtil.refreshPrint(ChatFormatting.GRAY + this.name + " " + ChatFormatting.RED + "Disabled");
        }

        this.onDisable();

        Onepop.getPomeloEventManager().removeEventListener(this);
    }

    public void status(String status) {
        this.status = status;
    }

    public void print(String message) {
        ChatUtil.print(ChatFormatting.GRAY + this.name + " " + ChatFormatting.WHITE + message);
    }

    public void onEnable() {}
    public void onDisable() {}

    public void onRender2D() {}
    public void onRender3D() {}

    public void onShutdown() {}

    /**
     * Handler settings in GUI at module.
     */
    public void onSetting() {

    }

    public void onSync() {
        this.settingStatus.setEnabled(this.settingArrayList.getValue());
    }

    @Override
    public void onSave() {
        try {
            String pathFolder = PresetManager.getPolicyProtectionValue() + "/module/" + this.category.name().toLowerCase() + "/";
            String pathFile = pathFolder + this.tag + ".json";

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jsonParser = new JsonParser();

            if (Files.exists(Paths.get(pathFolder)) == false) {
                Files.createDirectories(Paths.get(pathFolder));
            }

            if (Files.exists(Paths.get(pathFile))) {
                java.io.File file = new java.io.File(pathFile);
                file.delete();
            }

            Files.createFile(Paths.get(pathFile));

            JsonObject mainJson = new JsonObject();
            JsonObject jsonSettingList = new JsonObject();

            for (Setting settings : this.settingList) {
                if (settings instanceof ValueBoolean) {
                    ValueBoolean settingValueBoolean = (ValueBoolean) settings;

                    jsonSettingList.add(settingValueBoolean.getTag(), new JsonPrimitive(settingValueBoolean.getValue()));
                }

                if (settings instanceof ValueNumber) {
                    ValueNumber settingValueNumber = (ValueNumber) settings;

                    jsonSettingList.add(settingValueNumber.getTag(), new JsonPrimitive(settingValueNumber.getValue()));
                }

                if (settings instanceof ValueEnum) {
                    ValueEnum settingValueEnum = (ValueEnum) settings;

                    jsonSettingList.add(settingValueEnum.getTag(), new JsonPrimitive(settingValueEnum.getValue().name()));
                }

                if (settings instanceof ValueString) {
                    ValueString settingValueString = (ValueString) settings;

                    jsonSettingList.add(settingValueString.getTag(), new JsonPrimitive(settingValueString.getValue()));
                }

                if (settings instanceof ValueBind) {
                    ValueBind settingValueBind = (ValueBind) settings;

                    // We create a object json to save key and state.
                    JsonObject valueBindObject = new JsonObject();

                    // Convert to a string.
                    String stringKey = settingValueBind.getKeyCode() != -1 ? Keyboard.getKeyName(settingValueBind.getKeyCode()) : "NONE";

                    valueBindObject.add("key", new JsonPrimitive(stringKey));
                    valueBindObject.add("state", new JsonPrimitive(settingValueBind.getValue()));
                    valueBindObject.add("type", new JsonPrimitive(settingValueBind.getInputType().toString()));

                    jsonSettingList.add(settingValueBind.getTag(), valueBindObject);
                }

                if (settings instanceof ValueColor) {
                    ValueColor settingColor = (ValueColor) settings;

                    // We create a object json to save key and state.
                    JsonObject valueColorObject = new JsonObject();

                    valueColorObject.add("value", new JsonPrimitive(settingColor.getValue()));
                    valueColorObject.add("red", new JsonPrimitive(settingColor.getR()));
                    valueColorObject.add("green", new JsonPrimitive(settingColor.getG()));
                    valueColorObject.add("blue", new JsonPrimitive(settingColor.getB()));
                    valueColorObject.add("alpha", new JsonPrimitive(settingColor.getA()));

                    jsonSettingList.add(settingColor.getTag(), valueColorObject);
                }
            }

            mainJson.add("settings", jsonSettingList);

            String stringJson = gsonBuilder.toJson(jsonParser.parse(mainJson.toString()));
            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(pathFile), "UTF-8");

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
        }
    }

    @Override
    public void onLoad() {
        try {
            String pathFolder = PresetManager.getPolicyProtectionValue() + "/module/" + this.category.name().toLowerCase() + "/";
            String pathFile = pathFolder + this.tag + ".json";

            if (Files.exists(Paths.get(pathFile)) == false) {
                return;
            }

            JsonParser jsonParser = new JsonParser();

            InputStream file = Files.newInputStream(Paths.get(pathFile));
            JsonObject mainJson = jsonParser.parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson.get("settings") != null) {
                JsonObject jsonSettingList = mainJson.get("settings").getAsJsonObject();

                for (Setting settings : this.settingList) {
                    try {
                        if (jsonSettingList.get(settings.getTag()) == null) {
                            continue;
                        }

                        if (settings instanceof ValueBoolean) {
                            ValueBoolean settingValueBoolean = (ValueBoolean) settings;

                            settingValueBoolean.setValue(jsonSettingList.get(settings.getTag()).getAsBoolean());
                        }

                        if (settings instanceof ValueNumber) {
                            ValueNumber settingValueNumber = (ValueNumber) settings;

                            if (settingValueNumber.getValue() instanceof Float) {
                                settingValueNumber.setValue(jsonSettingList.get(settings.getTag()).getAsFloat());
                            }

                            if (settingValueNumber.getValue() instanceof Double) {
                                settingValueNumber.setValue(jsonSettingList.get(settings.getTag()).getAsDouble());
                            }

                            if (settingValueNumber.getValue() instanceof Integer) {
                                settingValueNumber.setValue(jsonSettingList.get(settings.getTag()).getAsInt());
                            }
                        }

                        if (settings instanceof ValueEnum) {
                            ValueEnum settingValueEnum = (ValueEnum) settings;

                            settingValueEnum.setValue(TurokClass.getEnumByName(settingValueEnum.getValue(), jsonSettingList.get(settings.getTag()).getAsString()));
                        }

                        if (settings instanceof ValueBind) {
                            ValueBind settingValueBind = (ValueBind) settings;

                            // Get the values from bind as json object.
                            JsonObject valueBindObject = jsonSettingList.get(settingValueBind.getTag()).getAsJsonObject();

                            if (valueBindObject.get("key") != null) {
                                // Find key index int using a name string.
                                String keyString = valueBindObject.get("key").getAsString();
                                int key = keyString.equalsIgnoreCase("NONE") ? -1 : Keyboard.getKeyIndex(keyString);

                                // Set key code.
                                settingValueBind.setKeyCode(key);
                            }

                            if (valueBindObject.get("state") != null) {
                                settingValueBind.setValue(valueBindObject.get("state").getAsBoolean());
                            }

                            if (valueBindObject.get("type") != null) {
                                settingValueBind.setInputType((InputType) TurokClass.getEnumByName(settingValueBind.getInputType(), valueBindObject.get("type").getAsString()));
                            }
                        }

                        if (settings instanceof ValueString) {
                            ValueString settingValueString = (ValueString) settings;

                            settingValueString.setValue(jsonSettingList.get(settings.getTag()).getAsString());
                        }

                        if (settings instanceof ValueColor) {
                            ValueColor settingValueColor = (ValueColor) settings;
                            JsonObject valueColorObject = jsonSettingList.get(settingValueColor.getTag()).getAsJsonObject();

                            if (valueColorObject.get("value") != null) {
                                settingValueColor.setValue(valueColorObject.get("value").getAsBoolean());
                            }

                            if (valueColorObject.get("red") != null) {
                                settingValueColor.setR(valueColorObject.get("red").getAsInt());
                            }

                            if (valueColorObject.get("green") != null) {
                                settingValueColor.setG(valueColorObject.get("green").getAsInt());
                            }

                            if (valueColorObject.get("blue") != null) {
                                settingValueColor.setB(valueColorObject.get("blue").getAsInt());
                            }

                            if (valueColorObject.get("alpha") != null) {
                                settingValueColor.setA(valueColorObject.get("alpha").getAsInt());
                            }
                        }
                    } catch (IllegalStateException | UnsupportedOperationException | NumberFormatException exc) {
                        continue;
                    }
                }
            }

            file.close();
        } catch (IOException exc) {
        }
    }
}
