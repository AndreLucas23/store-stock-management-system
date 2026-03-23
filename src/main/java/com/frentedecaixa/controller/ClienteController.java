package com.frentedecaixa.controller;

import com.frentedecaixa.dao.ClienteDAO;
import com.frentedecaixa.model.Cliente;
import com.frentedecaixa.pattern.singleton.ConexaoBD;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class ClienteController {

    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoEmail;
    @FXML private TextField campoLimite;
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, Double> colLimite;

    private ClienteDAO clienteDAO;
    private Cliente clienteSelecionado;

    @FXML
    public void initialize() {
        clienteDAO = new ClienteDAO(ConexaoBD.getInstancia().getConexao());

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colCpf.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCpf()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colLimite.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLimiteCredito()).asObject());

        tabelaClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                clienteSelecionado = novo;
                campoNome.setText(novo.getNome());
                campoCpf.setText(novo.getCpf());
                campoEmail.setText(novo.getEmail());
                campoLimite.setText(String.valueOf(novo.getLimiteCredito()));
            }
        });

        carregarClientes();
    }

    @FXML
    private void novo() {
        clienteSelecionado = null;
        campoNome.clear();
        campoCpf.clear();
        campoEmail.clear();
        campoLimite.clear();
        tabelaClientes.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        try {
            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim();
            String email = campoEmail.getText().trim();
            double limite = Double.parseDouble(campoLimite.getText().trim());

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                mostrarErro("Preencha todos os campos.");
                return;
            }

            if (clienteSelecionado == null) {
                Cliente c = new Cliente(nome, cpf, email, LocalDate.now(), limite);
                clienteDAO.inserir(c);
            } else {
                clienteSelecionado.setNome(nome);
                clienteSelecionado.setCpf(cpf);
                clienteSelecionado.setEmail(email);
                clienteSelecionado.setLimiteCredito(limite);
                clienteDAO.atualizar(clienteSelecionado);
            }
            carregarClientes();
            novo();
        } catch (NumberFormatException e) {
            mostrarErro("Limite de credito deve ser um numero valido.");
        } catch (Exception e) {
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        if (clienteSelecionado == null) {
            mostrarErro("Selecione um cliente para excluir.");
            return;
        }
        try {
            clienteDAO.deletar(clienteSelecionado.getId());
            carregarClientes();
            novo();
        } catch (Exception e) {
            mostrarErro("Erro ao excluir: " + e.getMessage());
        }
    }

    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            tabelaClientes.setItems(FXCollections.observableArrayList(clientes));
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
