import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Tela principal do simulador de pagamentos (Pix, débito, crédito e extrato). */
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String TELA_HOME = "home";
    private static final String TELA_PIX = "pix";
    private static final String TELA_DEBITO = "debito";
    private static final String TELA_CREDITO = "credito";
    private static final String TELA_EXTRATO = "extrato";

    private final Conta conta = new Conta();
    @SuppressWarnings("deprecation")
	private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final CardLayout cards = new CardLayout();
    private final JPanel raiz = new JPanel(cards);

    private final JLabel lblSaldo = new JLabel();
    private final JLabel lblLimite = new JLabel();
    private final JLabel lblResumoExtrato = new JLabel();

    private final DefaultTableModel modeloExtrato = new DefaultTableModel(
            new String[] {"Data/Hora", "Tipo", "Descrição", "Valor"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // campos do Pix
    private final JTextField pixChave = new JTextField(20);
    private final JTextField pixValor = new JTextField(20);
    // campos do débito
    private final JTextField debEstab = new JTextField(20);
    private final JTextField debValor = new JTextField(20);
    private final JPasswordField debSenha = new JPasswordField(20);
    // campos do crédito
    private final JTextField creEstab = new JTextField(20);
    private final JTextField creValor = new JTextField(20);
    private final JComboBox<Integer> creParcelas = new JComboBox<Integer>();
    private final JPasswordField creSenha = new JPasswordField(20);

    public Main() {
        super("Simulador de Pagamentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(620, 520);
        setMinimumSize(new Dimension(560, 480));
        setLocationRelativeTo(null);

        for (int i = 1; i <= 12; i++) {
            creParcelas.addItem(Integer.valueOf(i));
        }

        raiz.add(criarHome(), TELA_HOME);
        raiz.add(criarTelaPix(), TELA_PIX);
        raiz.add(criarTelaDebito(), TELA_DEBITO);
        raiz.add(criarTelaCredito(), TELA_CREDITO);
        raiz.add(criarTelaExtrato(), TELA_EXTRATO);
        setContentPane(raiz);

        atualizar();
        cards.show(raiz, TELA_HOME);
    }

    // ---------------------------------------------------------------- telas

    private JPanel criarHome() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Simulador de Pagamentos", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));

        lblSaldo.setFont(lblSaldo.getFont().deriveFont(Font.BOLD, 18f));
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLimite.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topo = new JPanel(new GridLayout(3, 1, 0, 6));
        topo.add(titulo);
        topo.add(lblSaldo);
        topo.add(lblLimite);

        JPanel botoes = new JPanel(new GridLayout(3, 2, 14, 14));
        botoes.add(botao("Pagar com Pix", new Color(0x1B9E8F), TELA_PIX));
        botoes.add(botao("Cartão de Débito", new Color(0x2D6CDF), TELA_DEBITO));
        botoes.add(botao("Cartão de Crédito", new Color(0x8A4FD6), TELA_CREDITO));

        JButton extrato = botao("Extrato", new Color(0x555555), TELA_EXTRATO);
        botoes.add(extrato);

        JButton depositar = new JButton("Depositar (simulação)");
        estilizar(depositar, new Color(0xE08A1E));
        depositar.addActionListener(e -> depositar());
        botoes.add(depositar);

        JButton sair = new JButton("Sair");
        estilizar(sair, new Color(0xC0392B));
        sair.addActionListener(e -> System.exit(0));
        botoes.add(sair);

        p.add(topo, BorderLayout.NORTH);
        p.add(botoes, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarTelaPix() {
        JPanel form = novoForm();
        linha(form, 0, "Chave Pix (CPF, e-mail, telefone...):", pixChave);
        linha(form, 1, "Valor (R$):", pixValor);

        JButton confirmar = new JButton("Confirmar Pix");
        estilizar(confirmar, new Color(0x1B9E8F));
        confirmar.addActionListener(e -> {
            try {
                conta.pagarPix(pixChave.getText(), lerValor(pixValor.getText()));
                info("Pix realizado com sucesso!");
                pixChave.setText("");
                pixValor.setText("");
                voltarHome();
            } catch (NumberFormatException ex) {
                erro("Valor inválido. Exemplo: 150,50");
            } catch (PagamentoException ex) {
                erro(ex.getMessage());
            }
        });
        return moldura("Pagamento via Pix", form, confirmar);
    }

    private JPanel criarTelaDebito() {
        JPanel form = novoForm();
        linha(form, 0, "Estabelecimento:", debEstab);
        linha(form, 1, "Valor (R$):", debValor);
        linha(form, 2, "Senha do cartão:", debSenha);

        JButton confirmar = new JButton("Pagar no Débito");
        estilizar(confirmar, new Color(0x2D6CDF));
        confirmar.addActionListener(e -> {
            String senha = new String(debSenha.getPassword());
            try {
                conta.pagarDebito(debEstab.getText(), lerValor(debValor.getText()), senha);
                info("Pagamento no débito aprovado!");
                debEstab.setText("");
                debValor.setText("");
                debSenha.setText("");
                voltarHome();
            } catch (NumberFormatException ex) {
                erro("Valor inválido. Exemplo: 150,50");
            } catch (PagamentoException ex) {
                erro(ex.getMessage());
                debSenha.setText("");
            }
        });
        return moldura("Cartão de Débito", form, confirmar);
    }

    private JPanel criarTelaCredito() {
        JPanel form = novoForm();
        linha(form, 0, "Estabelecimento:", creEstab);
        linha(form, 1, "Valor (R$):", creValor);
        linha(form, 2, "Parcelas:", creParcelas);
        linha(form, 3, "Senha do cartão:", creSenha);

        JButton confirmar = new JButton("Pagar no Crédito");
        estilizar(confirmar, new Color(0x8A4FD6));
        confirmar.addActionListener(e -> {
            String senha = new String(creSenha.getPassword());
            try {
                int parcelas = ((Integer) creParcelas.getSelectedItem()).intValue();
                conta.pagarCredito(creEstab.getText(), lerValor(creValor.getText()), parcelas, senha);
                info("Pagamento no crédito aprovado!");
                creEstab.setText("");
                creValor.setText("");
                creSenha.setText("");
                creParcelas.setSelectedIndex(0);
                voltarHome();
            } catch (NumberFormatException ex) {
                erro("Valor inválido. Exemplo: 150,50");
            } catch (PagamentoException ex) {
                erro(ex.getMessage());
                creSenha.setText("");
            }
        });
        return moldura("Cartão de Crédito", form, confirmar);
    }

    private JPanel criarTelaExtrato() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Extrato", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JTable tabela = new JTable(modeloExtrato);
        tabela.setRowHeight(24);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> voltarHome());

        JPanel rodape = new JPanel(new BorderLayout(10, 0));
        rodape.add(lblResumoExtrato, BorderLayout.CENTER);
        rodape.add(voltar, BorderLayout.EAST);

        p.add(titulo, BorderLayout.NORTH);
        p.add(new JScrollPane(tabela), BorderLayout.CENTER);
        p.add(rodape, BorderLayout.SOUTH);
        return p;
    }

    // ------------------------------------------------------------- helpers

    private JButton botao(String texto, Color cor, final String tela) {
        JButton b = new JButton(texto);
        estilizar(b, cor);
        b.addActionListener(e -> {
            atualizar();
            cards.show(raiz, tela);
        });
        return b;
    }

    private void estilizar(JButton b, Color cor) {
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
    }

    private JPanel novoForm() {
        return new JPanel(new GridBagLayout());
    }

    private void linha(JPanel form, int y, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridy = y;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        form.add(new JLabel(rotulo), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        form.add(campo, c);
    }

    private JPanel moldura(String titulo, JPanel form, JButton confirmar) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> voltarHome());

        JPanel botoes = new JPanel(new GridLayout(1, 2, 12, 0));
        botoes.add(voltar);
        botoes.add(confirmar);

        p.add(lbl, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(botoes, BorderLayout.SOUTH);
        return p;
    }

    /** Aceita "1.234,56", "1234,56" ou "1234.56". */
    private BigDecimal lerValor(String texto) {
        String t = texto == null ? "" : texto.trim().replace("R$", "").replace(" ", "");
        if (t.isEmpty()) {
            throw new NumberFormatException("vazio");
        }
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
    }

    private void depositar() {
        String txt = JOptionPane.showInputDialog(this, "Valor do depósito (R$):", "Depositar",
                JOptionPane.QUESTION_MESSAGE);
        if (txt == null) {
            return;
        }
        try {
            conta.depositar(lerValor(txt));
            atualizar();
            info("Depósito realizado!");
        } catch (NumberFormatException ex) {
            erro("Valor inválido. Exemplo: 500,00");
        } catch (PagamentoException ex) {
            erro(ex.getMessage());
        }
    }

    private void voltarHome() {
        atualizar();
        cards.show(raiz, TELA_HOME);
    }

    private void atualizar() {
        lblSaldo.setText("Saldo em conta: " + moeda.format(conta.getSaldo()));
        lblLimite.setText("Limite de crédito disponível: " + moeda.format(conta.getLimiteDisponivel())
                + " de " + moeda.format(Conta.LIMITE_CREDITO));

        modeloExtrato.setRowCount(0);
        List<Transacao> lista = conta.getTransacoes();
        for (int i = lista.size() - 1; i >= 0; i--) { // mais recentes primeiro
            Transacao t = lista.get(i);
            modeloExtrato.addRow(new Object[] {
                t.getData().format(formatoData), t.getTipo(), t.getDescricao(), moeda.format(t.getValor())
            });
        }
        lblResumoExtrato.setText("<html>Saldo: " + moeda.format(conta.getSaldo())
                + " &nbsp;|&nbsp; Crédito usado: " + moeda.format(conta.getCreditoUsado())
                + " &nbsp;|&nbsp; Disponível: " + moeda.format(conta.getLimiteDisponivel()) + "</html>");
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // usa o visual padrão do Swing
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
