package org.giancpz.gprivateworlds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.giancpz.gprivateworlds.Internal.PlayerInfo;
import org.giancpz.gprivateworlds.Internal.WorldInfo;
import org.giancpz.gprivateworlds.Utils2.Print;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class sql
{
    private static final String FIND_PLAYER_BY_UUID = "SELECT * FROM gprivateworlds_players WHERE " + SaveLoadData.PLAYER_UUID_1 + " = ?";
    private static final String FIND_PLAYER_BY_NAME = "SELECT * FROM gprivateworlds_players WHERE " + SaveLoadData.PLAYER_NAME_2 + " = ?";
    private static final String INSERT_PLAYER = "INSERT INTO gprivateworlds_players(" + SaveLoadData.PLAYER_UUID_1 + ", " + SaveLoadData.PLAYER_NAME_2 + ", " + SaveLoadData.MEMBER_UUID_3 + ") VALUES(?,?,?)";
    private static final String FIND_WORLD_BY_UUID = "SELECT * FROM gprivateworlds_worlds WHERE " + SaveLoadData.WORLD_UUID_1 + " = ?";
    private static final String INSERT_WORLD = "INSERT INTO gprivateworlds_worlds(" + SaveLoadData.WORLD_UUID_1 + ", " + SaveLoadData.OWNER_UUID_2 + ", " + SaveLoadData.OWNER_NAME_3 + ", " + SaveLoadData.MEMBERS_UUID_4 + ", " + SaveLoadData.SPAWN_LOCATION_5 + ", " + SaveLoadData.CREATION_DATE_6 + ", " + SaveLoadData.GAMERULES_7 + ", " + SaveLoadData.CHECKSUM_8 + ") VALUES(?,?,?,?,?,?,?,?)";

    private static final String UPDATE_WORLD = "UPDATE gprivateworlds_worlds "
            + "SET "
            + SaveLoadData.OWNER_UUID_2 + " = ?, "
            + SaveLoadData.OWNER_NAME_3 + " = ?, "
            + SaveLoadData.MEMBERS_UUID_4 + " = ?, "
            + SaveLoadData.SPAWN_LOCATION_5 + " = ?, "
            + SaveLoadData.CREATION_DATE_6 + " = ?, "
            + SaveLoadData.GAMERULES_7 + " = ?, "
            + SaveLoadData.CHECKSUM_8 + " = ? "
            + "WHERE " + SaveLoadData.WORLD_UUID_1 + " = ?";

    private static final String UPDATE_PLAYER = "UPDATE gprivateworlds_players SET "
            + SaveLoadData.PLAYER_NAME_2 + " = ?, "
            + SaveLoadData.MEMBER_UUID_3 + " = ? "
            + "WHERE " + SaveLoadData.PLAYER_UUID_1 + " = ?";


    HikariDataSource dataSource;
    public void initializeDatabase()
    {
        try
        {
            HikariConfig config = new HikariConfig();
            if(PluginConfig.Options().playerstoragemethod == PluginConfig.Storagemethod.MARIADB)
            {
                Print.info("Initializing MariaDB database...");
                String address = PluginConfig.Mysql.getConfig().address + ":" + PluginConfig.Mysql.getConfig().port;
                String url = String.format("jdbc:mysql://%s/%s?useSSL=%s", address, PluginConfig.Options().mysql.database, PluginConfig.Options().mysql.ssl);
                config.setJdbcUrl(url);
                config.setUsername(PluginConfig.Mysql.getConfig().username);
                config.setPassword(PluginConfig.Mysql.getConfig().password);
                config.setMaximumPoolSize(PluginConfig.Mysql.getConfig().maxpoolsize);

                dataSource = new HikariDataSource(config);

                String sql_players = "CREATE TABLE IF NOT EXISTS `gprivateworlds_players` ("
                        + "`" + SaveLoadData.PLAYER_UUID_1 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.PLAYER_NAME_2 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.MEMBER_UUID_3 + "` VARCHAR(64) NOT NULL, "
                        + "PRIMARY KEY (`player_uuid`)) ENGINE = InnoDB;";

                String sql_worlds = "CREATE TABLE IF NOT EXISTS `gprivateworlds_worlds` ("
                        + "`" + SaveLoadData.WORLD_UUID_1 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.OWNER_UUID_2 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.OWNER_NAME_3 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.MEMBERS_UUID_4 + "` VARCHAR(256) NOT NULL, "
                        + "`" + SaveLoadData.SPAWN_LOCATION_5 + "` VARCHAR(64) NOT NULL, "
                        + "`" + SaveLoadData.CREATION_DATE_6 + "` VARCHAR(16) NOT NULL, "
                        + "`" + SaveLoadData.GAMERULES_7 + "` VARCHAR(256) NOT NULL, "
                        + "`" + SaveLoadData.CHECKSUM_8 + "` VARCHAR(256) NOT NULL, "
                        + "PRIMARY KEY (`" + SaveLoadData.WORLD_UUID_1 + "`)) ENGINE = InnoDB;";

                    Statement statement = dataSource.getConnection().createStatement();
                    statement.execute(sql_players);
                    statement.execute(sql_worlds);
            }
            else
            {
                Print.info("Initializing H2 database...");
                config.setJdbcUrl("jdbc:h2:./GPrivateWorlds/local/h2");
                config.setUsername("sa");
                config.setPassword("");
                config.setDriverClassName("org.h2.Driver");
                dataSource = new HikariDataSource(config);

                String sql_players = "CREATE TABLE IF NOT EXISTS gprivateworlds_players ("
                        + SaveLoadData.PLAYER_UUID_1 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.PLAYER_NAME_2 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.MEMBER_UUID_3 + " VARCHAR(64) NOT NULL,"
                        + "PRIMARY KEY (player_uuid));";

                String sql_worlds = "CREATE TABLE IF NOT EXISTS gprivateworlds_worlds ("
                        + SaveLoadData.WORLD_UUID_1 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.OWNER_UUID_2 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.OWNER_NAME_3 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.MEMBERS_UUID_4 + " VARCHAR(256) NOT NULL,"
                        + SaveLoadData.SPAWN_LOCATION_5 + " VARCHAR(64) NOT NULL,"
                        + SaveLoadData.CREATION_DATE_6 + " VARCHAR(16) NOT NULL,"
                        + SaveLoadData.GAMERULES_7 + " VARCHAR(256) NOT NULL,"
                        + SaveLoadData.CHECKSUM_8 + " VARCHAR(256) NOT NULL,"
                        + "PRIMARY KEY (" + SaveLoadData.WORLD_UUID_1 + "));";

                Statement statement = dataSource.getConnection().createStatement();
                statement.execute(sql_players);
                statement.execute(sql_worlds);
            }

        }catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            if (e.getMessage().contains("Access denied for user")) {
                Print.error("incorrect username or password");
            } else {
                Print.error("Error connecting to the database");
            }
        }
    }

    public void CloseConnection()
    {
        try {
            dataSource.getConnection().close();
        }catch (SQLException e){
            Print.error("Error closing the connection");
        }
    }

    public PlayerInfo FindPlayerByUUID(String uuid) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PLAYER_BY_UUID)) {

            statement.setString(1, uuid);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new PlayerInfo(
                            results.getString(SaveLoadData.PLAYER_NAME_2), UUID.fromString(results.getString(SaveLoadData.PLAYER_UUID_1)),
                            UUID.fromString(results.getString(SaveLoadData.MEMBER_UUID_3)));
                }
            }
            return null;
        } catch (SQLException e) {
            Print.error("Error finding player by uuid");
            return null;
        }
    }

    public PlayerInfo FindPlayerByName(String name) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PLAYER_BY_NAME)) {

            statement.setString(1, name);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new PlayerInfo(
                            results.getString(SaveLoadData.PLAYER_NAME_2), UUID.fromString(results.getString(SaveLoadData.PLAYER_UUID_1)),
                            Utils.WorldStringToUUId(results.getString(SaveLoadData.MEMBER_UUID_3)));
                }
            }
            return null;
        } catch (SQLException e) {
            Print.error("Error finding player by name: " + name);
            return null;
        }
    }

    public void InsertPlayer(PlayerInfo player)
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER)) {

            statement.setString(1, player.playerUID.toString());
            statement.setString(2, player.playerName);
            statement.setString(3, Utils.WorldUUIDToString(player.WorldUUID));

            statement.executeUpdate();
        }catch (Exception e){
            System.out.println("SQLException: " + e.getMessage());
            Print.error("Error inserting the player");
        }
    }

    public void UpdatePlayer(PlayerInfo playerinfo)
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER)) {

            //statement.setString(1, worldInfo.worldUUID);
            statement.setString(1, playerinfo.playerName);
            statement.setString(2, Utils.WorldUUIDToString(playerinfo.WorldUUID));
            statement.setString(3, playerinfo.playerUID.toString());

            int filasActualizadas =  statement.executeUpdate();
            System.out.println("Filas actualizadas: " + filasActualizadas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WorldInfo FindWorldByUUID(UUID uuid) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_WORLD_BY_UUID)) {
            statement.setString(1, uuid.toString());

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    WorldInfo worldInfo = new WorldInfo();

                    worldInfo.uuid = UUID.fromString(results.getString(SaveLoadData.WORLD_UUID_1));
                    worldInfo.playerOwnerUID = UUID.fromString(results.getString(SaveLoadData.OWNER_UUID_2));
                    worldInfo.playerOwnerDisplayName = results.getString(SaveLoadData.OWNER_NAME_3);

                    /*
                    List<String> stringList = new ArrayList<>(Arrays.asList(results.getString(SaveLoadData.MEMBERS_UUID_4).split(";")));

                    worldInfo.members = stringList.stream()
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                     */

                    //worldInfo.members = new ArrayList<>(Arrays.asList(results.getString(SaveLoadData.MEMBERS_UUID_4).split(";")))

                    worldInfo.spawnLocation.toObject(results.getString(SaveLoadData.SPAWN_LOCATION_5));
                    worldInfo.creationDate.toObject(results.getString(SaveLoadData.CREATION_DATE_6));
                    //worldInfo.gamerules.optionsString = results.getString(SaveLoadData.OPTIONS_7);
                    worldInfo.checkSum = results.getString(SaveLoadData.CHECKSUM_8);

                    System.out.println("deded edde " + worldInfo.uuid);
                    return worldInfo;
                }
            }
            return null;
        } catch (SQLException e) {
            Main.Singleton().getLogger().severe(e.getMessage());
            return null;
        }
    }

    public void UpdateWorld(WorldInfo worldInfo)
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_WORLD)) {

            //statement.setString(1, worldInfo.worldUUID);
            statement.setString(1, worldInfo.playerOwnerUID.toString());
            statement.setString(2, worldInfo.playerOwnerDisplayName);

            // Convertir a lista de String
            List<String> stringList = worldInfo.members.stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());

            statement.setString(3, String.join(";", stringList));
            statement.setString(4, worldInfo.spawnLocation.toText());
            statement.setString(5, worldInfo.creationDate.toText());
            statement.setString(6, "options here");
            statement.setString(7, worldInfo.checkSum);
            statement.setString(8, worldInfo.uuid.toString());

            int filasActualizadas =  statement.executeUpdate();
            System.out.println("Filas actualizadas: " + filasActualizadas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertWorld(WorldInfo worldInfo) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_WORLD)) {

            statement.setString(1, worldInfo.uuid.toString());
            statement.setString(2, worldInfo.playerOwnerUID.toString());
            statement.setString(3, worldInfo.playerOwnerDisplayName);

            statement.setString(4, String.join(";",
                    worldInfo.members.stream()
                            .map(UUID::toString) // Convierte cada UUID a String
                            .toArray(String[]::new) // Convierte la lista a un array
            ));

            statement.setString(5, worldInfo.spawnLocation.toText());
            statement.setString(6, worldInfo.creationDate.toText());
            statement.setString(7, "options here");
            statement.setString(8, worldInfo.checkSum);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            Print.error("Error inserting world");
        }
    }
}
