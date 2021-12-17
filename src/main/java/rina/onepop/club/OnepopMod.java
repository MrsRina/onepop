package rina.onepop.club;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "onepop", name = Onepop.NAME, version = Onepop.VERSION)
public class OnepopMod {
    public static Onepop INSTANCE = new Onepop();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        INSTANCE.onClientStarted(event);
    }
}
