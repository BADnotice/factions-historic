package io.github.badnotice.factionshistoric.dao.factory;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import io.github.badnotice.factionshistoric.FactionsHistoricPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public final class SQLDatabaseFactory {

    public static SQLConnector createSqlConnector(ConfigurationSection section) {
        String databaseType = section.getString("type");

        ConfigurationSection typeSection = section.getConfigurationSection(databaseType);
        switch (databaseType) {
            case "sqlite":
                return buildSqliteDatabaseType(typeSection).connect();
            case "mysql":
                return buildMysqlDatabaseType(typeSection).connect();
            default:
                throw new UnsupportedOperationException("database type unsupported!");
        }
    }

    private static SQLiteDatabaseType buildSqliteDatabaseType(ConfigurationSection typeSection) {
        return SQLiteDatabaseType.builder()
                .file(new File(FactionsHistoricPlugin.getInstance().getDataFolder(), typeSection.getString("fileName")))
                .build();
    }

    private static MySQLDatabaseType buildMysqlDatabaseType(ConfigurationSection typeSection) {
        return MySQLDatabaseType.builder()
                .address(typeSection.getString("address"))
                .username(typeSection.getString("username"))
                .password(typeSection.getString("password"))
                .database(typeSection.getString("database"))
                .build();
    }

}
