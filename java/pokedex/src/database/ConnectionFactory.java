package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
		
		public static Connection getConnection(
				final String enderecoIP,
				final String enderecoPort,
				final String nomeBanco,
				final String user,
				final String senha
				
				) throws SQLException 
		{
			
			return DriverManager.getConnection
					(
							"jdbc:mysql://"+enderecoIP+":"+enderecoPort+"/"+nomeBanco,user,senha
					);

		}
}
