package io.github.badnotice.factionshistoric.dao.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import io.github.badnotice.factionshistoric.player.PlayerHistoric;

public final class SQLPlayerHistoricAdapter implements SQLResultAdapter<PlayerHistoric> {

    @Override
    public PlayerHistoric adaptResult(SimpleResultSet resultSet) {
        return new PlayerHistoric(
                resultSet.get("player_name"),
                resultSet.get("faction_tag"),
                resultSet.get("faction_name"),
                resultSet.get("date")
        );
    }

}
