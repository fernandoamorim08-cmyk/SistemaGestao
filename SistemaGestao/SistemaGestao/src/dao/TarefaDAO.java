// ============================================================
// ARQUIVO: dao/TarefaDAO.java
// ============================================================
package dao;

import exception.DAOException;
import enums.Prioridade;
import enums.StatusTarefa;
import model.Projeto;
import model.Tarefa;
import model.Usuario;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ProjetoDAO projetoDAO = new ProjetoDAO();

    // INSERT
    public void inserir(Tarefa tarefa) {
        String sql = "INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id, " +
                     "data_limite, prioridade, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setInt(3, tarefa.getProjeto().getId());
            ps.setObject(4, tarefa.getResponsavel() != null ? tarefa.getResponsavel().getId() : null);
            ps.setDate(5, tarefa.getDataLimite() != null ? Date.valueOf(tarefa.getDataLimite()) : null);
            ps.setString(6, tarefa.getPrioridade().name());
            ps.setString(7, tarefa.getStatus().name());

            ps.executeUpdate();

            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) tarefa.setId(chaves.getInt(1));

        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir tarefa: " + e.getMessage(), e);
        }
    }

    // SELECT todos de um projeto
    public List<Tarefa> listarPorProjeto(int projetoId) {
        String sql = "SELECT * FROM tarefas WHERE projeto_id = ? ORDER BY prioridade DESC, data_limite";
        List<Tarefa> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearResultado(rs));

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar tarefas: " + e.getMessage(), e);
        }
        return lista;
    }

    // SELECT tarefas pendentes (relatório)
    public List<Tarefa> listarPendentes() {
        String sql = "SELECT t.*, p.nome as projeto_nome, u.nome as responsavel_nome " +
                     "FROM tarefas t " +
                     "JOIN projetos p ON t.projeto_id = p.id " +
                     "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                     "WHERE t.status IN ('PENDENTE', 'EM_ANDAMENTO') " +
                     "ORDER BY t.prioridade DESC, t.data_limite";
        List<Tarefa> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearResultado(rs));

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar tarefas pendentes: " + e.getMessage(), e);
        }
        return lista;
    }

    // SELECT por ID
    public Tarefa buscarPorId(int id) {
        String sql = "SELECT * FROM tarefas WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapearResultado(rs);

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar tarefa: " + e.getMessage(), e);
        }
        return null;
    }

    // UPDATE
    public void atualizar(Tarefa tarefa) {
        String sql = "UPDATE tarefas SET titulo=?, descricao=?, responsavel_id=?, " +
                     "data_limite=?, prioridade=?, status=? WHERE id=?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setObject(3, tarefa.getResponsavel() != null ? tarefa.getResponsavel().getId() : null);
            ps.setDate(4, tarefa.getDataLimite() != null ? Date.valueOf(tarefa.getDataLimite()) : null);
            ps.setString(5, tarefa.getPrioridade().name());
            ps.setString(6, tarefa.getStatus().name());
            ps.setInt(7, tarefa.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void excluir(int id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir tarefa: " + e.getMessage(), e);
        }
    }

    private Tarefa mapearResultado(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setPrioridade(Prioridade.valueOf(rs.getString("prioridade")));
        t.setStatus(StatusTarefa.valueOf(rs.getString("status")));

        Date dl = rs.getDate("data_limite");
        if (dl != null) t.setDataLimite(dl.toLocalDate());

        int projetoId = rs.getInt("projeto_id");
        if (projetoId > 0) t.setProjeto(projetoDAO.buscarPorId(projetoId));

        int responsavelId = rs.getInt("responsavel_id");
        if (responsavelId > 0) t.setResponsavel(usuarioDAO.buscarPorId(responsavelId));

        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) t.setCriadoEm(ts.toLocalDateTime());

        return t;
    }
}
