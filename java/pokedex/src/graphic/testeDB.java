package graphic;

import java.sql.Connection;
import java.sql.SQLException;

import database.ConnectionFactory;
import database.dao.UsuarioDAO;
import database.model.UsuarioModel;

public class testeDB {

	public static void main(String[] args) throws SQLException {
		
		Connection conec = ConnectionFactory.getConnection("localhost", "5432", "postgres", "admin", "FomeMundial");
		
		if(conec != null) {
			System.out.println("Sucexo!");
			
			UsuarioModel model = new UsuarioModel();
			model.setUsuario("HWFNOINFNIONOIWREFNIOWFNUIOE");
			model.setSenha("HWFNOINFNIONOIWREFNIOWFNUIOEUHWEFIUWE");
			
			System.out.println("Veio até aqui");
			new UsuarioDAO(conec).inserir(model);
			System.out.println("Passou aqui");
			
		}else {
			System.out.println("não");
		}

	}

}
