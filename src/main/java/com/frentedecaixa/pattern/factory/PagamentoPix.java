package com.frentedecaixa.pattern.factory;

import com.frentedecaixa.model.Pagamento;
import com.frentedecaixa.pattern.strategy.EstrategiaCalculoTaxa;
import com.frentedecaixa.pattern.strategy.IsencaoPixStrategy;

public class PagamentoPix implements Pagamento {

    private String chavePix;
    private EstrategiaCalculoTaxa estrategia;

    public PagamentoPix() {
        this.chavePix = "loja@frentedecaixa.com";
        this.estrategia = new IsencaoPixStrategy();
    }

    @Override
    public boolean processar(double valor) {
        double taxa = estrategia.calcularTaxa(valor);
        double valorFinal = valor + taxa;
        System.out.println("[PIX] Pagamento de R$ " + String.format("%.2f", valorFinal)
                + " processado via chave: " + chavePix + " | Taxa: R$ " + String.format("%.2f", taxa));
        return true;
    }

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }
}
