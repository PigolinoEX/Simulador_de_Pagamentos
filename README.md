SIMULADOR DE PAGAMENTOS (CARTÃO VIA DÉBITO E CRÉDITO) E VIA PIX COM EXTRATO

O Simulador de Pagamentos é uma aplicação desktop desenvolvida em Java 8 com Swing, criada para fins educacionais. O sistema simula operações financeiras do dia a dia por meio de telas e botões:

- Pix: pagamento por chave, com validação de saldo.

- Cartão de débito: compra em estabelecimento com senha, descontando direto do saldo.

- Cartão de crédito: compra com senha, parcelamento de até 12x e limite de R$ 100.000,00.

- Extrato: histórico de transações com data, tipo, descrição e valor, além do resumo de saldo e limite disponível.

Todos os valores usam BigDecimal, e as regras de negócio (saldo insuficiente, senha incorreta, limite excedido) ficam separadas da interface. Nenhum pagamento real é processado, e os dados ficam apenas em memória.
