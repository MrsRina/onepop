package rina.onepop.club.api.preset.management;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.management.ModuleManager;
import rina.onepop.club.api.preset.Preset;
import rina.onepop.club.api.preset.impl.PresetState;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.ByteManipulator;
import rina.onepop.club.api.util.client.DateTimerUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author SrRina
 * @since 10/07/2021 at 13:53
 **/
public class PresetManager {
    public static final int SAVE = 331;
    public static final int LOAD = 332;
    public static final int DATA = 333;

    public static PresetManager INSTANCE;
    public static Path PATH = Paths.get(Onepop.PATH_PRESET);

    private final Set<Preset> presetSet = new HashSet<>();
    protected String policyProtectionPreset = "default";

    public PresetManager() {
        INSTANCE = this;
    }

    public void init() {
    }

    public Set<Preset> getPresetSet() {
        return presetSet;
    }

    public static String getPolicyProtectionValue() {
        return PresetManager.INSTANCE.getCurrentPresetPath();
    }

    public static void implement(Preset preset) {
        if (INSTANCE.presetSet.contains(preset)) {
            return;
        }

        INSTANCE.presetSet.add(preset);
    }

    public static void exclude(Preset preset) {
        INSTANCE.presetSet.remove(preset);

        final String path = Onepop.PATH_PRESET + preset.getTag();

        if (Files.exists(Paths.get(path))) {
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    public static void sync(Preset preset) {
        final Preset current = current();

        set(preset);
        reload();

        process(SAVE);

        if (current != null) {
            set(current);
            reload();
        }
    }

    public Map<String, PresetState> findForPresetMap() {
        final HashMap<String, PresetState> map = new HashMap<>();

        if (!Files.exists(PATH)) {
            try {
                Files.createDirectories(PATH);
            } catch (IOException exc) {
                exc.printStackTrace();
            }

            return map;
        }

        final File[] fileList = new File(Onepop.PATH_PRESET).listFiles();

        if (fileList != null) {
            int i = 0;

            for (int j = fileList.length; i < j; i++) {
                final File file = fileList[i];

                if (!file.isDirectory() || contains(file.getName())) {
                    continue;
                }

                map.put(file.getName(), this.getPresetState(file.getPath()));
            }
        }

        return map;
    }

    public PresetState getPresetState(final String path) {
        final String concurrentPath = path + "/" + "Validator.json";
        final PresetState state = new PresetState();

        if (!Files.exists(Paths.get(concurrentPath))) {
            return state;
        }

        try {
            JsonParser jsonParser = new JsonParser();

            InputStream file = Files.newInputStream(Paths.get(concurrentPath));
            JsonObject mainJson = jsonParser.parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson != null) {
                state.setMetaData(mainJson);
            }

            file.close();
        } catch (IOException exc) {
            state.setMetaData(null);
        }

        return state;
    }

    public static Preset get(String name) {
        Preset preset = null;

        for (Preset presets : INSTANCE.getPresetSet()) {
            if (presets.getTag().equalsIgnoreCase(name)) {
                preset = presets;

                break;
            }
        }

        return preset;
    }

    public static boolean contains(String name) {
        return get(name) != null;
    }

    public static void refresh() {
        final Map<String, PresetState> map = INSTANCE.findForPresetMap();

        for (Map.Entry<String, PresetState> entry : map.entrySet()) {
            final String name = entry.getKey();
            final PresetState preset = entry.getValue();

            Preset thePreset = get(name);

            if (thePreset != null) {
                continue;
            }

            final JsonObject metadata = preset.getMetaData();

            thePreset = new Preset(name, DateTimerUtil.time(DateTimerUtil.TIME_AND_DATE));

            if (metadata != null && (metadata.get("data") != null || metadata.get("current") != null)) {
                if (metadata.get("data") != null) {
                    thePreset.setData(metadata.get("data").getAsString());
                }

                if (metadata.get("current") != null) {
                    boolean isValid = ByteManipulator.byteToBoolean(metadata.get("current").getAsByte());

                    if (isValid) {
                        thePreset.setValidator();
                    } else {
                        thePreset.unsetValidator();
                    }
                }
            }

            implement(thePreset);

            ChatUtil.print("Found " + thePreset.getTag() + " new preset!");
        }
    }

    public static void reload() {
        boolean containsOne = false;

        for (Preset presets : INSTANCE.getPresetSet()) {
            if (presets.isCurrent()) {
                INSTANCE.setPolicyProtectionPreset(presets.getTag().toLowerCase());

                containsOne = true;

                break;
            }
        }

        if (!containsOne) {
            final Preset preset = new Preset("default", DateTimerUtil.time(DateTimerUtil.TIME_AND_DATE));

            INSTANCE.implement(preset);
            INSTANCE.setPolicyProtectionPreset(preset.getTag().toLowerCase());

            set(preset);
            sync(preset);
        }
    }

    public static void process(int protocol) {
        for (Preset presets : INSTANCE.getPresetSet()) {
            if (protocol == DATA) {
                INSTANCE.updateMetaData(presets);
            }

            if (presets.isCurrent() && protocol != DATA) {
                if (protocol == SAVE) {
                    INSTANCE.doSaveClient();
                } else if (protocol == LOAD) {
                    INSTANCE.doLoadClient();
                }

                break;
            }
        }
    }

    public static Preset current() {
        Preset preset = null;

        for (Preset presets : INSTANCE.getPresetSet()) {
            if (presets.isCurrent()) {
                preset = presets;

                break;
            }
        }

        return preset;
    }

    public static void set(Preset preset) {
        for (Preset presets : INSTANCE.getPresetSet()) {
            if (presets.getTag().equalsIgnoreCase(preset.getTag())) {
                presets.setValidator();
            } else {
                presets.unsetValidator();
            }
        }
    }

    public void updateMetaData(Preset preset) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();

        String superiorFolder = Onepop.PATH_PRESET + preset.getTag();
        String superiorFile = superiorFolder + "/Validator.json";

        try {
            if (!Files.exists(Paths.get(superiorFolder))) {
                Files.createDirectories(Paths.get(superiorFolder));
            }

            if (Files.exists(Paths.get(superiorFile))) {
                java.io.File file = new java.io.File(superiorFile);
                file.delete();
            }

            Files.createFile(Paths.get(superiorFile));

            JsonObject metadata = new JsonObject();

            metadata.add("tag", new JsonPrimitive(preset.getTag()));
            metadata.add("data", new JsonPrimitive(preset.getData()));
            metadata.add("current", new JsonPrimitive(preset.getCertification()));

            String stringJson = gsonBuilder.toJson(jsonParser.parse(metadata.toString()));
            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(superiorFile), "UTF-8");

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
        }
    }

    public void setPolicyProtectionPreset(String preset) {
        policyProtectionPreset = preset;
    }

    public String getCurrentPresetPath() {
        return Onepop.PATH_PRESET + this.policyProtectionPreset + "/";
    }

    public void doSaveClient() {
        ModuleManager.INSTANCE.onSave();
    }

    public void doLoadClient() {
        ModuleManager.INSTANCE.onLoad();
        ModuleManager.reload();
    }

    public static void info() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (Preset presets : INSTANCE.getPresetSet()) {
            stringBuilder.append(presets.getTag()).append("; ");
        }

        ChatUtil.print(stringBuilder.toString());
    }
}