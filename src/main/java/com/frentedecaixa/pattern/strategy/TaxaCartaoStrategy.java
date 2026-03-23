package com.frentedecaixa.pattern.strategy;

public class TaxaCartaoStrategy implements EstrategiaCalculoTaxa {

    private static final double TAXA_CARTAO = 0.025; // 2.5%

    @Override
    public double calcularTaxa(double valor) {
        return valor * TAXA_CARTAO;
    }
}
