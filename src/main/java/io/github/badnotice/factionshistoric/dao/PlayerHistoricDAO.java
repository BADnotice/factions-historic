package io.github.badnotice.factionshistoric.dao;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import io.github.badnotice.factionshistoric.FactionsHistoricPlugin;
import io.github.badnotice.factionshistoric.dao.adapter.SQLPlayerHistoricAdapter;
import io.github.badnotice.factionshistoric.player.PlayerHistoric;

import java.util.Set;
import java.util.UUID;

public final class PlayerHistoricDAO {

    private final static String TABLE = "player_histories";

    public PlayerHistoricDAO() {
        createTable();
    }

    public void createTable() {
        this.executor().updateQuery(
                "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                        "player_name VARCHAR(16) NOT NULL," +
                        "faction_tag TEXT NOT NULL," +
                        "faction_name TEXT NOT NULL," +
                        "date TEXT NOT NULL" +
                        ");"
        );
    }

    public Set<PlayerHistoric> selectMany(String name) {
        return this.executor().resultManyQuery(
                "SELECT * FROM " + TABLE + " WHERE player_name = ?",
                statement -> {
                    statement.set(1, name);
                },
                SQLPlayerHistoricAdapter.class
        );
    }

    public void insertOne(PlayerHistoric historic) {
        this.executor().updateQuery(
                "REPLACE INTO " + TABLE + " VALUES(?, ?, ?, ?, ?)",
                statement -> {
                    statement.set(1, UUID.randomUUID().toString());
                    statement.set(2, historic.getPlayerName());
                    statement.set(3, historic.getFactionTag());
                    statement.set(4, historic.getFactionName());
                    statement.set(5, historic.getDateCreated());
                }
        );
    }

    private SQLExecutor executor() {
        return new SQLExecutor(FactionsHistoricPlugin.getInstance().getSqlConnector());
    }

}
