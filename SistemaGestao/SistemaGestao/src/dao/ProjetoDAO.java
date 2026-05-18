// ============================================================
// ARQUIVO: dao/ProjetoDAO.java
//
// DAO responsável por todas as operações de banco de dados
// da entidade Projeto. Segue o mesmo padrão do UsuarioDAO.
// ============================================================
package dao;

import exception.DAOException;
import enums.StatusProjeto;
import model.Projeto;
import model.Usuario;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    // DAO auxiliar para buscar o gerente do projeto
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // --------------------------------------------------------
    // INSERT
    // --------------------------------------------------------
    public void inserir(Projeto projeto) {
        String sql = "INSERT INTO projetos (nome, descricao, data_inicio, data_previsao_fim, status, gerente_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, projeto.getNome());
            ps.setString(2, projeto.getDescricao());
            // Converte LocalDate para java.sql.Date
            ps.setDate(3, Date.valueOf(projeto.getDataInicio()));
            ps.setDate(4, projeto.getDataPrevisaoFim() != null
                    ? Date.valueOf(projeto.getDataPrevisaoFim()) : null);
            ps.setString(5, projeto.getStatus().name());
            ps.setInt(6, projeto.getGerente() != null ? projeto.getGerente().getId() : 0);

            ps.executeUpdate();

            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) projeto.setId(chaves.getInt(1));

        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir projeto: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // SELECT por ID
    // --------------------------------------------------------
    public Projeto buscarPorId(int id) {
        String sql = "SELECT * FROM projetos WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapearResultado(rs);

        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar projeto: " + e.getMessage(), e);
        }
        return null;
    }

    // --------------------------------------------------------
    // SELECT todos
    // --------------------------------------------------------
    public List<Projeto> listarTodos() {
        String sql = "SELECT * FROM projetos ORDER BY nome";
        List<Projeto> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearResultado(rs));

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar projetos: " + e.getMessage(), e);
        }
        return lista;
    }

    // --------------------------------------------------------
    // SELECT por status (relatório)
    // --------------------------------------------------------
    public List<Projeto> listarPorStatus(StatusProjeto status) {
        String sql = "SELECT * FROM projetos WHERE status = ? ORDER BY nome";
        List<Projeto> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearResultado(rs));

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar projetos por status: " + e.getMessage(), e);
        }
        return lista;
    }

    // --------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------
    public void atualizar(Projeto projeto) {
        String sql = "UPDATE projetos SET nome=?, descricao=?, data_inicio=?, " +
                     "data_previsao_fim=?, status=?, gerente_id=? WHERE id=?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projeto.getNome());
            ps.setString(2, projeto.getDescricao());
            ps.setDate(3, Date.valueOf(projeto.getDataInicio()));
            ps.setDate(4, projeto.getDataPrevisaoFim() != null
                    ? Date.valueOf(projeto.getDataPrevisaoFim()) : null);
            ps.setString(5, projeto.getStatus().name());
            ps.setInt(6, projeto.getGerente() != null ? projeto.getGerente().getId() : 0);
            ps.setInt(7, projeto.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar projeto: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // DELETE
    // --------------------------------------------------------
    public void excluir(int id) {
        String sql = "DELETE FROM projetos WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir projeto: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // Alocar equipe em projeto
    // --------------------------------------------------------
    public void alocarEquipe(int projetoId, int equipeId) {
        String sql = "INSERT IGNORE INTO projeto_equipes (projeto_id, equipe_id) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetoId);
            ps.setInt(2, equipeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao alocar equipe: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // Remover equipe de projeto
    // --------------------------------------------------------
    public void removerEquipe(int projetoId, int equipeId) {
        String sql = "DELETE FROM projeto_equipes WHERE projeto_id = ? AND equipe_id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetoId);
            ps.setInt(2, equipeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao remover equipe: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------
    // Mapper privado: ResultSet → Projeto
    // --------------------------------------------------------
    private Projeto mapearResultado(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));

        Date di = rs.getDate("data_inicio");
        if (di != null) p.setDataInicio(di.toLocalDate());

        Date df = rs.getDate("data_previsao_fim");
        if (df != null) p.setDataPrevisaoFim(df.toLocalDate());

        p.setStatus(StatusProjeto.valueOf(rs.getString("status")));

        // Busca o gerente pelo ID armazenado na coluna gerente_id
        int gerenteId = rs.getInt("gerente_id");
        if (gerenteId > 0) p.setGerente(usuarioDAO.buscarPorId(gerenteId));

        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) p.setCriadoEm(ts.toLocalDateTime());

        return p;
    }
}
