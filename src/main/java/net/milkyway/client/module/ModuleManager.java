package net.milkyway.client.module;

import net.minecraft.client.MinecraftClient;
import net.milkyway.client.modules.combat.*;
import net.milkyway.client.modules.hud.*;
import net.milkyway.client.modules.movement.*;
import net.milkyway.client.modules.optimization.*;
import net.milkyway.client.modules.render.*;
import net.milkyway.client.modules.world.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // -- Render ------------------------------------------------------------
        register(new ZoomModule());
        register(new FullbrightModule());
        register(new HitboxesModule());
        register(new PlayerESPModule());
        register(new TracersModule());
        register(new NametagsModule());
        register(new ArmorHudModule());
        register(new NoHurtcamModule());
        register(new AmbienceModule());
        register(new FreelookModule());
        register(new NoRenderModule());
        register(new BreadcrumbsModule());

        // -- Combat ------------------------------------------------------------
        register(new HitColorModule());
        register(new DamageTintModule());
        register(new ReachDisplayModule());
        register(new HitParticlesModule());
        register(new AttackIndicatorModule());
        register(new KillCounterModule());
        register(new TargetHudModule());
        register(new VelocityModule());
        register(new ComboCounterModule());

        // -- Movement ----------------------------------------------------------
        register(new SprintModule());
        register(new StepModule());
        register(new SneakLockModule());
        register(new NoSlowModule());
        register(new BunnyHopModule());
        register(new FastSwimModule());
        register(new AntiVoidModule());

        // -- World -------------------------------------------------------------
        register(new XRayModule());
        register(new CaveFinderModule());
        register(new ChestEspModule());
        register(new SearchModule());
        register(new MinimapModule());

        // -- HUD ---------------------------------------------------------------
        register(new FpsCounterModule());
        register(new PingDisplayModule());
        register(new PotionsHudModule());
        register(new KeystrokesModule());
        register(new CpsCounterModule());
        register(new ClockModule());
        register(new ServerInfoModule());
        register(new CompassModule());
        register(new CoordinatesHudModule());

        // -- Optimization ------------------------------------------------------
        register(new FPSBoostModule());
        register(new ChunkOptModule());
        register(new NetworkOptModule());
        register(new AutoGgModule());
        register(new ChatTweaksModule());
        register(new ScreenshotModule());
        register(new NoDisconnectModule());
        register(new ReconnectModule());
        register(new AntiAfkModule());
        register(new MacroModule());
    }

    private void register(Module m) {
        modules.add(m);
    }

    public void onTick(MinecraftClient client) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onTick(client);
                } catch (Exception e) {
                    // isolate per-module errors
                }
            }
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public List<Module> getModulesByCategory(Module.Category cat) {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == cat) result.add(m);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> cls) {
        for (Module m : modules) {
            if (m.getClass() == cls) return (T) m;
        }
        return null;
    }
}
