package net.noscape.project.supremetags.storage;

import lombok.Getter;
import net.noscape.project.supremetags.SupremeTags;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class Database {

    private final String ConnectionURL;

    public Database(String connectionURL) {
        ConnectionURL = connectionURL;

        this.initialiseDatabase();
    }

    public Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(SupremeTags.getConnectionURL());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("H2: Something wrong with connecting to h2-sql, contact the developer if you see this.");
        }

        return connection;
    }

    public void initialiseDatabase() {
        PreparedStatement preparedStatement;

        String userTable = "CREATE TABLE IF NOT EXISTS `users` " +
                "(`Name` VARCHAR(100), `UUID` VARCHAR(100) primary key, `Active` VARCHAR(100))";
        try {
            preparedStatement = getConnection().prepareStatement(userTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}