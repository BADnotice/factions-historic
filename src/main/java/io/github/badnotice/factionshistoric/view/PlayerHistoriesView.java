package io.github.badnotice.factionshistoric.view;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import io.github.badnotice.factionshistoric.player.PlayerHistoric;
import io.github.badnotice.factionshistoric.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PlayerHistoriesView extends PagedInventory {

    private final Map<String, ItemStack> HEAD_CACHE  = Maps.newConcurrentMap();

    public PlayerHistoriesView() {
        super(
                "playerhistoriesview.main",
                "",
                5 * 9
        );
    }

    @Override
    protected void configureViewer(@NotNull PagedViewer viewer) {
        ViewerConfigurationImpl configuration = viewer.getConfiguration();

        String playerName = viewer.getPropertyMap().get("player_name");
        configuration.titleInventory("Historico: " + playerName);
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(@NotNull PagedViewer viewer) {
        List<InventoryItemSupplier> itemSuppliers = Lists.newCopyOnWriteArrayList();
        Set<PlayerHistoric> histories = viewer.getPropertyMap().get("player_histories");

        for (PlayerHistoric playerHistoric : histories) {
            itemSuppliers.add(() -> this.playerHistoryInventoryItem(playerHistoric));
        }

        return itemSuppliers;
    }

    private InventoryItem playerHistoryInventoryItem(PlayerHistoric historic) {
        ItemStack head = HEAD_CACHE.getOrDefault(
                historic.getPlayerName(),
                HEAD_CACHE.put(historic.getPlayerName(), getHeadPlayer(historic.getPlayerName()))
        ).clone();

        return InventoryItem.of(ItemBuilder.of(head)
                .displayName("§eHistorico de facção")
                .lore(Arrays.asList(
                        "",
                        " §fFacção: §7[" + historic.getFactionTag() + "] " + historic.getFactionName(),
                        " §fData: §7" + historic.getDateCreated()
                ))
                .getItemStack());
    }

    private ItemStack getHeadPlayer(String name) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();

        skullMeta.setOwner(name);
        skull.setItemMeta(skullMeta);

        return skull;
    }

}
