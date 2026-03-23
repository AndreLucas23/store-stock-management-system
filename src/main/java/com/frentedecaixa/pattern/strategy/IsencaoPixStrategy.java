package com.frentedecaixa.pattern.strategy;

public class IsencaoPixStrategy implements EstrategiaCalculoTaxa {

    @Override
    public double calcularTaxa(double valor) {
        return 0.0; // PIX nao possui taxa
    }
}
