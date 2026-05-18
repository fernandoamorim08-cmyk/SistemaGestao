// ============================================================
// ARQUIVO: util/Conexao.java  (VERSÃO CORRIGIDA)
//
// PROBLEMA CORRIGIDO:
//   O erro "Operation not allowed after ResultSet closed" ocorria
//   porque o padrão Singleton compartilhava UMA conexão entre
//   todos os DAOs. Quando o ProjetoDAO listava projetos e chamava
//   o UsuarioDAO para buscar o gerente de cada um, a segunda
//   query fechava o ResultSet da primeira — causando o erro.
//
// SOLUÇÃO:
//   Cada chamada a getConexao() abre uma conexão NOVA e
//   independente. O try-with-resources em cada DAO fecha a
//   conexão automaticamente ao terminar o bloco.
// ============================================================
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    private static final String HOST    = "localhost";
    private static final String PORTA   = "3306";
    private static final String BANCO   = "sistema_gestao";
    private static final String USUARIO = "root";       // seu usuário MySQL
    private static final String SENHA   = "123456";  // ← ALTERE AQUI

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO
            + "?useSSL=false"
            + "&serverTimezone=America/Sao_Paulo"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";

    private Conexao() {}

    /**
     * Abre e retorna uma NOVA conexão a cada chamada.
     * Use dentro de try-with-resources nos DAOs:
          *   try (Connection conn = Conexao.getConexao();
     *        PreparedStatement ps = conn.prepareStatement(sql)) {
     *       // conn fechada automaticamente ao sair do bloco
     *   }
     */
    public static Connection getConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);

            // Força seleção do banco — resolve "No database selected"
            try (Statement st = conn.createStatement()) {
                st.execute("USE " + BANCO);
            }

            System.out.println("✅ Conexão aberta com: " + BANCO);
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erro de conexão: " + e.getMessage());
        }
        return null;
    }

    public static void fecharConexao() {
        // Conexões fechadas automaticamente pelo try-with-resources
    }
}
