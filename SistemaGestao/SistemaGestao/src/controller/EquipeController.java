// ============================================================
// ARQUIVO: controller/EquipeController.java
// ============================================================
package controller;

import dao.EquipeDAO;
import exception.SistemaException;
import model.Equipe;

import java.util.Collections;
import java.util.List;

public class EquipeController {

    private final EquipeDAO equipeDAO = new EquipeDAO();

    public void cadastrar(Equipe equipe) {
        validarEquipe(equipe);
        equipeDAO.inserir(equipe);
    }

    public void atualizar(Equipe equipe) {
        validarEquipe(equipe);
        equipeDAO.atualizar(equipe);
    }

    public void excluir(int id) {
        equipeDAO.excluir(id);
    }

    public Equipe buscarPorId(int id) {
        return equipeDAO.buscarPorId(id);
    }

    public List<Equipe> listarTodas() {
        List<Equipe> lista = equipeDAO.listarTodos();
        Collections.sort(lista, (e1, e2) -> e1.getNome().compareToIgnoreCase(e2.getNome()));
        return lista;
    }

    public void adicionarMembro(int equipeId, int usuarioId) {
        equipeDAO.adicionarMembro(equipeId, usuarioId);
    }

    public void removerMembro(int equipeId, int usuarioId) {
        equipeDAO.removerMembro(equipeId, usuarioId);
    }

    private void validarEquipe(Equipe e) {
        if (e.getNome() == null || e.getNome().trim().isEmpty())
            throw new SistemaException("Nome da equipe é obrigatório.");
    }
}
