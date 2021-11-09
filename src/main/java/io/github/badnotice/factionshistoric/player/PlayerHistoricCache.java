package io.github.badnotice.factionshistoric.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import io.github.badnotice.factionshistoric.FactionsHistoricPlugin;
import io.github.badnotice.factionshistoric.dao.PlayerHistoricDAO;
import io.github.badnotice.factionshistoric.view.PlayerHistoriesView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public final class PlayerHistoricCache {

    private final PlayerHistoricDAO playerHistoricDAO;
    private final LoadingCache<String, Set<PlayerHistoric>> cache;

    public PlayerHistoricCache(FactionsHistoricPlugin plugin) {
        playerHistoricDAO = new PlayerHistoricDAO();

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, Set<PlayerHistoric>>() {
                            @Override
                            public Set<PlayerHistoric> load(@NotNull String playerName) throws Exception {
                                return playerHistoricDAO.selectMany(playerName);
                            }
                        }
                );

        Bukkit.getPluginManager().registerEvents(this.createInternalListener(), plugin);
    }

    public Set<PlayerHistoric> getHistoric(String name) {
        return this.cache.getUnchecked(name);
    }

    public void completeHistoric(PlayerHistoric historic) {
        this.playerHistoricDAO.insertOne(historic);
        this.cache.invalidate(historic.getPlayerName());
    }

    Listener createInternalListener() {
        return new Listener() {

            private final Set<String> subCommands = Sets.newHashSet(
                    "historic",
                    "historico"
            );

            @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
            public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
                String[] commands = event.getMessage().split(" ");
                if (commands.length != 3) {
                    return;
                }

                if (commands[0].equalsIgnoreCase("/f") && subCommands.contains(commands[1])) {
                    event.setCancelled(true);

                    Player player = event.getPlayer();
                    String targetName = commands[2];

                    PlayerHistoriesView historiesView = FactionsHistoricPlugin.getInstance().getPlayerHistoriesView();
                    historiesView.openInventory(player, viewer -> {
                        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
                        propertyMap.set("player_name", targetName);
                        propertyMap.set("player_histories", getHistoric(targetName));
                    });
                }
            }

        };
    }

}
