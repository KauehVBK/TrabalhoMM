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

            // Primeira passagem: Leitura e identificação dos Pokémon
            ArrayList<Pokemon> pokemonList = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                String nome = result.getString("nome");
                String type = result.getString("type");
                pokemonList.add(new Pokemon(id, nome, type, false));
            }

            // Segunda passagem: Processamento dos Pokémon
            ArrayList<Pokemon> dataToMove = new ArrayList<>();
            for (Pokemon p : pokemonList) {
                String nome = p.getPokemon();
                int id = p.getId();
                String type = p.gettype();

                // Verifica se já vimos o nome do Pokémon
                if (seenPokemon.containsKey(nome)) {
                    // Se for duplicado, move uma cópia para pokemon_deletados e remove o resto
                    insertDeletados.setInt(1, id);
                    insertDeletados.setString(2, nome);
                    insertDeletados.setString(3, type);
                    insertDeletados.executeUpdate();

                    // Remove todas as cópias desse Pokémon da tabela pokemon, exceto o primeiro
                    deletePokemon.setString(1, nome);
                    deletePokemon.setInt(2, seenPokemon.get(nome)); // mantém a primeira cópia
                    deletePokemon.executeUpdate();

                    // Marca este Pokémon como duplicado
                    dataToMove.add(new Pokemon(id, nome, type, true));
                } else {
                    // Insere nas tabelas correspondentes e guarda o id da primeira cópia
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

                    // Guarda o id para não deletar a primeira cópia do Pokémon
                    seenPokemon.put(nome, id);
                    dataToMove.add(new Pokemon(id, nome, type, false));
                }
            }

            // Atualiza a tabela totalizadora
            updateTotal(conec, dataToMove);

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


    private static void updateTotal(Connection conn, ArrayList<Pokemon> pokemonList) throws SQLException {
        Map<String, Integer> quantType = new HashMap<>(); // Total de Pokémon por tipo
        Map<String, Integer> dupType = new HashMap<>();   // Total de Pokémon duplicados por tipo

        // Conjunto para armazenar nomes únicos e detectar duplicados
        HashMap<String, String> uniqueNames = new HashMap<>(); // Armazena nome -> tipo
        HashMap<String, Integer> duplicateCount = new HashMap<>(); // Armazena contagem de duplicados por tipo

        // Itera sobre todos os Pokémon e registra tipos e duplicados
        for (Pokemon p : pokemonList) {
            String type = p.gettype();
            String nome = p.getPokemon();

            // Atualiza a contagem total de cada tipo
            quantType.put(type, quantType.getOrDefault(type, 0) + 1);

            // Verifica se o nome já foi visto anteriormente
            if (uniqueNames.containsKey(nome)) {
                // Se já foi visto e pertence ao mesmo tipo, incrementa duplicados para este tipo
                if (uniqueNames.get(nome).equals(type)) {
                    dupType.put(type, dupType.getOrDefault(type, 0) + 1);
                }
            } else {
                // Caso contrário, adiciona o nome ao conjunto de únicos
                uniqueNames.put(nome, type);
            }
        }

        // Prepara a query de inserção ou atualização na tabela pokemon_totalizador
        String insertOrUpdateTotalizador = "INSERT INTO pokemon_totalizador (type, quant, quantdup) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE quant = VALUES(quant), quantdup = VALUES(quantdup)";
        PreparedStatement totalizador = conn.prepareStatement(insertOrUpdateTotalizador);

        // Insere os dados em pokemon_totalizador
        for (Map.Entry<String, Integer> entry : quantType.entrySet()) {
            String type = entry.getKey();
            int quant = entry.getValue();
            int quantDup = dupType.getOrDefault(type, 0); // Usa apenas duplicados reais

            totalizador.setString(1, type);
            totalizador.setInt(2, quant);
            totalizador.setInt(3, quantDup);
            totalizador.executeUpdate();
        }

        totalizador.close();
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
