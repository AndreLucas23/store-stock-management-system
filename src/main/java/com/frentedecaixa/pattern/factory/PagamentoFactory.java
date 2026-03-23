package com.frentedecaixa.pattern.factory;

import com.frentedecaixa.model.Pagamento;

public class PagamentoFactory {

    public static Pagamento criarPagamento(String tipo) {
        switch (tipo.toUpperCase()) {
            case "PIX":
                return new PagamentoPix();
            case "CARTAO":
                return new PagamentoCartao();
            default:
                throw new IllegalArgumentException("Tipo de pagamento invalido: " + tipo);
        }
    }
}
