package org.nunnerycode.bukkit.mobbountyreloaded;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.nunnerycode.bukkit.libraries.ivory.IvoryPlugin;
import net.nunnerycode.bukkit.libraries.ivory.config.VersionedIvoryYamlConfiguration;
import net.nunnerycode.bukkit.libraries.ivory.settings.IvorySettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.nunnerycode.bukkit.mobbountyreloaded.api.economy.IEconomyHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.api.groups.IGroupHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.api.mobs.IMobHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.commands.MobBountyCommands;
import org.nunnerycode.bukkit.mobbountyreloaded.economy.EconomyHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.exploits.ExploitListener;
import org.nunnerycode.bukkit.mobbountyreloaded.groups.GroupHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.listeners.EntityListener;
import org.nunnerycode.bukkit.mobbountyreloaded.mobs.MobHandler;
import org.nunnerycode.bukkit.mobbountyreloaded.wrappers.HoloAPIWrapper;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

import java.io.File;
import java.util.logging.Level;

import static net.nunnerycode.bukkit.libraries.ivory.config.VersionedIvoryConfiguration.VersionUpdateType;

public final class MobBountyReloadedPlugin extends IvoryPlugin {

    private IvorySettings ivorySettings;
    private IMobHandler mobHandler;
    private IEconomyHandler economyHandler;
    private IGroupHandler groupHandler;
    private EntityListener entityListener;
    private ExploitListener exploitListener;
    private VersionedIvoryYamlConfiguration configYAML;
    private VersionedIvoryYamlConfiguration rewardsYAML;
    private VersionedIvoryYamlConfiguration multipliersYAML;
    private VersionedIvoryYamlConfiguration languageYAML;
    private VersionedIvoryYamlConfiguration exploitsYAML;
    private HoloAPIWrapper holoAPIWrapper;

    public VersionedIvoryYamlConfiguration getConfigYAML() {
        return configYAML;
    }

    @Override
    public void enable() {
        configYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "config.yml"),
                        getResource("config.yml"),
                        VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
            debug(Level.INFO, "Updating config.yml");
        }

        rewardsYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "rewards.yml"),
                        getResource("rewards.yml"),
                        VersionUpdateType.BACKUP_AND_UPDATE);
        if (rewardsYAML.update()) {
            getLogger().info("Updating rewards.yml");
            debug(Level.INFO, "Updating rewards.yml");
        }

        multipliersYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "multipliers.yml"),
                        getResource("multipliers.yml"),
                        VersionUpdateType.BACKUP_AND_UPDATE);
        if (multipliersYAML.update()) {
            getLogger().info("Updating multipliers.yml");
            debug(Level.INFO, "Updating multipliers.yml");
        }

        languageYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "language.yml"),
                        getResource("language.yml"),
                        VersionUpdateType.BACKUP_AND_UPDATE);
        if (languageYAML.update()) {
            getLogger().info("Updating language.yml");
            debug(Level.INFO, "Updating language.yml");
        }

        exploitsYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "exploits.yml"),
                        getResource("exploits.yml"),
                        VersionUpdateType.BACKUP_AND_UPDATE);
        if (exploitsYAML.update()) {
            getLogger().info("Updating exploits.yml");
            debug(Level.INFO, "Updating exploits.yml");
        }

        ivorySettings =
                IvorySettings
                        .loadFromFiles(configYAML, rewardsYAML, multipliersYAML, languageYAML, exploitsYAML);

        mobHandler = new MobHandler(this);

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            debug(Level.SEVERE, "Could not find Vault, disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        RegisteredServiceProvider<Economy>
                rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            debug(Level.SEVERE, "Could not find Economy provider, disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (rsp.getProvider() == null) {
            debug(Level.SEVERE, "Could not find Economy plugin, disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        economyHandler = new EconomyHandler(rsp.getProvider());

        RegisteredServiceProvider<Permission>
                rsp2 = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp2 == null) {
            debug(Level.SEVERE, "Could not find Permissions provider");
        }
        if (rsp2 != null && rsp2.getProvider() == null) {
            debug(Level.SEVERE, "Could not find Permissions plugin");
        }
        groupHandler = new GroupHandler(rsp2 != null ? rsp2.getProvider() : null);

        holoAPIWrapper = new HoloAPIWrapper(this);

        entityListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(entityListener, this);
        exploitListener = new ExploitListener(this);
        Bukkit.getPluginManager().registerEvents(exploitListener, this);

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new MobBountyCommands(this));

        debug(Level.INFO, "v" + getDescription().getVersion() + " enabled");
    }

    @Override
    public void disable() {
        // do nothing
    }

    public IvorySettings getIvorySettings() {
        return ivorySettings;
    }

    public IMobHandler getMobHandler() {
        return mobHandler;
    }

    public IEconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    public VersionedIvoryYamlConfiguration getRewardsYAML() {
        return rewardsYAML;
    }

    public VersionedIvoryYamlConfiguration getMultipliersYAML() {
        return multipliersYAML;
    }

    public VersionedIvoryYamlConfiguration getLanguageYAML() {
        return languageYAML;
    }

    public VersionedIvoryYamlConfiguration getExploitsYAML() {
        return exploitsYAML;
    }

    public IGroupHandler getGroupHandler() {
        return groupHandler;
    }

    public HoloAPIWrapper getHoloAPIWrapper() {
        return holoAPIWrapper;
    }
}