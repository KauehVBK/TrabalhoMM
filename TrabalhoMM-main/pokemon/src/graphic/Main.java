package graphic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.connection;

public class Main {

    private static void updateTotal(Connection connc, ArrayList<Pokemon> pokemon) throws SQLException {
        // Mapa para contar os Pokémon por tipo
        Map<String, Integer> quantType = new HashMap<>();
        // Mapa para contar apenas os Pokémon duplicados por tipo
        Map<String, Integer> dupType = new HashMap<>();

        // Primeira passagem: Calcular quantType e dupType
        for (Pokemon p : pokemon) {
            String type = p.gettype();

            // Conta o total de Pokémon por tipo
            quantType.put(type, quantType.getOrDefault(type, 0) + 1);

            // Conta apenas os Pokémon duplicados (mesmo nome, mas diferentes IDs)
            if (p.isDup()) {
                dupType.put(type, dupType.getOrDefault(type, 0) + 1);
            }
        }

        // Atualizar a tabela pokemon_totalizador
        String insertOrUpdateTotalizador = "INSERT INTO pokemon_totalizador (type, quant, quantdup) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE quant = VALUES(quant), quantdup = VALUES(quantdup)";
        PreparedStatement totalizador = connc.prepareStatement(insertOrUpdateTotalizador);

        // Passar por cada tipo de Pokémon e atualizar as contagens na tabela pokemon_totalizador
        for (Map.Entry<String, Integer> entry : quantType.entrySet()) {
            String type = entry.getKey();
            int quant = entry.getValue(); // Total de Pokémon desse tipo
            int quantDup = dupType.getOrDefault(type, 0); // Contagem de Pokémon duplicados

            // Configurar e executar a atualização na tabela
            totalizador.setString(1, type);
            totalizador.setInt(2, quant);
            totalizador.setInt(3, quantDup);
            totalizador.executeUpdate();
        }

        totalizador.close();
    }

    public static void main(String[] args) throws SQLException {
        Connection conec = null;
        PreparedStatement select = null;
        PreparedStatement insertEletrico = null;
        PreparedStatement insertFogo = null;
        PreparedStatement insertVoador = null;
        PreparedStatement insertDeletados = null;
        PreparedStatement deletePokemon = null;
        ResultSet result = null;

        try {
            conec = connection.getConnection("localhost", "3306", "pokedex", "root", "2402");

            // Preparando consulta e declarações SQL
            String selectQuery = "SELECT * FROM pokemon";
            select = conec.prepareStatement(selectQuery);
            result = select.executeQuery();

            String insertE = "INSERT INTO pokemon_eletrico (id, nome, type) VALUES (?, ?, ?)";
            insertEletrico = conec.prepareStatement(insertE);
            String insertF = "INSERT INTO pokemon_fogo (id, nome, type) VALUES (?, ?, ?)";
            insertFogo = conec.prepareStatement(insertF);
            String insertV = "INSERT INTO pokemon_voador (id, nome, type) VALUES (?, ?, ?)";
            insertVoador = conec.prepareStatement(insertV);
            String insertD = "INSERT INTO pokemon_deletados (id, nome, type) VALUES (?, ?, ?)";
            insertDeletados = conec.prepareStatement(insertD);

            // SQL para excluir duplicados
            String deleteDuplicateQuery = "DELETE FROM pokemon WHERE nome = ? AND id != ?";
            deletePokemon = conec.prepareStatement(deleteDuplicateQuery);

            // HashMap para rastrear duplicados e garantir que apenas uma cópia permaneça
            HashMap<String, Integer> seenPokemon = new HashMap<>();
            ArrayList<Pokemon> dataToMove = new ArrayList<>();

            // Primeira passagem: Leitura e identificação dos Pokémon
            while (result.next()) {
                int id = result.getInt("id");
                String nome = result.getString("nome");
                String type = result.getString("type");

                if (seenPokemon.containsKey(nome)) {
                    // Se o nome já foi visto, é um duplicado
                    dataToMove.add(new Pokemon(id, nome, type, true));
                } else {
                    // Marca o primeiro Pokémon como não-duplicado e armazena o id
                    seenPokemon.put(nome, id);
                    dataToMove.add(new Pokemon(id, nome, type, false));
                }
            }

            // Atualiza a tabela totalizadora antes de deletar os duplicados
            updateTotal(conec, dataToMove);  // Chama a atualização aqui

            // Segunda passagem: Processamento dos Pokémon e movimentação dos duplicados
            for (Pokemon p : dataToMove) {
                int id = p.getId();
                String nome = p.getPokemon();
                String type = p.gettype();

                if (p.isDup()) {
                    // Move um Pokémon duplicado para a tabela `pokemon_deletados`
                    insertDeletados.setInt(1, id);
                    insertDeletados.setString(2, nome);
                    insertDeletados.setString(3, type);
                    insertDeletados.executeUpdate();

                    // Remove todas as cópias desse Pokémon, exceto a primeira
                    deletePokemon.setString(1, nome);
                    deletePokemon.setInt(2, seenPokemon.get(nome)); // mantém a primeira cópia
                    deletePokemon.executeUpdate();
                } else {
                    // Insere o Pokémon na tabela correspondente
                    switch (type.toLowerCase()) {
                        case "elétrico":
                            insertEletrico.setInt(1, id);
                            insertEletrico.setString(2, nome);
                            insertEletrico.setString(3, type);
                            insertEletrico.executeUpdate();
                            break;
                        case "fogo":
                            insertFogo.setInt(1, id);
                            insertFogo.setString(2, nome);
                            insertFogo.setString(3, type);
                            insertFogo.executeUpdate();
                            break;
                        case "voador":
                            insertVoador.setInt(1, id);
                            insertVoador.setString(2, nome);
                            insertVoador.setString(3, type);
                            insertVoador.executeUpdate();
                            break;
                    }
                }
            }

            System.out.println("Processamento concluído.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) result.close();
                if (select != null) select.close();
                if (insertEletrico != null) insertEletrico.close();
                if (insertFogo != null) insertFogo.close();
                if (insertVoador != null) insertVoador.close();
                if (insertDeletados != null) insertDeletados.close();
                if (deletePokemon != null) deletePokemon.close();
                if (conec != null) conec.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}

class Pokemon {
    private int id;
    private String pokemon;
    private String type;
    private boolean Dup;

    public Pokemon(int id, String pokemon, String type, boolean dup) {
        this.id = id;
        this.pokemon = pokemon;
        this.type = type;
        this.Dup = dup;
    }

    public int getId() {
        return id;
    }

    public String getPokemon() {
        return pokemon;
    }

    public String gettype() {
        return type;
    }

    public boolean isDup() {
        return Dup;
    }
}
