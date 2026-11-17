/** Erro de regra de negócio (saldo insuficiente, senha errada, etc.). */
public class PagamentoException extends Exception {
    private static final long serialVersionUID = 1L;

    public PagamentoException(String mensagem) {
        super(mensagem);
    }
}
