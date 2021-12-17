package rina.onepop.club.client.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.file.FileUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import me.rina.turok.util.TurokTick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:17
 **/
@Registry(name = "Search", tag = "Search", description = "Search module for find bases or a possible stash.", category = ModuleCategory.MISC)
public class ModuleSearch extends Module {
    public static class Artefact {
        private boolean isEntity;
        private boolean isTileEntity;

        private BlockPos position;
        private String name;

        public Artefact(String name, BlockPos pos) {
            this.name = name;
            this.position = pos;
        }

        public String getName() {
            return name;
        }

        public BlockPos getPosition() {
            return position;
        }

        public void setEntity(boolean entity) {
            isEntity = entity;
        }

        public boolean isEntity() {
            return isEntity;
        }

        public void setTileEntity(boolean tileEntity) {
            isTileEntity = tileEntity;
        }

        public boolean isTileEntity() {
            return isTileEntity;
        }
    }

    /* Misc. */
    public static ValueBoolean settingSaveFile = new ValueBoolean("Save File", "SaveFile", "Save all finds in a file.", true);
    public static ValueBoolean settingEnderCrystal = new ValueBoolean("Ender Crystal", "EnderCrystal", "Search ender crystals.", true);
    public static ValueBoolean settingWither = new ValueBoolean("Wither", "Wither", "Search for withers.",true);
    public static ValueBoolean settingChunk = new ValueBoolean("Chunk", "Chunk", "Find for artefacts at all chunk.", true);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range.", "Search range.", 200, 1, 500);

    private final List<Artefact> artefactList = new ArrayList<>();
    private final TurokTick delay = new TurokTick();

    private final StringBuilder cacheFile = new StringBuilder();
    private final String newLine = System.getProperty("line.separator");

    @Override
    public void onSetting() {
        settingRange.setEnabled(!settingChunk.getValue());

        if (!this.isEnabled()) {
            this.artefactList.clear();
        }
    }

    @Override
    public void onShutdown() {
        if (this.isEnabled()) {
            this.setDisabled();
        }
    }

    @Override
    public void onDisable() {
        if (settingSaveFile.getValue() && this.cacheFile.length() != 0) {
            try {
                final String pathFolder = Onepop.PATH_CONFIG + "search/";

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss");
                final LocalDateTime time = LocalDateTime.now();

                final String pathFile = pathFolder + "Search-" + (formatter.format(time)) + ".txt";

                FileUtil.createFolderIfNeeded(Paths.get(pathFolder));
                FileUtil.createFileIfNeeded(Paths.get(pathFile));

                final FileWriter file = new FileWriter(pathFile);

                file.write(this.cacheFile.toString());
                file.close();

                this.print(ChatFormatting.YELLOW + "Saved cache search! " + formatter.format(time));
            } catch (IOException exc) {
                this.print("An error occurred in save search cache.");
            }
        }

        this.artefactList.clear();
        this.cleanInfo();
    }

    @Override
    public void onEnable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.delay.reset();
        this.artefactList.clear();

        this.cleanInfo();

        if (settingSaveFile.getValue()) {
            this.cacheFile.append("#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$##$" + newLine);
            this.cacheFile.append("File Generated By Search From Onepop Client." + newLine);
            this.cacheFile.append("Username: " + ISLClass.mc.player.getName() + newLine);
            this.cacheFile.append("ServerIP: " + (ISLClass.mc.getCurrentServerData() == null ? "Vanilla" : ISLClass.mc.getCurrentServerData().serverIP) + newLine);
            this.cacheFile.append("#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$##$" + newLine);
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.updateSearchTileEntity();
        this.updateSearchEntity();

        if (this.artefactList.size() >= 64) {
            this.artefactList.clear();
        }
    }

    protected void updateSearchTileEntity() {
        for (TileEntity tileEntities : ISLClass.mc.world.loadedTileEntityList) {
            if (ISLClass.mc.player.getDistance(tileEntities.getPos().x, tileEntities.getPos().y, tileEntities.getPos().z) > settingRange.getValue().intValue() && !settingChunk.getValue()) {
                continue;
            }

            if (!this.containsArtefact(tileEntities.getPos())) {
                ChatUtil.print(ChatFormatting.YELLOW + "Found tile entity artefact: " + BlockUtil.getState(tileEntities.getPos()).getBlock().getLocalizedName() + " " + ("[" + tileEntities.getPos().x + ", " + tileEntities.getPos().y + ", " + tileEntities.getPos().z + "]"));

                this.makeInfo("Found tile entity: " + BlockUtil.getState(tileEntities.getPos()).getBlock().getLocalizedName() + " " + ("[" + tileEntities.getPos().x + ", " + tileEntities.getPos().y + ", " + tileEntities.getPos().z + "]"));

                final Artefact artefact = new Artefact(tileEntities.getBlockType().toString(), tileEntities.getPos());

                artefact.setEntity(false);
                artefact.setTileEntity(true);

                this.artefactList.add(artefact);
            }
        }
    }

    protected void updateSearchEntity() {
        for (Entity entities : ISLClass.mc.world.loadedEntityList) {
            if (ISLClass.mc.player.getDistance(entities) > settingRange.getValue().intValue() && !settingChunk.getValue()) {
                continue;
            }

            boolean registryArtefact = !this.containsArtefact(entities.getPosition()) && ((entities instanceof EntityEnderCrystal && settingEnderCrystal.getValue()) || (entities instanceof EntityWither && settingWither.getValue()));

            if (registryArtefact) {
                ChatUtil.print(ChatFormatting.YELLOW + "Found entity artefact: " + entities.getName() + " " + ("[" + entities.getPosition().x + ", " + entities.getPosition().y + ", " + entities.getPosition().z + "]"));

                this.makeInfo("Found entity: " + entities.getName() + " " + ("[" + entities.getPosition().x + ", " + entities.getPosition().y + ", " + entities.getPosition().z + "]"));

                final Artefact artefact = new Artefact(entities.getName(), entities.getPosition());

                artefact.setEntity(true);
                artefact.setTileEntity(false);

                this.artefactList.add(artefact);
            }
        }
    }

    public void makeInfo(String info) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        final LocalTime time = LocalTime.now();

        this.cacheFile.append("[" + formatter.format(time) + "]" + " [Info] " + info + newLine);
    }

    public void cleanInfo() {
        this.cacheFile.setLength(0);
    }

    public boolean containsArtefact(BlockPos position) {
        for (Artefact artefacts : this.artefactList) {
            if (PositionUtil.collideBlockPos(artefacts.getPosition(), position)) {
                return true;
            }
        }

        return false;
    }
}
