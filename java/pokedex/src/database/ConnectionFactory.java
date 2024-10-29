package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
		
		public static Connection getConnection(
				final String enderecoIP,
				final String enderecoPort,
				final String user,
				final String senha,
				final String nomeBanco
				) throws SQLException 
		{
			
			return DriverManager.getConnection
					(
							"jdbc:postgresql://"+enderecoIP+":"+enderecoPort+"/"+nomeBanco,
							user,
							senha
					);

		}
}
