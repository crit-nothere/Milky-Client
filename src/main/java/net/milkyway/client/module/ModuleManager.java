package net.milkyway.client.module;

import net.minecraft.client.MinecraftClient;
import net.milkyway.client.modules.render.*;
import net.milkyway.client.modules.combat.*;
import net.milkyway.client.modules.movement.*;
import net.milkyway.client.modules.world.*;
import net.milkyway.client.modules.hud.*;
import net.milkyway.client.modules.optimization.*;

import java.util.*;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        try {
            // Render modules (12)
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

            // Combat modules (9)
            register(new HitColorModule());
            register(new DamageTintModule());
            register(new ReachDisplayModule());
            register(new HitParticlesModule());
            register(new AttackIndicatorModule());
            register(new KillCounterModule());
            register(new TargetHudModule());
            register(new VelocityModule());
            register(new ComboCounterModule());

            // Movement modules (8)
            register(new SprintModule());
            register(new StepModule());
            register(new SneakLockModule());
            register(new NoSlowModule());
            register(new BunnyHopModule());
            register(new FastSwimModule());
            register(new AntiVoidModule());

            // World modules (5)
            register(new XRayModule());
            register(new CaveFinderModule());
            register(new ChestEspModule());
            register(new SearchModule());
            register(new MinimapModule());

            // HUD modules (9)
            register(new FpsCounterModule());
            register(new PingDisplayModule());
            register(new PotionsHudModule());
            register(new KeystrokesModule());
            register(new CpsCounterModule());
            register(new ClockModule());
            register(new ServerInfoModule());
            register(new CompassModule());
            register(new CoordinatesHudModule());

            // Optimization modules (10)
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

        } catch (Exception e) {
            System.err.println("[MilkyWay] Error initializing modules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void register(Module module) {
        modules.add(module);
    }

    public void onTick(MinecraftClient client) {
        try {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    try {
                        module.onTick(client);
                    } catch (Exception e) {
                        System.err.println("[MilkyWay] Error ticking module " + module.getName() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[MilkyWay] Error in ModuleManager.onTick: " + e.getMessage());
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        return null;
    }
}
