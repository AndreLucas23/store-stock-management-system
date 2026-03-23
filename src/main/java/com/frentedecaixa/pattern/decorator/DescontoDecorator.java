package com.frentedecaixa.pattern.decorator;

import com.frentedecaixa.model.ItemVenda;
import com.frentedecaixa.model.Venda;

import java.time.LocalDate;
import java.util.List;

public class DescontoDecorator extends Venda {

    private Venda vendaBase;
    private double percentualDesconto;

    public DescontoDecorator(Venda vendaBase, double percentualDesconto) {
        this.vendaBase = vendaBase;
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public double calcularTotalBase() {
        double total = vendaBase.calcularTotalBase();
        return total - (total * percentualDesconto);
    }

    @Override
    public int getId() { return vendaBase.getId(); }

    @Override
    public void setId(int id) { vendaBase.setId(id); }

    @Override
    public LocalDate getData() { return vendaBase.getData(); }

    @Override
    public void setData(LocalDate data) { vendaBase.setData(data); }

    @Override
    public int getClienteId() { return vendaBase.getClienteId(); }

    @Override
    public void setClienteId(int clienteId) { vendaBase.setClienteId(clienteId); }

    @Override
    public String getTipoPagamento() { return vendaBase.getTipoPagamento(); }

    @Override
    public void setTipoPagamento(String tipoPagamento) { vendaBase.setTipoPagamento(tipoPagamento); }

    @Override
    public List<ItemVenda> getItens() { return vendaBase.getItens(); }

    @Override
    public void adicionarItem(ItemVenda item) { vendaBase.adicionarItem(item); }

    public double getPercentualDesconto() { return percentualDesconto; }
}
