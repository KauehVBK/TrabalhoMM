package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.model.UsuarioModel;

public class UsuarioDAO {
	
	private String select = "SELECT * FROM tb_pokemon";
	private String insert = "INSERT INTO tb_pokemon(nome_pokemon, tipo_pokemon) VALUES (?,?)";
	
	private PreparedStatement pstSelect;
	private PreparedStatement pstInsert;
	
	public UsuarioDAO(Connection conec) throws SQLException{
		
		pstSelect = conec.prepareStatement(select);
		pstInsert = conec.prepareStatement(insert);
		
	}
	
	public void inserir(UsuarioModel model) throws SQLException{
		pstInsert.clearParameters();
		pstInsert.setString(1, model.getNome());
		pstInsert.setString(2, model.getTipo());
		pstInsert.execute();
	}

}
