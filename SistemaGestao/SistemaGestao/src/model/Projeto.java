// ============================================================
// ARQUIVO: model/Projeto.java
//
// CONCEITO - ASSOCIAÇÃO:
//   O Projeto possui um "gerente" do tipo Usuario. Isso é uma
//   ASSOCIAÇÃO — a classe Projeto "conhece" a classe Usuario.
//   Se o Projeto for excluído, o Usuario continua existindo.
//   Representamos isso guardando o objeto Usuario dentro do Projeto.
//
// CONCEITO - COMPOSIÇÃO:
//   O Projeto "possui" uma lista de Tarefas. Se o projeto for
//   excluído, as tarefas também devem ser excluídas. Isso é
//   COMPOSIÇÃO (relação mais forte que Associação).
//
// CONCEITO - AGREGAÇÃO:
//   O Projeto tem Equipes alocadas. Se o Projeto for excluído,
//   as Equipes continuam existindo com seus membros. Isso é
//   AGREGAÇÃO (relação mais fraca que Composição).
// ============================================================
package model;

import enums.StatusProjeto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um projeto do sistema.
 */
public class Projeto {

    private int id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataPrevisaoFim;
    private StatusProjeto status;
    private LocalDateTime criadoEm;

    // --------------------------------------------------------
    // ASSOCIAÇÃO: Projeto possui um Gerente (Usuario)
    // Se o projeto for excluído, o usuário continua existindo.
    // --------------------------------------------------------
    private Usuario gerente;

    // --------------------------------------------------------
    // COMPOSIÇÃO: Projeto possui Tarefas
    // ArrayList é uma lista dinâmica — pode crescer conforme
    // adicionamos elementos. É um dos tipos mais usados em Java.
    // Tarefas só existem dentro de um Projeto → Composição.
    // --------------------------------------------------------
    private List<Tarefa> tarefas;

    // --------------------------------------------------------
    // AGREGAÇÃO: Projeto tem Equipes alocadas
    // As equipes existem independentemente do projeto → Agregação.
    // --------------------------------------------------------
    private List<Equipe> equipes;

    // Construtor padrão — inicializa as listas para evitar NullPointerException
    public Projeto() {
        this.status   = StatusProjeto.PLANEJADO;   // Status inicial padrão
        this.criadoEm = LocalDateTime.now();
        this.tarefas  = new ArrayList<>();           // Inicia a lista vazia
        this.equipes  = new ArrayList<>();
    }

    // Construtor completo
    public Projeto(String nome, String descricao, LocalDate dataInicio,
                   LocalDate dataPrevisaoFim, Usuario gerente) {
        this();
        this.nome            = nome;
        this.descricao       = descricao;
        this.dataInicio      = dataInicio;
        this.dataPrevisaoFim = dataPrevisaoFim;
        this.gerente         = gerente;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataPrevisaoFim() { return dataPrevisaoFim; }
    public void setDataPrevisaoFim(LocalDate dataPrevisaoFim) { this.dataPrevisaoFim = dataPrevisaoFim; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }

    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }

    // Métodos auxiliares para manipular as listas
    public void adicionarTarefa(Tarefa tarefa) {
        this.tarefas.add(tarefa);
    }

    public void adicionarEquipe(Equipe equipe) {
        this.equipes.add(equipe);
    }

    @Override
    public String toString() {
        return nome + " [" + (status != null ? status.getDescricao() : "sem status") + "]";
    }
}
