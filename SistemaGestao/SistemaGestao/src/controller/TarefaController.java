// ============================================================
// ARQUIVO: controller/TarefaController.java
// ============================================================
package controller;

import dao.TarefaDAO;
import exception.SistemaException;
import model.Tarefa;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TarefaController {

    private final TarefaDAO tarefaDAO = new TarefaDAO();

    public void cadastrar(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefaDAO.inserir(tarefa);
    }

    public void atualizar(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefaDAO.atualizar(tarefa);
    }

    public void excluir(int id) {
        tarefaDAO.excluir(id);
    }

    public Tarefa buscarPorId(int id) {
        return tarefaDAO.buscarPorId(id);
    }

    /**
     * Lista tarefas de um projeto ordenadas por prioridade (maior primeiro)
     * e depois por data limite (mais próxima primeiro).
     *
     * CONCEITO - ORDENAÇÃO COM COMPARATOR ENCADEADO:
     *   Comparator.comparing() cria um comparador.
     *   .thenComparing() adiciona critério secundário.
     *   .reversed() inverte a ordem (decrescente).
     */
    public List<Tarefa> listarPorProjeto(int projetoId) {
        List<Tarefa> lista = tarefaDAO.listarPorProjeto(projetoId);

        lista.sort(
            Comparator.comparing(Tarefa::getPrioridade).reversed()
                      .thenComparing(t -> t.getDataLimite() != null ? t.getDataLimite().toString() : "9999")
        );

        return lista;
    }

    // Tarefas pendentes para o relatório
    public List<Tarefa> listarPendentes() {
        return tarefaDAO.listarPendentes();
    }

    // Tarefas atrasadas (filtro em memória)
    public List<Tarefa> listarAtrasadas() {
        return tarefaDAO.listarPendentes()
                        .stream()
                        .filter(Tarefa::isAtrasada)
                        .collect(Collectors.toList());
    }

    private void validarTarefa(Tarefa t) {
        if (t.getTitulo() == null || t.getTitulo().trim().isEmpty())
            throw new SistemaException("Título da tarefa é obrigatório.");
        if (t.getProjeto() == null)
            throw new SistemaException("A tarefa deve pertencer a um projeto.");
        if (t.getPrioridade() == null)
            throw new SistemaException("Prioridade é obrigatória.");
    }
}
