package com.frentedecaixa.controller;

import com.frentedecaixa.dao.VendaDAO;
import com.frentedecaixa.model.Venda;
import com.frentedecaixa.pattern.singleton.ConexaoBD;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioController {

    @FXML private ComboBox<String> comboPeriodo;
    @FXML private ComboBox<String> comboFiltroPagamento;
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private HBox painelDatas;
    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colId;
    @FXML private TableColumn<Venda, String> colData;
    @FXML private TableColumn<Venda, Integer> colCliente;
    @FXML private TableColumn<Venda, String> colPagamento;
    @FXML private TableColumn<Venda, Double> colDesconto;
    @FXML private TableColumn<Venda, Double> colTotal;
    @FXML private Label lblTotalVendas;

    private VendaDAO vendaDAO;

    @FXML
    public void initialize() {
        vendaDAO = new VendaDAO(ConexaoBD.getInstancia().getConexao());

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getData().toString()));
        colCliente.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getClienteId()).asObject());
        colPagamento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoPagamento()));
        colDesconto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getDesconto()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        // Periodos
        comboPeriodo.setItems(FXCollections.observableArrayList(
                "Hoje", "Ultimos 7 dias", "Ultimos 30 dias", "Personalizado"));
        comboPeriodo.setValue("Hoje");

        // Filtro pagamento
        comboFiltroPagamento.setItems(FXCollections.observableArrayList("Todos", "PIX", "CARTAO"));
        comboFiltroPagamento.setValue("Todos");

        // Datas
        datePickerInicio.setValue(LocalDate.now());
        datePickerFim.setValue(LocalDate.now());
        painelDatas.setVisible(false);
        painelDatas.setManaged(false);

        // Listener para mostrar/esconder datas personalizadas
        comboPeriodo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean personalizado = "Personalizado".equals(newVal);
            painelDatas.setVisible(personalizado);
            painelDatas.setManaged(personalizado);
            if (!personalizado) {
                filtrar();
            }
        });

        filtrar();
    }

    @FXML
    private void filtrar() {
        try {
            LocalDate inicio;
            LocalDate fim = LocalDate.now();
            String periodo = comboPeriodo.getValue();

            switch (periodo) {
                case "Ultimos 7 dias":
                    inicio = LocalDate.now().minusDays(6);
                    break;
                case "Ultimos 30 dias":
                    inicio = LocalDate.now().minusDays(29);
                    break;
                case "Personalizado":
                    inicio = datePickerInicio.getValue();
                    fim = datePickerFim.getValue();
                    if (inicio == null || fim == null) {
                        mostrarErro("Selecione as duas datas.");
                        return;
                    }
                    break;
                default: // Hoje
                    inicio = LocalDate.now();
                    break;
            }

            List<Venda> vendas = vendaDAO.listarPorPeriodo(inicio, fim);

            // Filtrar por tipo de pagamento
            String filtroPag = comboFiltroPagamento.getValue();
            if (!"Todos".equals(filtroPag)) {
                vendas = vendas.stream()
                        .filter(v -> filtroPag.equals(v.getTipoPagamento()))
                        .collect(Collectors.toList());
            }

            exibirVendas(vendas);
        } catch (Exception e) {
            mostrarErro("Erro ao filtrar: " + e.getMessage());
        }
    }

    @FXML
    private void listarTodas() {
        try {
            List<Venda> vendas = vendaDAO.listarTodas();

            // Filtrar por tipo de pagamento
            String filtroPag = comboFiltroPagamento.getValue();
            if (!"Todos".equals(filtroPag)) {
                vendas = vendas.stream()
                        .filter(v -> filtroPag.equals(v.getTipoPagamento()))
                        .collect(Collectors.toList());
            }

            exibirVendas(vendas);
        } catch (Exception e) {
            mostrarErro("Erro ao listar vendas: " + e.getMessage());
        }
    }

    private void exibirVendas(List<Venda> vendas) {
        tabelaVendas.setItems(FXCollections.observableArrayList(vendas));
        double total = vendas.stream().mapToDouble(Venda::getTotal).sum();
        lblTotalVendas.setText("R$ " + String.format("%.2f", total));
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
