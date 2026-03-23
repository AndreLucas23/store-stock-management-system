package com.frentedecaixa.controller;

import com.frentedecaixa.dao.ClienteDAO;
import com.frentedecaixa.dao.GerenteDAO;
import com.frentedecaixa.model.Cliente;
import com.frentedecaixa.model.Gerente;
import com.frentedecaixa.model.Usuario;
import com.frentedecaixa.pattern.singleton.ConexaoBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

public class LoginController {

    @FXML private TextField campoCpf;
    @FXML private Label lblMensagem;

    private static Usuario usuarioLogado;

    @FXML
    private void entrar() {
        String cpf = campoCpf.getText().trim();
        if (cpf.isEmpty()) {
            lblMensagem.setText("Digite o CPF.");
            return;
        }

        try {
            Connection conn = ConexaoBD.getInstancia().getConexao();
            GerenteDAO gerenteDAO = new GerenteDAO(conn);
            ClienteDAO clienteDAO = new ClienteDAO(conn);

            Gerente gerente = gerenteDAO.buscarPorCpf(cpf);
            if (gerente != null) {
                usuarioLogado = gerente;
                abrirPDV();
                return;
            }

            Cliente cliente = clienteDAO.buscarPorCpf(cpf);
            if (cliente != null) {
                usuarioLogado = cliente;
                abrirPDV();
                return;
            }

            lblMensagem.setText("CPF nao encontrado.");
        } catch (Exception e) {
            lblMensagem.setText("Erro: " + e.getMessage());
        }
    }

    private void abrirPDV() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/frentedecaixa/view/pdv.fxml"));
        Stage stage = (Stage) campoCpf.getScene().getWindow();
        Scene scene = new Scene(loader.load(), 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Frente de Caixa - PDV");
        stage.centerOnScreen();
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static boolean isGerente() {
        return usuarioLogado instanceof Gerente;
    }

    public static void logout() {
        usuarioLogado = null;
    }
}
