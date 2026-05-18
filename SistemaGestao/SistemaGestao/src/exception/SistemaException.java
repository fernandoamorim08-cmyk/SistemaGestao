// ============================================================
// ARQUIVO: exception/SistemaException.java
//
// CONCEITO - EXCEÇÕES PERSONALIZADAS:
//   Em vez de usar exceções genéricas (Exception), criamos
//   nossas próprias para dar mensagens mais claras ao usuário.
//   Extendemos RuntimeException para não obrigar o uso de
//   try/catch em todo lugar (unchecked exception).
//
// CONCEITO - HERANÇA:
//   SistemaException HERDA de RuntimeException.
//   Isso significa que ela já vem com todos os comportamentos
//   de uma exceção, e nós apenas adicionamos ou personalizamos.
//   Usamos a palavra "extends" para indicar herança em Java.
// ============================================================
package exception;

/**
 * Exceção base do sistema. Todas as outras exceções do sistema
 * herdam desta classe.
 *
 * HERANÇA: SistemaException → RuntimeException → Exception → Throwable
 */
public class SistemaException extends RuntimeException {

    // serialVersionUID é exigido para classes que implementam Serializable
    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem de erro.
     * @param mensagem — explicação do erro
     */
    public SistemaException(String mensagem) {
        super(mensagem);   // Chama o construtor da classe pai (RuntimeException)
    }

    /**
     * Construtor com mensagem e causa original.
     * @param mensagem — explicação do erro
     * @param causa    — exceção original que causou este erro
     */
    public SistemaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
