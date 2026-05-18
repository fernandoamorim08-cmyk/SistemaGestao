// ============================================================
// ARQUIVO: dao/UsuarioDAO.java
//
// CONCEITO - PADRÃO DAO (Data Access Object):
//   O padrão DAO separa a lógica de acesso ao banco de dados
//   do resto da aplicação. Em vez de escrever SQL em qualquer
//   lugar do código, concentramos tudo aqui nesta classe.
//
//   Vantagens:
//   ✓ Organização — código SQL fica isolado
//   ✓ Manutenção — se mudar o banco, só altera o DAO
//   ✓ Reutilização — outras classes chamam métodos simples
//
// OPERAÇÕES CRUD:
//   C = Create (INSERT)
//   R = Read   (SELECT)
//   U = Update (UPDATE)
//   D = Delete (DELETE)
//
// CONCEITO - try-with-resources:
//   try (PreparedStatement ps = conn.prepareStatement(...)) { }
//   O recurso (PreparedStatement) é fechado automaticamente
//   ao sair do bloco try, mesmo se ocorrer uma exceção.
// ============================================================
package dao;

import exception.DAOException;
import enums.PerfilUsuario;
import model.Usuario;
import util.Conexao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por todas as operações de banco de dados
 * relacionadas à entidade Usuario.
 */
public class UsuarioDAO {

    // --------------------------------------------------------
    // INSERT — Salva um novo usuário no banco de dados
    // --------------------------------------------------------
    /**
     * Insere um novo usuário no banco de dados.
     * @param usuario — objeto com os dados a serem salvos
     * @throws DAOException se ocorrer algum erro no banco
     */
    public void inserir(Usuario usuario) {
        // SQL com "?" são parâmetros (PreparedStatement evita SQL Injection)
        String sql = "INSERT INTO usuarios (nome, cpf, email, cargo, login, senha, perfil) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // try-with-resources: fecha automaticamente os recursos
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Define os valores para cada "?" na ordem
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getLogin());
            ps.setString(6, usuario.getSenha());   // Em produção: usar hash (BCrypt)
            ps.setString(7, usuario.getPerfil().name());

            // Executa o comando SQL
            ps.executeUpdate();

            // Recupera o ID gerado pelo banco (AUTO_INCREMENT)
            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) {
                usuario.setId(chaves.getInt(1));
            }

        } catch (SQLException e) {
            // Relança como DAOException com mensagem amigável
            throw new DAOException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // SELECT — Busca um usuário pelo ID
    // --------------------------------------------------------
    /**
     * Busca um usuário pelo seu ID.
     * @param id — identificador do usuário
     * @return Usuario encontrado, ou null se não existir
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            // ResultSet contém o resultado da consulta (como uma tabela)
            ResultSet rs = ps.executeQuery();

            // Se houver resultado, mapeia para um objeto Usuario
            if (rs.next()) {
                return mapearResultado(rs);
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }

        return null;  // Retorna null se não encontrar
    }

    // --------------------------------------------------------
    // SELECT — Busca todos os usuários
    // --------------------------------------------------------
    /**
     * Retorna todos os usuários cadastrados.
     * @return Lista de usuários (pode estar vazia, nunca null)
     */
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios WHERE ativo = 1 ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();  // Lista para guardar os resultados

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Percorre cada linha do resultado
            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return lista;
    }

    // --------------------------------------------------------
    // SELECT — Busca por login e senha (autenticação)
    // --------------------------------------------------------
    /**
     * Autentica um usuário pelo login e senha.
     * @param login — login do usuário
     * @param senha — senha digitada
     * @return Usuario autenticado, ou null se credenciais inválidas
     */
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ? AND ativo = 1";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, senha);  // Em produção: comparar hash

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        return null;
    }

    // --------------------------------------------------------
    // SELECT — Busca usuários por perfil
    // --------------------------------------------------------
    public List<Usuario> listarPorPerfil(PerfilUsuario perfil) {
        String sql = "SELECT * FROM usuarios WHERE perfil = ? AND ativo = 1 ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, perfil.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar por perfil: " + e.getMessage(), e);
        }

        return lista;
    }

    // --------------------------------------------------------
    // UPDATE — Atualiza dados de um usuário
    // --------------------------------------------------------
    /**
     * Atualiza os dados de um usuário existente.
     * @param usuario — objeto com os dados atualizados (deve ter ID)
     */
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome=?, cpf=?, email=?, cargo=?, perfil=? WHERE id=?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getPerfil().name());
            ps.setInt(6, usuario.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // DELETE lógico — Desativa (não exclui fisicamente)
    // --------------------------------------------------------
    /**
     * Desativa um usuário (exclusão lógica — ativo = 0).
     * Em vez de remover do banco, marcamos como inativo.
     * Isso preserva histórico e referências.
     */
    public void desativar(int id) {
        String sql = "UPDATE usuarios SET ativo = 0 WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao desativar usuário: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // BUSCA — algoritmo de busca por nome (contém texto)
    // --------------------------------------------------------
    /**
     * Busca usuários cujo nome contenha o texto informado.
     * CONCEITO - ALGORITMO DE BUSCA:
     *   Usamos o operador LIKE do SQL com % (curinga).
     *   Ex: buscarPorNome("ana") encontra "Ana", "Mariana", "Silvana".
     */
    public List<Usuario> buscarPorNome(String nome) {
        String sql = "SELECT * FROM usuarios WHERE nome LIKE ? AND ativo = 1 ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // % é curinga: %texto% = qualquer coisa antes e depois
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar por nome: " + e.getMessage(), e);
        }

        return lista;
    }

    // --------------------------------------------------------
    // MÉTODO PRIVADO AUXILIAR — mapeia ResultSet → Usuario
    // Evita repetição de código (princípio DRY: Don't Repeat Yourself)
    // --------------------------------------------------------
    /**
     * Converte uma linha do ResultSet em um objeto Usuario.
     * Este método é privado — só usado internamente pelo DAO.
     */
    private Usuario mapearResultado(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCpf(rs.getString("cpf"));
        u.setEmail(rs.getString("email"));
        u.setCargo(rs.getString("cargo"));
        u.setLogin(rs.getString("login"));
        u.setSenha(rs.getString("senha"));
        u.setAtivo(rs.getBoolean("ativo"));
        // Converte String do banco para o Enum correspondente
        u.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
        // Converte Timestamp do banco para LocalDateTime
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) u.setCriadoEm(ts.toLocalDateTime());
        return u;
    }
}
