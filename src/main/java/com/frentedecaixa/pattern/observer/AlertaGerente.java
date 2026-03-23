package com.frentedecaixa.pattern.observer;

import com.frentedecaixa.model.Gerente;
import com.frentedecaixa.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class AlertaGerente implements Observer {

    private Gerente gerente;
    private List<String> alertas;

    public AlertaGerente(Gerente gerente) {
        this.gerente = gerente;
        this.alertas = new ArrayList<>();
    }

    @Override
    public void atualizar(Produto produto) {
        String msg = "ALERTA: Estoque baixo do produto '" + produto.getNome()
                + "' - restam " + produto.getQtdEstoque() + " unidades.";
        alertas.add(msg);
        System.out.println("[AlertaGerente -> " + gerente.getNome() + "] " + msg);
    }

    public Gerente getGerente() { return gerente; }
    public List<String> getAlertas() { return alertas; }
}
