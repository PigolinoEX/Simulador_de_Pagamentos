import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Conta simulada: saldo, cartão (débito/crédito) e histórico de transações. */
public class Conta {
    public static final BigDecimal LIMITE_CREDITO = new BigDecimal("100000.00");
    public static final String SENHA_CARTAO = "1234"; // senha do cartão (simulação)

    private BigDecimal saldo = new BigDecimal("5000.00"); // saldo inicial de teste
    private BigDecimal creditoUsado = BigDecimal.ZERO.setScale(2);
    private final List<Transacao> transacoes = new ArrayList<Transacao>();

    public BigDecimal getSaldo() {
        return saldo;
    }

    public BigDecimal getCreditoUsado() {
        return creditoUsado;
    }

    public BigDecimal getLimiteDisponivel() {
        return LIMITE_CREDITO.subtract(creditoUsado);
    }

    public List<Transacao> getTransacoes() {
        return Collections.unmodifiableList(transacoes);
    }

    private void validarValor(BigDecimal valor) throws PagamentoException {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagamentoException("Informe um valor maior que zero.");
        }
    }

    private void validarSenha(String senha) throws PagamentoException {
        if (senha == null || !SENHA_CARTAO.equals(senha)) {
            throw new PagamentoException("Senha do cartão incorreta.");
        }
    }

    public void depositar(BigDecimal valor) throws PagamentoException {
        validarValor(valor);
        saldo = saldo.add(valor);
        transacoes.add(new Transacao("DEPÓSITO", "Depósito em conta", valor));
    }

    public void pagarPix(String chave, BigDecimal valor) throws PagamentoException {
        if (chave == null || chave.trim().isEmpty()) {
            throw new PagamentoException("Informe a chave Pix do destinatário.");
        }
        validarValor(valor);
        if (valor.compareTo(saldo) > 0) {
            throw new PagamentoException("Saldo insuficiente para o Pix.");
        }
        saldo = saldo.subtract(valor);
        transacoes.add(new Transacao("PIX", "Pix para " + chave.trim(), valor.negate()));
    }

    public void pagarDebito(String estabelecimento, BigDecimal valor, String senha)
            throws PagamentoException {
        if (estabelecimento == null || estabelecimento.trim().isEmpty()) {
            throw new PagamentoException("Informe o estabelecimento.");
        }
        validarValor(valor);
        validarSenha(senha);
        if (valor.compareTo(saldo) > 0) {
            throw new PagamentoException("Saldo insuficiente para o débito.");
        }
        saldo = saldo.subtract(valor);
        transacoes.add(new Transacao("DÉBITO", "Compra em " + estabelecimento.trim(), valor.negate()));
    }

    public void pagarCredito(String estabelecimento, BigDecimal valor, int parcelas, String senha)
            throws PagamentoException {
        if (estabelecimento == null || estabelecimento.trim().isEmpty()) {
            throw new PagamentoException("Informe o estabelecimento.");
        }
        validarValor(valor);
        validarSenha(senha);
        if (parcelas < 1) {
            throw new PagamentoException("Número de parcelas inválido.");
        }
        if (valor.compareTo(getLimiteDisponivel()) > 0) {
            throw new PagamentoException("Limite de crédito insuficiente.");
        }
        creditoUsado = creditoUsado.add(valor);
        String desc = "Compra em " + estabelecimento.trim();
        if (parcelas > 1) {
            BigDecimal parcela = valor.divide(new BigDecimal(parcelas), 2, RoundingMode.HALF_UP);
            desc += " (" + parcelas + "x de R$ " + parcela.toPlainString().replace('.', ',') + ")";
        } else {
            desc += " (à vista)";
        }
        transacoes.add(new Transacao("CRÉDITO", desc, valor.negate()));
    }
}
