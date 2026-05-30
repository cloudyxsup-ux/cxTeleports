package com.cxteleport;

import com.cxteleport.commands.admin.CXTeleportCommand;
import com.cxteleport.commands.admin.JailCommand;
import com.cxteleport.commands.admin.SoltarCommand;
import com.cxteleport.commands.admin.TpAllCommand;
import com.cxteleport.commands.admin.TpCommand;
import com.cxteleport.commands.admin.TpHereCommand;
import com.cxteleport.commands.admin.TpLogCommand;
import com.cxteleport.commands.admin.TpPosCommand;
import com.cxteleport.commands.admin.TpSubirCommand;
import com.cxteleport.commands.back.VoltarCommand;
import com.cxteleport.commands.home.CasaCommand;
import com.cxteleport.commands.home.CasasCommand;
import com.cxteleport.commands.home.DelCasaCommand;
import com.cxteleport.commands.home.SetCasaCommand;
import com.cxteleport.commands.rtp.RTPCommand;
import com.cxteleport.commands.rtp.RTPFilaCommand;
import com.cxteleport.commands.spawn.SetSpawnCommand;
import com.cxteleport.commands.spawn.SpawnCommand;
import com.cxteleport.commands.tpa.TPACommand;
import com.cxteleport.commands.tpa.TPAAquiCommand;
import com.cxteleport.commands.tpa.TPAHistoricoCommand;
import com.cxteleport.commands.tpa.TPAAutoCommand;
import com.cxteleport.commands.tpa.TPABloquearCommand;
import com.cxteleport.commands.tpa.TPACancelarCommand;
import com.cxteleport.commands.tpa.TPAAceitarCommand;
import com.cxteleport.commands.tpa.TPADesbloquearCommand;
import com.cxteleport.commands.tpa.TPARecusarCommand;
import com.cxteleport.commands.tpa.ToggleTPACommand;
import com.cxteleport.commands.tpa.ToggleTPAAquiCommand;
import com.cxteleport.commands.warp.DelWarpCommand;
import com.cxteleport.commands.warp.SetWarpCommand;
import com.cxteleport.commands.warp.WarpCommand;
import com.cxteleport.commands.warp.WarpsCommand;
import com.cxteleport.hook.PAPIExpansion;
import com.cxteleport.hook.VaultHook;
import com.cxteleport.listener.CombatListener;
import com.cxteleport.listener.DeathListener;
import com.cxteleport.listener.GUIListener;
import com.cxteleport.listener.JoinListener;
import com.cxteleport.listener.MoveListener;
import com.cxteleport.listener.ProtectionListener;
import com.cxteleport.manager.CooldownManager;
import com.cxteleport.manager.HomeManager;
import com.cxteleport.manager.JailManager;
import com.cxteleport.manager.PlayerDataManager;
import com.cxteleport.manager.SpawnManager;
import com.cxteleport.manager.TPAManager;
import com.cxteleport.manager.TeleportManager;
import com.cxteleport.manager.WarpManager;
import com.cxteleport.manager.RTPManager;
import com.cxteleport.manager.CrossServerManager;
import com.cxteleport.scheduler.SchedulerAdapter;
import com.cxteleport.util.ConfigUtil;
import com.cxteleport.util.MessageUtil;
import com.cxteleport.util.SafeLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CXTeleport extends JavaPlugin {

    private static CXTeleport instance;
    private SchedulerAdapter scheduler;
    private ConfigUtil configUtil;
    private MessageUtil messageUtil;

    private CooldownManager cooldownManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private TPAManager tpaManager;
    private TeleportManager teleportManager;
    private PlayerDataManager playerDataManager;
    private JailManager jailManager;
    private RTPManager rtpManager;
    private CrossServerManager crossServerManager;

    private VaultHook vaultHook;
    private boolean folia = false;
    private boolean papiEnabled = false;

    private FileConfiguration messagesConfig;
    private File messagesFile;

    private long startTime;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        startTime = System.currentTimeMillis();
        instance = this;

        detectFolia();

        saveDefaultConfigs();
        loadConfigs();

        scheduler = new SchedulerAdapter(this);
        configUtil = new ConfigUtil(this);
        messageUtil = new MessageUtil(this);

        initHooks();
        initManagers();
        registerCommands();
        registerListeners();

        if (papiEnabled) {
            new PAPIExpansion(this).register();
        }

        getLogger().info("CXTeleport v" + getDescription().getVersion() + " ativado!");
        getLogger().info("Folia: " + folia);
        getLogger().info("Vault: " + (vaultHook != null && vaultHook.isEnabled()));
        getLogger().info("PlaceholderAPI: " + papiEnabled);
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) playerDataManager.saveAll();
        if (homeManager != null) homeManager.saveAll();
        if (warpManager != null) warpManager.saveAll();
        if (jailManager != null) jailManager.saveAll();
        if (spawnManager != null) spawnManager.save();
        if (rtpManager != null) rtpManager.shutdown();
        if (crossServerManager != null) crossServerManager.shutdown();

        scheduler.shutdown();
        getLogger().info("CXTeleport desativado!");
    }

    private void detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
            getLogger().info("Folia detectado! Usando schedulers regionais.");
        } catch (ClassNotFoundException e) {
            folia = false;
        }
    }

    private void saveDefaultConfigs() {
        saveResource("config.yml", false);
        saveResource("messages.yml", false);
    }

    private void loadConfigs() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void initHooks() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vaultHook = new VaultHook(this);
            vaultHook.setup();
        }

        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private void initManagers() {
        cooldownManager = new CooldownManager(this);
        playerDataManager = new PlayerDataManager(this);
        homeManager = new HomeManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        tpaManager = new TPAManager(this);
        teleportManager = new TeleportManager(this);
        jailManager = new JailManager(this);
        rtpManager = new RTPManager(this);
        crossServerManager = new CrossServerManager(this);

        playerDataManager.loadAll();
        homeManager.loadAll();
        warpManager.loadAll();
        spawnManager.load();
        jailManager.loadAll();
        rtpManager.startQueueProcessor();
    }

    private void registerCommands() {
        registerCommand("teleportar", new TPACommand(this));
        registerCommand("tpaaqui", new TPAAquiCommand(this));
        registerCommand("tpaceitar", new TPAAceitarCommand(this));
        registerCommand("tprecusar", new TPARecusarCommand(this));
        registerCommand("tpcancelar", new TPACancelarCommand(this));
        registerCommand("toggletpa", new ToggleTPACommand(this));
        registerCommand("toggletpaaqui", new ToggleTPAAquiCommand(this));
        registerCommand("tpaauto", new TPAAutoCommand(this));
        registerCommand("tpahistorico", new TPAHistoricoCommand(this));
        registerCommand("tpbloquear", new TPABloquearCommand(this));
        registerCommand("tpdesbloquear", new TPADesbloquearCommand(this));

        registerCommand("casa", new CasaCommand(this));
        registerCommand("setcasa", new SetCasaCommand(this));
        registerCommand("delcasa", new DelCasaCommand(this));
        registerCommand("casas", new CasasCommand(this));

        registerCommand("warp", new WarpCommand(this));
        registerCommand("setwarp", new SetWarpCommand(this));
        registerCommand("delwarp", new DelWarpCommand(this));
        registerCommand("warps", new WarpsCommand(this));

        registerCommand("spawn", new SpawnCommand(this));
        registerCommand("setspawn", new SetSpawnCommand(this));

        registerCommand("rtp", new RTPCommand(this));
        registerCommand("rtpfila", new RTPFilaCommand(this));

        registerCommand("voltar", new VoltarCommand(this));

        registerCommand("tp", new TpCommand(this));
        registerCommand("tphere", new TpHereCommand(this));
        registerCommand("tppos", new TpPosCommand(this));
        registerCommand("tpsubir", new TpSubirCommand(this));
        registerCommand("tptodos", new TpAllCommand(this));
        registerCommand("tplog", new TpLogCommand(this));
        registerCommand("prender", new JailCommand(this));
        registerCommand("soltar", new SoltarCommand(this));
        registerCommand("cxteleport", new CXTeleportCommand(this));
    }

    private void registerCommand(String name, Command command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register("cxteleport", command);
        } catch (Exception e) {
            getLogger().warning("Falha ao registrar comando: " + name);
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    public void reloadPlugin() {
        reloadConfig();
        loadConfigs();
        messageUtil = new MessageUtil(this);
        configUtil = new ConfigUtil(this);
        homeManager.loadAll();
        warpManager.loadAll();
        spawnManager.load();
        jailManager.loadAll();
    }

    public FileConfiguration getMessages() {
        return messagesConfig;
    }

    public void saveMessages() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CXTeleport getInstance() {
        return instance;
    }

    public SchedulerAdapter getScheduler() {
        return scheduler;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public TPAManager getTpaManager() {
        return tpaManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public JailManager getJailManager() {
        return jailManager;
    }

    public RTPManager getRtpManager() {
        return rtpManager;
    }

    public CrossServerManager getCrossServerManager() {
        return crossServerManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public boolean isFolia() {
        return folia;
    }

    public boolean isPapiEnabled() {
        return papiEnabled;
    }

    public long getStartTime() {
        return startTime;
    }
}
