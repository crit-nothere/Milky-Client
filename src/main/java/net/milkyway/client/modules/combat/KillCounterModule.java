package net.milkyway.client.modules.combat;

import net.milkyway.client.module.Module;

public class KillCounterModule extends Module {
    
    public KillCounterModule() {
        super("Kill Counter", "Tracks kills", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Module enabled
    }

    @Override
    public void onDisable() {
        // Module disabled
    }
}
