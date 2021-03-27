package ru.kargond.namechanger.mysql;

import com.zaxxer.hikari.HikariDataSource;
import ru.kargond.namechanger.main.NameChanger;
import ru.kargond.namechanger.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MySQL {

    private static ExecutorService exec = Executors.newSingleThreadExecutor();
    private HikariDataSource ds;

    private NameChanger nameChanger;

    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS `names` (" +
            "`id` BINARY(16) NOT NULL," +
            "`mask` VARCHAR(16) NOT NULL," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE INDEX `id` (`id`)" +
            ")" +
            "COLLATE='utf8_general_ci'" +
            "ENGINE=InnoDB;";

    private static final String SQL_GET_MASK = "SELECT `mask` FROM `names` WHERE `id` = UNHEX(?)";
    private static final String SQL_REMOVE_MASK = "DELETE FROM `names` WHERE `id` = UNHEX(?)";
    private static final String SQL_UPDATE_MASK = "INSERT INTO `names` (id, mask) VALUES (UNHEX(?), ?) " +
            "ON DUPLICATE KEY UPDATE mask = ?";

    public MySQL(String ip, String userName, String password, String db, NameChanger nameChanger) {
        this.nameChanger = nameChanger;
        ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://" + ip + "/" + db
                + "?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&maxReconnects=10");
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setMaximumPoolSize(2);
        ds.setLeakDetectionThreshold(60 * 1000);

        createTable();
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void createTable() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_IF_NOT_EXISTS)) {
                statement.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadMask(UUID uuid, String name) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_GET_MASK)) {
                statement.setString(1, Utils.trimUUID(uuid));
                ResultSet result = statement.executeQuery();
                String displayName = null;
                if (result.next()) {
                    displayName = result.getString(1);
                }

                nameChanger.getMaskManager().createMaskedPlayer(uuid, displayName, name);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removeMask(UUID uuid) {
        exec.submit(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(SQL_REMOVE_MASK)) {
                    statement.setString(1, Utils.trimUUID(uuid));
                    statement.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void updateMask(UUID uuid, String mask) {
        exec.submit(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_MASK)) {
                    statement.setString(1, Utils.trimUUID(uuid));
                    statement.setString(2, mask);
                    statement.setString(3, mask);
                    statement.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
