// ============================================================
// ARQUIVO: exception/DAOException.java
// Exceção para erros no acesso ao banco de dados
// ============================================================
package exception;

/**
 * Exceção lançada quando há erro no acesso ao banco de dados.
 * HERANÇA: DAOException → SistemaException → RuntimeException
 */
public class DAOException extends SistemaException {
    private static final long serialVersionUID = 1L;

    public DAOException(String mensagem) {
        super(mensagem);
    }

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
