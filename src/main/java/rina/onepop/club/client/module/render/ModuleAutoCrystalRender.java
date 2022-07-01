package rina.onepop.club.client.module.render;

import me.rina.turok.util.TurokMath;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.module.combat.autocrystalrewrite.ModuleAutoCrystalRewrite;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 09/10/2021 at 15:27
 **/
@Registry(name = "Auto-Crystal Render", tag = "AutoCrystalRender", description = "Better renders.", category = ModuleCategory.RENDER)
public class ModuleAutoCrystalRender extends Module {
    public static ModuleAutoCrystalRender INSTANCE;

    // Misc.
    public static ValueNumber settingSlow = new ValueNumber("Slow", "Slow", "Sets the animation factory.", 1, 0, 2);

    public static class PlaceRender {
        private BlockPos position;
        private int alphaLine;
        private int alphaSolid;

        public PlaceRender(BlockPos position) {
            this.position = position;

            this.alphaLine = ModuleAutoCrystalRewrite.settingLineAlpha.getValue().intValue();
            this.alphaSolid = ModuleAutoCrystalRewrite.settingLineAlpha.getValue().intValue();
        }

        public int getAlphaLine() {
            return alphaLine;
        }

        public int getAlphaSolid() {
            return alphaSolid;
        }

        public void setAlphaLine(int alphaLine) {
            this.alphaLine = alphaLine;
        }

        public void setAlphaSolid(int alphaSolid) {
            this.alphaSolid = alphaSolid;
        }

        public BlockPos getPosition() {
            return position;
        }
    }

    public final List<BlockPos> blockList = new ArrayList<>();
    public final List<PlaceRender> renderList = new ArrayList<>();

    public ModuleAutoCrystalRender() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.renderList.clear();
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ()).down();

            if (packet.getType() == 51 && ModuleAutoCrystalRewrite.INSTANCE.placeCount.contains(pos)) {
                this.renderList.removeIf(placeRender -> (placeRender.getPosition().equals(pos)));

                ModuleAutoCrystalRender.INSTANCE.renderList.add(new PlaceRender(pos));
            }
        }
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        try {
            float partialTicks = (float) (Onepop.getClientEventManager().getCurrentRender2DPartialTicks() * (settingSlow.getValue().intValue() == 0 ? 0.1 : (settingSlow.getValue().intValue() == 1 ? 0.01 : 0.001)));

            for (PlaceRender places : new ArrayList<>(this.renderList)) {
                places.setAlphaLine((int) TurokMath.lerp(places.getAlphaLine(), 0, partialTicks));
                places.setAlphaSolid((int) TurokMath.lerp(places.getAlphaSolid(), 0, partialTicks));

                RenderUtil.drawSolidBlock(camera, places.getPosition(), ModuleAutoCrystalRewrite.settingColorPlace.getColor(places.getAlphaSolid()));
                RenderUtil.drawOutlineBlock(camera, places.getPosition(), ModuleAutoCrystalRewrite.settingLineSize.getValue().floatValue(), ModuleAutoCrystalRewrite.settingColorPlace.getColor(places.getAlphaLine()));
            }
        } catch (Exception exc) {}
    }

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.renderList.removeIf(placeRender -> (placeRender.getAlphaSolid() <= 10 && placeRender.getAlphaLine() <= 10));
    }
}
