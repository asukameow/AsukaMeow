package at.meowww.AsukaMeow;

import at.meowww.AsukaMeow.config.ConfigManager;
import at.meowww.AsukaMeow.database.DatabaseManager;
import at.meowww.AsukaMeow.dialog.DialogManager;
import at.meowww.AsukaMeow.dialog.command.DialogCommandExecutor;
import at.meowww.AsukaMeow.item.ItemManager;
import at.meowww.AsukaMeow.item.command.ItemCommandExecutor;
import at.meowww.AsukaMeow.nms.NMSManager;
import at.meowww.AsukaMeow.system.SystemManager;
import at.meowww.AsukaMeow.system.command.SystemCommandExecutor;
import at.meowww.AsukaMeow.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class AsukaMeow extends JavaPlugin {

    public static Logger logger;
    public static String NMS_VERSION;
    public static AsukaMeow INSTANCE;

    private File file;
    private FileConfiguration config;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;

    private NMSManager nmsManager;
    private UserManager userManager;
    private DialogManager dialogManager;
    private ItemManager itemManager;
    private SystemManager systemManager;
    private World defaultWorld;

    DialogCommandExecutor dialogCommandExecutor;
    ItemCommandExecutor itemCommandExecutor;
    SystemCommandExecutor systemCommandExecutor;

    @Override
    public void onEnable () {
        INSTANCE = this;
        logger = INSTANCE.getLogger();
        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);

        file = new File(this.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        configManager = new ConfigManager(file, config);
        databaseManager = new DatabaseManager();

        nmsManager = new NMSManager();
        dialogManager = new DialogManager();
        userManager = new UserManager();
        itemManager = new ItemManager();
        systemManager = new SystemManager();

        // Infrastructure Load or Init
        configManager.load(databaseManager);
        logger.info("ConfigManager loaded!");

        databaseManager.databaseInit(
                userManager, dialogManager, itemManager, systemManager);
        logger.info("DatabaseManager loaded!");

        // Load
        defaultWorld = Bukkit.getWorlds().get(0);

        userManager.portOldPlayer();
        dialogManager.loadDialogs();
        itemManager.loadItems();
        systemManager.load();

        // CommandExecutors
        dialogCommandExecutor = new DialogCommandExecutor(this, dialogManager);
        itemCommandExecutor = new ItemCommandExecutor(this, itemManager);
        systemCommandExecutor = new SystemCommandExecutor(this, systemManager);
        // Executor init
        dialogCommandExecutor.setExecutor();
        itemCommandExecutor.setExecutor();
        systemCommandExecutor.setExecutor();

        // Register Listener
        userManager.registerListener();
        logger.info("UserManager loaded!");
        dialogManager.registerListener();
        logger.info("AsukaMeow DialogManager loaded!");
        itemManager.registerListener();
        logger.info("ItemManager loaded!");
    }

    @Override
    public void onDisable () {
        itemManager.saveItems();

        systemManager.save();
        configManager.save(databaseManager);
    }

    public World getDefaultWorld () {
        return this.defaultWorld;
    }

    public NMSManager getNMSManager () {
        return this.nmsManager;
    }

    public ItemManager getItemManager () {
        return this.itemManager;
    }

    public DialogManager getDialogManager () {
        return this.dialogManager;
    }

    public SystemManager getSystemManager () {
        return this.systemManager;
    }
}
