// ============================================================
// ARQUIVO: dao/EquipeDAO.java
// ============================================================
package dao;

import exception.DAOException;
import model.Equipe;
import model.Usuario;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // INSERT
    public void inserir(Equipe equipe) {
        String sql = "INSERT INTO equipes (nome, descricao) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipe.getNome());
            ps.setString(2, equipe.getDescricao());
            ps.executeUpdate();

            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) equipe.setId(chaves.getInt(1));

            // Insere os membros na tabela de relacionamento
            for (Usuario membro : equipe.getMembros()) {
                adicionarMembro(equipe.getId(), membro.getId());
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir equipe: " + e.getMessage(), e);
        }
    }

    // SELECT todos
    public List<Equipe> listarTodos() {
        String sql = "SELECT * FROM equipes ORDER BY nome";
        List<Equipe> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipe e = mapearResultado(rs);
                e.setMembros(listarMembros(e.getId()));
                lista.add(e);
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar equipes: " + e.getMessage(), e);
        }
        return lista;
    }

    // SELECT por ID
    public Equipe buscarPorId(int id) {
        String sql = "SELECT * FROM equipes WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Equipe e = mapearResultado(rs);
                e.setMembros(listarMembros(e.getId()));
                return e;
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar equipe: " + e.getMessage(), e);
        }
        return null;
    }

    // UPDATE
    public void atualizar(Equipe equipe) {
        String sql = "UPDATE equipes SET nome=?, descricao=? WHERE id=?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipe.getNome());
            ps.setString(2, equipe.getDescricao());
            ps.setInt(3, equipe.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar equipe: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void excluir(int id) {
        String sql = "DELETE FROM equipes WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir equipe: " + e.getMessage(), e);
        }
    }

    // Adicionar membro à equipe
    public void adicionarMembro(int equipeId, int usuarioId) {
        String sql = "INSERT IGNORE INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipeId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao adicionar membro: " + e.getMessage(), e);
        }
    }

    // Remover membro da equipe
    public void removerMembro(int equipeId, int usuarioId) {
        String sql = "DELETE FROM equipe_membros WHERE equipe_id = ? AND usuario_id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipeId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao remover membro: " + e.getMessage(), e);
        }
    }

    // Listar membros de uma equipe
    public List<Usuario> listarMembros(int equipeId) {
        String sql = "SELECT u.* FROM usuarios u " +
                     "JOIN equipe_membros em ON u.id = em.usuario_id " +
                     "WHERE em.equipe_id = ? ORDER BY u.nome";
        List<Usuario> membros = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) membros.add(usuarioDAO.buscarPorId(rs.getInt("id")));

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar membros: " + e.getMessage(), e);
        }
        return membros;
    }

    private Equipe mapearResultado(ResultSet rs) throws SQLException {
        Equipe e = new Equipe();
        e.setId(rs.getInt("id"));
        e.setNome(rs.getString("nome"));
        e.setDescricao(rs.getString("descricao"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) e.setCriadoEm(ts.toLocalDateTime());
        return e;
    }
}
