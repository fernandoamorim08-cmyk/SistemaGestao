// ============================================================
// ARQUIVO: enums/PerfilUsuario.java
// CONCEITO: Enum (Enumeração) é uma forma de criar um conjunto
// fixo de valores constantes. Evita usar strings soltas no
// código, reduzindo erros de digitação e facilitando a leitura.
// ============================================================
package enums;

public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente"),
    COLABORADOR("Colaborador");

    // Atributo para armazenar o nome legível
    private final String descricao;

    // Construtor do enum (privado por padrão)
    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    // Método para obter a descrição
    public String getDescricao() {
        return descricao;
    }

    // Retorna a descrição ao converter para String
    @Override
    public String toString() {
        return descricao;
    }
}
