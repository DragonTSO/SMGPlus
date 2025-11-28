package me.waleks.simplematerialgenerators;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.waleks.simplematerialgenerators.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Level;

public class SimpleMaterialGenerators extends JavaPlugin implements SlimefunAddon {

    private static SimpleMaterialGenerators instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        setInstance(this);

        // Save default config
        saveDefaultConfig();

        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, "  _____ __  __  _____");
        getLogger().log(Level.INFO, " / ____|  \\/  |/ ____|");
        getLogger().log(Level.INFO, "| (___ | \\  / | |  __");
        getLogger().log(Level.INFO, " \\___ \\| |\\/| | | |_ |");
        getLogger().log(Level.INFO, " ____) | |  | | |__| |");
        getLogger().log(Level.INFO, "|_____/|_|  |_|\\_____|");
        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, "SimpleMaterialGenerators v" + getDescription().getVersion());
        getLogger().log(Level.INFO, "Supports Networks Grabber & Cargo!");
        getLogger().log(Level.INFO, "");

        // Load and register generators from config
        configManager = new ConfigManager(this);
        configManager.loadAndRegister();

        getLogger().log(Level.INFO, "SimpleMaterialGenerators has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "SimpleMaterialGenerators has been disabled.");
        setInstance(null);
    }

    @Nonnull
    @Override
    public String getBugTrackerURL() {
        return "https://github.com/waleks647/SMG/issues";
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nonnull
    public static SimpleMaterialGenerators getInstance() {
        return instance;
    }

    private static void setInstance(@Nullable SimpleMaterialGenerators instance) {
        SimpleMaterialGenerators.instance = instance;
    }

    @Nonnull
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
