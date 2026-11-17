 *SIMULADOR DE PAGAMENTOS*

O Simulador de Pagamentos é uma aplicação desktop desenvolvida em Java 8 com Swing, criada para fins educacionais. O sistema simula operações financeiras do dia a dia por meio de telas e botões:

## Funcionalidades

- **Pix:** pagamento por chave (CPF, e-mail, telefone etc.), com validação de saldo.
- **Cartão de débito:** compra em estabelecimento, com senha, descontando direto do saldo.
- **Cartão de crédito:** limite de **R$ 100.000,00**, parcelamento de 1 a 12x e senha obrigatória.
- **Extrato:** histórico com data/hora, tipo, descrição e valor, mostrando as mais recentes primeiro, além do resumo de saldo, crédito usado e limite disponível.
- **Depósito de teste:** adiciona saldo para facilitar os testes.

- ## Dados de teste

| Item | Valor |
|---|---|
| Saldo inicial | R$ 5.000,00 |
| Senha do cartão (débito e crédito) | `1234` |
| Limite de crédito | R$ 100.000,00 |

## Tecnologias

- Java 8 (JDK 8)
- Swing (interface gráfica)
- Eclipse IDE
- `BigDecimal` para valores monetários

## Estrutura do projeto

```
src/
├── Main.java                  # Telas e botões (Swing)
├── Conta.java                 # Regras de negócio: saldo, limite, validações
├── Transacao.java             # Registro exibido no extrato
└── PagamentoException.java    # Erros de regra (saldo, senha, limite)
```
