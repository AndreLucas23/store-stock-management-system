package com.frentedecaixa.model;

public class Produto {

    private int id;
    private String nome;
    private double preco;
    private int qtdEstoque;
    private int categoriaId;

    public Produto() {}

    public Produto(String nome, double preco, int qtdEstoque, int categoriaId) {
        this.nome = nome;
        this.preco = preco;
        this.qtdEstoque = qtdEstoque;
        this.categoriaId = categoriaId;
    }

    public void reduzirEstoque(int qtd) {
        if (qtd > qtdEstoque) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + nome);
        }
        this.qtdEstoque -= qtd;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(int qtdEstoque) { this.qtdEstoque = qtdEstoque; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    @Override
    public String toString() {
        return nome + " - R$ " + String.format("%.2f", preco);
    }
}
