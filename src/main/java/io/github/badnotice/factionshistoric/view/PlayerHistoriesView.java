package io.github.badnotice.factionshistoric.view;

import com.google.common.collect.Lists;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import io.github.badnotice.factionshistoric.player.PlayerHistoric;
import io.github.badnotice.factionshistoric.util.ItemBuilder;
import io.github.badnotice.factionshistoric.util.SkullHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class PlayerHistoriesView extends PagedInventory {

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
        return InventoryItem.of(ItemBuilder.of(SkullHelper.getPlayerHead(historic.getPlayerName()))
                .displayName("§eHistorico de facção")
                .lore(Arrays.asList(
                        "",
                        " §fFacção: §7[" + historic.getFactionTag() + "] " + historic.getFactionName(),
                        " §fData: §7" + historic.getDateCreated()
                ))
                .getItemStack());
    }

}
