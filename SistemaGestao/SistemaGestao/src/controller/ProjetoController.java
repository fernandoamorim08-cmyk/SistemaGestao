// ============================================================
// ARQUIVO: controller/ProjetoController.java
// ============================================================
package controller;

import dao.ProjetoDAO;
import dao.EquipeDAO;
import enums.StatusProjeto;
import exception.SistemaException;
import model.Projeto;
import model.Equipe;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ProjetoController {

    private final ProjetoDAO projetoDAO   = new ProjetoDAO();
    private final EquipeDAO  equipeDAO    = new EquipeDAO();

    // Cadastra projeto com validações
    public void cadastrar(Projeto projeto) {
        validarProjeto(projeto);
        projetoDAO.inserir(projeto);
    }

    // Atualiza projeto
    public void atualizar(Projeto projeto) {
        validarProjeto(projeto);
        projetoDAO.atualizar(projeto);
    }

    // Exclui projeto
    public void excluir(int id) {
        projetoDAO.excluir(id);
    }

    // Lista todos ordenados por nome
    public List<Projeto> listarTodos() {
        List<Projeto> lista = projetoDAO.listarTodos();
        Collections.sort(lista, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));
        return lista;
    }

    // Lista apenas projetos em andamento (para relatório)
    public List<Projeto> listarEmAndamento() {
        return projetoDAO.listarPorStatus(StatusProjeto.EM_ANDAMENTO);
    }

    // Busca por ID
    public Projeto buscarPorId(int id) {
        return projetoDAO.buscarPorId(id);
    }

    // Aloca uma equipe em um projeto
    public void alocarEquipe(int projetoId, int equipeId) {
        Projeto projeto = projetoDAO.buscarPorId(projetoId);
        Equipe  equipe  = equipeDAO.buscarPorId(equipeId);

        if (projeto == null) throw new SistemaException("Projeto não encontrado.");
        if (equipe == null)  throw new SistemaException("Equipe não encontrada.");

        projetoDAO.alocarEquipe(projetoId, equipeId);
    }

    // Remove equipe do projeto
    public void removerEquipe(int projetoId, int equipeId) {
        projetoDAO.removerEquipe(projetoId, equipeId);
    }

    private void validarProjeto(Projeto p) {
        if (p.getNome() == null || p.getNome().trim().isEmpty())
            throw new SistemaException("Nome do projeto é obrigatório.");
        if (p.getDataInicio() == null)
            throw new SistemaException("Data de início é obrigatória.");
        if (p.getDataPrevisaoFim() != null && p.getDataPrevisaoFim().isBefore(p.getDataInicio()))
            throw new SistemaException("Data de término deve ser após a data de início.");
        if (p.getGerente() == null)
            throw new SistemaException("Gerente responsável é obrigatório.");
    }
}
