package at.meowww.AsukaMeow;

import at.meowww.AsukaMeow.config.ConfigManager;
import at.meowww.AsukaMeow.database.DatabaseManager;
import at.meowww.AsukaMeow.dialog.DialogManager;
import at.meowww.AsukaMeow.nms.NMSManager;
import at.meowww.AsukaMeow.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class AsukaMeow extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("");
    public static String NMS_VERSION;
    public static AsukaMeow INSTANCE;

    private File file;
    private FileConfiguration config;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;

    private NMSManager nmsManager;
    private UserManager userManager;
    private DialogManager dialogManager;
    private World defaultWorld;

    @Override
    public void onEnable () {
        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        INSTANCE = this;
        file = new File(this.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        configManager = new ConfigManager(file, config);
        databaseManager = new DatabaseManager();

        nmsManager = new NMSManager();
        dialogManager = new DialogManager();
        userManager = new UserManager();

        configManager.load(databaseManager);
        logger.info("AsukaMeow config loaded!");

        databaseManager.databaseInit(userManager);
        logger.info("AsukaMeow database loaded!");

        dialogManager.registerListener();
        logger.info("AsukaMeow DialogManager loaded!");

        userManager.registerListener();
        logger.info("AsukaMeow UserManager loaded!");

        defaultWorld = Bukkit.getWorlds().get(0);

        userManager.portOldPlayer();
    }

    @Override
    public void onDisable () {
        configManager.save(databaseManager);
    }

    public World getDefaultWorld () {
        return this.defaultWorld;
    }

    public NMSManager getNMSManager () {
        return this.nmsManager;
    }
}
