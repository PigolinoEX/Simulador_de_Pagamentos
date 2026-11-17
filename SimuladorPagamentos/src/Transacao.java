import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro de uma operação exibida no extrato. */
public class Transacao {
    private final LocalDateTime data;
    private final String tipo;       // PIX, DÉBITO, CRÉDITO, DEPÓSITO
    private final String descricao;
    private final BigDecimal valor;  // negativo = saída, positivo = entrada

    public Transacao(String tipo, String descricao, BigDecimal valor) {
        this.data = LocalDateTime.now();
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
