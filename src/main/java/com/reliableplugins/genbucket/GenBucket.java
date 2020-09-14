package com.reliableplugins.genbucket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reliableplugins.genbucket.command.impl.BaseCommand;
import com.reliableplugins.genbucket.generator.Generator;
import com.reliableplugins.genbucket.generator.data.GeneratorData;
import com.reliableplugins.genbucket.generator.data.GeneratorType;
import com.reliableplugins.genbucket.listener.InventoryListener;
import com.reliableplugins.genbucket.listener.PlayerListener;
import com.reliableplugins.genbucket.manager.GenBucketManager;
import com.reliableplugins.genbucket.manager.HookManager;
import com.reliableplugins.genbucket.menu.MainMenu;
import com.reliableplugins.genbucket.nms.NMSHandler;
import com.reliableplugins.genbucket.nms.nms.Version_1_8_R3;
import com.reliableplugins.genbucket.runnable.GeneratorTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class GenBucket extends JavaPlugin {

    private final int tickSpeed = getConfig().getInt("settings.tick-speed");
    private BaseCommand baseCommand;
    private NMSHandler nmsHandler;
    private HookManager hookManager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
    private Map<String, Generator> generatorMap = new HashMap<>();
    private MainMenu mainMenu;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.nmsHandler = setupNMS();
        this.baseCommand = new BaseCommand(this);
        this.hookManager = new HookManager(this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new GeneratorTask(this), tickSpeed, tickSpeed);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        generatorMap = GenBucketManager.loadGenBuckets(getConfig(), this);

        this.mainMenu = new MainMenu(this).init();
    }

    @Override
    public void onDisable() {
        Map<String, Set<GeneratorData>> locations = new HashMap<>();
        for (Map.Entry<String, Generator> generator : generatorMap.entrySet()) {
            if (generator.getValue().getGeneratorType() == GeneratorType.HORIZONTAL) continue;
            Set<GeneratorData> oldLocations = new HashSet<>();
            generator.getValue().getLocations().values().forEach(oldLocations::addAll);
            locations.put(generator.getKey(), oldLocations);
        }
        try (FileWriter writer = new FileWriter(this.getDataFolder() + File.separator + "genbucket-data.json")) {
            gson.toJson(locations, writer);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save genbucket data!");
        }
    }

    public NMSHandler setupNMS() {
        String version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        switch (version) {
            case "v1_8_R3":
                return new Version_1_8_R3();
            default:
                return null;
        }
    }

    public BaseCommand getBaseCommand() {
        return baseCommand;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public Map<String, Generator> getGeneratorMap() {
        return generatorMap;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public NMSHandler getNMSHandler() {
        return nmsHandler;
    }
}
