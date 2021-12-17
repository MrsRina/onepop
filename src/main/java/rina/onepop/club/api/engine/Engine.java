package rina.onepop.club.api.engine;

import rina.onepop.club.api.engine.caller.Processor;

/**
 * @author SrRina
 * @since 14/07/2021 at 23:57
 **/
public class Engine {
    public static final String VERSION = "0.1";
    public static final int ID = 0;

    public static Engine INSTANCE;
    public static Processor THE_PROCESSOR;

    public Engine() {
        THE_PROCESSOR = new Processor();
    }

    public static void initialize() {
        INSTANCE = new Engine();
    }

    public static Processor callGPU() {
        return THE_PROCESSOR;
    }
}
