package io.github.badnotice.factionshistoric;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import io.github.badnotice.factionshistoric.dao.factory.SQLDatabaseFactory;
import io.github.badnotice.factionshistoric.listener.FactionsListener;
import io.github.badnotice.factionshistoric.player.PlayerHistoricCache;
import io.github.badnotice.factionshistoric.view.PlayerHistoriesView;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class FactionsHistoricPlugin extends JavaPlugin {

    public static FactionsHistoricPlugin getInstance() {
        return getPlugin(FactionsHistoricPlugin.class);
    }

    private SQLConnector sqlConnector;

    private PlayerHistoricCache playerHistoricCache;
    private PlayerHistoriesView playerHistoriesView;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        InventoryManager.enable(this);

        FileConfiguration config = this.getConfig();
        sqlConnector = SQLDatabaseFactory.createSqlConnector(config.getConfigurationSection("database"));

        playerHistoricCache = new PlayerHistoricCache(this);
        playerHistoriesView = new PlayerHistoriesView().init();

        Bukkit.getPluginManager().registerEvents(new FactionsListener(this.playerHistoricCache), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
