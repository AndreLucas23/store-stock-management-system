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

public class ProdutoController {

    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private TextField campoEstoque;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, String> colCategoria;

    private ProdutoDAO produtoDAO;
    private CategoriaDAO categoriaDAO;
    private Produto produtoSelecionado;

    @FXML
    public void initialize() {
        Connection conn = ConexaoBD.getInstancia().getConexao();
        produtoDAO = new ProdutoDAO(conn);
        categoriaDAO = new CategoriaDAO(conn);

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colPreco.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPreco()).asObject());
        colEstoque.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQtdEstoque()).asObject());
        colCategoria.setCellValueFactory(data -> {
            try {
                Categoria cat = categoriaDAO.buscarPorId(data.getValue().getCategoriaId());
                return new SimpleStringProperty(cat != null ? cat.getDescricao() : "");
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                produtoSelecionado = novo;
                campoNome.setText(novo.getNome());
                campoPreco.setText(String.valueOf(novo.getPreco()));
                campoEstoque.setText(String.valueOf(novo.getQtdEstoque()));
                for (Categoria cat : comboCategoria.getItems()) {
                    if (cat.getId() == novo.getCategoriaId()) {
                        comboCategoria.setValue(cat);
                        break;
                    }
                }
            }
        });

        carregarCategorias();
        carregarProdutos();
    }

    @FXML
    private void novo() {
        produtoSelecionado = null;
        campoNome.clear();
        campoPreco.clear();
        campoEstoque.clear();
        comboCategoria.setValue(null);
        tabelaProdutos.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        try {
            String nome = campoNome.getText().trim();
            double preco = Double.parseDouble(campoPreco.getText().trim());
            int estoque = Integer.parseInt(campoEstoque.getText().trim());
            Categoria cat = comboCategoria.getValue();

            if (nome.isEmpty() || cat == null) {
                mostrarErro("Preencha todos os campos.");
                return;
            }

            if (produtoSelecionado == null) {
                Produto p = new Produto(nome, preco, estoque, cat.getId());
                produtoDAO.inserir(p);
            } else {
                produtoSelecionado.setNome(nome);
                produtoSelecionado.setPreco(preco);
                produtoSelecionado.setQtdEstoque(estoque);
                produtoSelecionado.setCategoriaId(cat.getId());
                produtoDAO.atualizar(produtoSelecionado);
            }
            carregarProdutos();
            novo();
        } catch (NumberFormatException e) {
            mostrarErro("Preco e estoque devem ser numeros validos.");
        } catch (Exception e) {
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        if (produtoSelecionado == null) {
            mostrarErro("Selecione um produto para excluir.");
            return;
        }
        try {
            produtoDAO.deletar(produtoSelecionado.getId());
            carregarProdutos();
            novo();
        } catch (Exception e) {
            mostrarErro("Erro ao excluir: " + e.getMessage());
        }
    }

    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listarTodos();
            tabelaProdutos.setItems(FXCollections.observableArrayList(produtos));
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.listarTodas();
            comboCategoria.setItems(FXCollections.observableArrayList(categorias));
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
