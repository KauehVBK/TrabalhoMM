package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection
{

    public static Connection getConnection(String endeIP, String porta, String nomeDB, String user, String pass) throws SQLException {

        return DriverManager.getConnection("jdbc:mysql://" + endeIP + ":" + porta + "/" + nomeDB, user, pass);

    }

}
