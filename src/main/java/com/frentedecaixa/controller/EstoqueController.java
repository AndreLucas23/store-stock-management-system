package com.frentedecaixa.controller;

import com.frentedecaixa.dao.CategoriaDAO;
import com.frentedecaixa.dao.ProdutoDAO;
import com.frentedecaixa.model.Categoria;
import com.frentedecaixa.model.Produto;
import com.frentedecaixa.pattern.singleton.ConexaoBD;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.List;

public class EstoqueController {

    @FXML private TableView<Produto> tabelaEstoque;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, String> colCategoria;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TextArea areaAlertas;
    @FXML private Label lblProdutoSelecionado;
    @FXML private Spinner<Integer> spinnerEstoque;

    private ProdutoDAO produtoDAO;
    private CategoriaDAO categoriaDAO;
    private Produto produtoSelecionado;

    @FXML
    public void initialize() {
        Connection conn = ConexaoBD.getInstancia().getConexao();
        try {
            conn.setAutoCommit(true);
        } catch (Exception ignored) {}
        produtoDAO = new ProdutoDAO(conn);
        categoriaDAO = new CategoriaDAO(conn);

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colEstoque.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQtdEstoque()).asObject());
        colPreco.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPreco()).asObject());
        colCategoria.setCellValueFactory(data -> {
            try {
                Categoria cat = categoriaDAO.buscarPorId(data.getValue().getCategoriaId());
                return new SimpleStringProperty(cat != null ? cat.getDescricao() : "");
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        // Destacar em vermelho os produtos com estoque baixo
        tabelaEstoque.setRowFactory(tv -> new TableRow<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getQtdEstoque() <= 5) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });

        // Spinner de estoque
        spinnerEstoque.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));

        // Listener de selecao na tabela
        tabelaEstoque.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                produtoSelecionado = newVal;
                lblProdutoSelecionado.setText(newVal.getNome() + " (atual: " + newVal.getQtdEstoque() + ")");
                spinnerEstoque.getValueFactory().setValue(newVal.getQtdEstoque());
            }
        });

        carregarDados();
    }

    @FXML
    private void salvarEstoque() {
        if (produtoSelecionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Selecione um produto na tabela.");
            return;
        }

        int novaQtd = spinnerEstoque.getValue();
        try {
            produtoDAO.atualizarEstoque(produtoSelecionado.getId(), novaQtd);
            int qtdAnterior = produtoSelecionado.getQtdEstoque();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estoque Atualizado",
                    produtoSelecionado.getNome() + ": " + qtdAnterior + " -> " + novaQtd + " unidades");
            carregarDados();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar estoque: " + e.getMessage());
        }
    }

    private void carregarDados() {
        try {
            List<Produto> produtos = produtoDAO.listarTodos();
            tabelaEstoque.setItems(FXCollections.observableArrayList(produtos));

            // Gerar alertas para produtos com estoque baixo
            StringBuilder alertas = new StringBuilder();
            for (Produto p : produtos) {
                if (p.getQtdEstoque() <= 5) {
                    alertas.append("ALERTA: ").append(p.getNome())
                            .append(" - Restam ").append(p.getQtdEstoque())
                            .append(" unidades\n");
                }
            }
            areaAlertas.setText(alertas.length() > 0 ? alertas.toString() : "Nenhum alerta no momento.");
        } catch (Exception e) {
            areaAlertas.setText("Erro ao carregar estoque: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
