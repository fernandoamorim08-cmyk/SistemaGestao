// ============================================================
// ARQUIVO: model/Tarefa.java
// ============================================================
package model;

import enums.Prioridade;
import enums.StatusTarefa;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa uma tarefa dentro de um projeto.
 *
 * CONCEITO - COMPOSIÇÃO:
 *   Tarefa pertence a um Projeto. Se o projeto for excluído,
 *   as tarefas também devem ser excluídas (ON DELETE CASCADE
 *   no banco de dados). A Tarefa não existe sem o Projeto.
 *   Isso caracteriza Composição.
 */
public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private LocalDate dataLimite;
    private Prioridade prioridade;
    private StatusTarefa status;
    private LocalDateTime criadoEm;

    // COMPOSIÇÃO: Tarefa pertence a um Projeto
    private Projeto projeto;

    // ASSOCIAÇÃO: Tarefa tem um Responsável (Usuario)
    private Usuario responsavel;

    public Tarefa() {
        this.status    = StatusTarefa.PENDENTE;
        this.prioridade = Prioridade.MEDIA;
        this.criadoEm  = LocalDateTime.now();
    }

    public Tarefa(String titulo, String descricao, Projeto projeto,
                  Usuario responsavel, LocalDate dataLimite, Prioridade prioridade) {
        this();
        this.titulo      = titulo;
        this.descricao   = descricao;
        this.projeto     = projeto;
        this.responsavel = responsavel;
        this.dataLimite  = dataLimite;
        this.prioridade  = prioridade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    /**
     * Verifica se a tarefa está atrasada.
     * @return true se a data limite passou e a tarefa não está concluída/cancelada
     */
    public boolean isAtrasada() {
        if (dataLimite == null) return false;
        boolean naoFinalizada = status != StatusTarefa.CONCLUIDA && status != StatusTarefa.CANCELADA;
        return naoFinalizada && LocalDate.now().isAfter(dataLimite);
    }

    @Override
    public String toString() {
        return titulo + " [" + prioridade + " | " + status + "]";
    }
}
