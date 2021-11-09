package io.github.badnotice.factionshistoric.listener;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import io.github.badnotice.factionshistoric.player.PlayerHistoric;
import io.github.badnotice.factionshistoric.player.PlayerHistoricCache;
import io.github.badnotice.factionshistoric.util.DateUtils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public final class FactionsListener implements Listener {

    private final PlayerHistoricCache playerHistoricCache;

    @EventHandler
    public void onFactionsCreate(EventFactionsCreate event) {
        MPlayer mPlayer = event.getMPlayer();

        playerHistoricCache.completeHistoric(new PlayerHistoric(
                mPlayer.getName(),
                event.getFactionTag(),
                event.getFactionName(),
                DateUtils.getCurrentDateTime()
        ));
    }

    @EventHandler
    public void onFactionsMembershipChange(EventFactionsMembershipChange event) {
        if (event.getReason() != EventFactionsMembershipChange.MembershipChangeReason.JOIN) {
            return;
        }

        MPlayer mPlayer = event.getMPlayer();
        Faction newFaction = event.getNewFaction();

        playerHistoricCache.completeHistoric(new PlayerHistoric(
                mPlayer.getName(),
                newFaction.getTag(),
                newFaction.getName(),
                DateUtils.getCurrentDateTime()
        ));
    }

}
