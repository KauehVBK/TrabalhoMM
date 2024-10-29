package graphic;

import java.sql.Connection;
import java.sql.SQLException;

import database.ConnectionFactory;
import database.dao.UsuarioDAO;
import database.model.UsuarioModel;

public class testeDB {

	public static void main(String[] args) throws SQLException {
		
		Connection conec = ConnectionFactory.getConnection("localhost", "3306", "pokedex", "root", "unesc");
		
		if(conec != null) {
			System.out.println("Sucexo!");
						
			UsuarioModel model = new UsuarioModel();
			
			
			//new UsuarioDAO(conec).inserir(model);
			//System.out.println("Passou aqui");
			
		}else {
			System.out.println("não");
		}

	}

}
