// ============================================================
// ARQUIVO: enums/StatusProjeto.java
// ============================================================
package enums;

public enum StatusProjeto {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusProjeto(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() { return descricao; }
}

// ============================================================
// ARQUIVO: enums/StatusTarefa.java  (colocado no mesmo arquivo
// apenas para reduzir quantidade de arquivos do exemplo;
// em produção, cada enum fica em seu próprio arquivo)
// ============================================================

// package enums;
// public enum StatusTarefa { PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA }
