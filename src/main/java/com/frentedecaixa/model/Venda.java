package com.frentedecaixa.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int id;
    private LocalDate data;
    private int clienteId;
    private String tipoPagamento;
    private double desconto;
    private double total;
    private List<ItemVenda> listaProdutos;

    public Venda() {
        this.listaProdutos = new ArrayList<>();
        this.data = LocalDate.now();
    }

    public void adicionarItem(ItemVenda item) {
        listaProdutos.add(item);
    }

    public double calcularTotalBase() {
        double soma = 0;
        for (ItemVenda item : listaProdutos) {
            soma += item.calcularSubtotal();
        }
        return soma;
    }

    public void finalizar() {
        this.total = calcularTotalBase();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<ItemVenda> getItens() { return listaProdutos; }
    public void setItens(List<ItemVenda> listaProdutos) { this.listaProdutos = listaProdutos; }
}
