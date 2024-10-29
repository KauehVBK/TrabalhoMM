package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.model.UsuarioModel;

public class UsuarioDAO {
	
	private String select = "SELECT * FROM tb_user";
	private String insert = "INSERT INTO tb_user(user_nome, user_senha) VALUES (?,?)";
	
	private PreparedStatement pstSelect;
	private PreparedStatement pstInsert;
	
	public UsuarioDAO(Connection conec) throws SQLException{
		
		pstSelect = conec.prepareStatement(select);
		pstInsert = conec.prepareStatement(insert);
		
	}
	
	public void inserir(UsuarioModel model) throws SQLException{
		pstInsert.clearParameters();
		pstInsert.setString(1, model.getUsuario());
		pstInsert.setString(2, model.getSenha());
		pstInsert.execute();
	}

}
