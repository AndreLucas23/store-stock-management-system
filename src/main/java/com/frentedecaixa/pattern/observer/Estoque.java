package com.frentedecaixa.pattern.observer;

import com.frentedecaixa.dao.ProdutoDAO;
import com.frentedecaixa.model.Produto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Estoque {

    private List<Observer> observers;
    private ProdutoDAO produtoDAO;
    private static final int LIMIAR_ESTOQUE_BAIXO = 5;

    public Estoque(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notificar(Produto produto) {
        for (Observer o : observers) {
            o.atualizar(produto);
        }
    }

    public boolean verificarDisponibilidade(int produtoId, int qtd) throws SQLException {
        Produto p = produtoDAO.buscarPorId(produtoId);
        return p != null && p.getQtdEstoque() >= qtd;
    }

    public void reduzir(int produtoId, int qtd) throws SQLException {
        Produto p = produtoDAO.buscarPorId(produtoId);
        if (p == null) {
            throw new IllegalArgumentException("Produto nao encontrado: " + produtoId);
        }
        p.reduzirEstoque(qtd);
        produtoDAO.atualizarEstoque(produtoId, p.getQtdEstoque());

        if (p.getQtdEstoque() <= LIMIAR_ESTOQUE_BAIXO) {
            notificar(p);
        }
    }

    public ProdutoDAO getProdutoDAO() { return produtoDAO; }
}
