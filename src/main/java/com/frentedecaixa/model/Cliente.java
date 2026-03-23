package com.frentedecaixa.model;

import java.time.LocalDate;

public class Cliente extends Usuario {

    private LocalDate dataCadastro;
    private double limiteCredito;

    public Cliente() {}

    public Cliente(String nome, String cpf, String email, LocalDate dataCadastro, double limiteCredito) {
        super(nome, cpf, email);
        this.dataCadastro = dataCadastro;
        this.limiteCredito = limiteCredito;
    }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }
}
