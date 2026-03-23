package com.frentedecaixa.pattern.factory;

import com.frentedecaixa.model.Pagamento;
import com.frentedecaixa.pattern.strategy.EstrategiaCalculoTaxa;
import com.frentedecaixa.pattern.strategy.TaxaCartaoStrategy;

public class PagamentoCartao implements Pagamento {

    private int parcelas;
    private String bandeira;
    private EstrategiaCalculoTaxa estrategia;

    public PagamentoCartao() {
        this.parcelas = 1;
        this.bandeira = "Visa";
        this.estrategia = new TaxaCartaoStrategy();
    }

    @Override
    public boolean processar(double valor) {
        double taxa = estrategia.calcularTaxa(valor);
        double valorFinal = valor + taxa;
        System.out.println("[CARTAO] Pagamento de R$ " + String.format("%.2f", valorFinal)
                + " em " + parcelas + "x na bandeira " + bandeira
                + " | Taxa: R$ " + String.format("%.2f", taxa));
        return true;
    }

    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
}
