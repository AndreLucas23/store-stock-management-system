package com.frentedecaixa.model;

public class Gerente extends Usuario {

    private int nivelAcesso;

    public Gerente() {}

    public Gerente(String nome, String cpf, String email, int nivelAcesso) {
        super(nome, cpf, email);
        this.nivelAcesso = nivelAcesso;
    }

    public int getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(int nivelAcesso) { this.nivelAcesso = nivelAcesso; }
}
