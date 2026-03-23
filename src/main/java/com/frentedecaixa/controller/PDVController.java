package com.frentedecaixa.controller;

import com.frentedecaixa.model.Cliente;
import com.frentedecaixa.model.ItemVenda;
import com.frentedecaixa.model.Produto;
import com.frentedecaixa.pattern.facade.FrenteDeCaixaFacade;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class PDVController {

    @FXML private TextField campoBuscaProduto;
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colProdId;
    @FXML private TableColumn<Produto, String> colProdNome;
    @FXML private TableColumn<Produto, Double> colProdPreco;
    @FXML private TableColumn<Produto, Integer> colProdEstoque;
    @FXML private Spinner<Integer> spinnerQtd;

    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private TableView<ItemVenda> tabelaCarrinho;
    @FXML private TableColumn<ItemVenda, String> colCarrProduto;
    @FXML private TableColumn<ItemVenda, Integer> colCarrQtd;
    @FXML private TableColumn<ItemVenda, Double> colCarrPreco;
    @FXML private TableColumn<ItemVenda, Double> colCarrSubtotal;

    @FXML private Label lblSubtotal;
    @FXML private TextField campoDesconto;
    @FXML private Button btnAutorizarDesconto;
    @FXML private Label lblDescontoStatus;
    @FXML private VBox painelDesconto;
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private ComboBox<String> comboBandeira;
    @FXML private VBox painelBandeira;
    @FXML private VBox painelTaxa;
    @FXML private Label lblTaxa;
    @FXML private Label lblTotalComTaxa;
    @FXML private Label lblStatus;
    @FXML private Label lblUsuario;
    @FXML private MenuBar menuBar;
    @FXML private Menu menuCadastros;
    @FXML private Menu menuConsultas;
    @FXML private MenuItem menuItemEstoque;

    private FrenteDeCaixaFacade facade;
    private ObservableList<ItemVenda> itensCarrinho;

    @FXML
    public void initialize() {
        facade = new FrenteDeCaixaFacade();
        itensCarrinho = FXCollections.observableArrayList();

        // Configurar colunas de produtos
        colProdId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colProdNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colProdPreco.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPreco()).asObject());
        colProdEstoque.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQtdEstoque()).asObject());

        // Configurar colunas do carrinho
        colCarrProduto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduto().getNome()));
        colCarrQtd.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantidade()).asObject());
        colCarrPreco.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecoUnitario()).asObject());
        colCarrSubtotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calcularSubtotal()).asObject());

        tabelaCarrinho.setItems(itensCarrinho);

        // Spinner de quantidade
        spinnerQtd.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));

        // ComboBox de pagamento
        comboPagamento.setItems(FXCollections.observableArrayList("PIX", "CARTAO"));
        comboPagamento.setValue("PIX");

        // ComboBox de bandeira
        comboBandeira.setItems(FXCollections.observableArrayList("Visa", "Mastercard", "Elo"));
        comboBandeira.setValue("Visa");

        // Listener: mostrar/esconder bandeira e taxa ao trocar forma de pagamento
        comboPagamento.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCartao = "CARTAO".equals(newVal);
            painelBandeira.setVisible(isCartao);
            painelBandeira.setManaged(isCartao);
            painelTaxa.setVisible(isCartao);
            painelTaxa.setManaged(isCartao);
            atualizarTotais();
        });

        // Usuario logado
        if (LoginController.getUsuarioLogado() != null) {
            lblUsuario.setText("Usuario: " + LoginController.getUsuarioLogado().getNome()
                    + (LoginController.isGerente() ? " (Gerente)" : " (Cliente)"));
        }

        // Restringir acesso para clientes: manter apenas o menu Sistema (ultimo menu)
        if (!LoginController.isGerente()) {
            Menu menuSistema = menuBar.getMenus().get(menuBar.getMenus().size() - 1);
            menuBar.getMenus().clear();
            menuBar.getMenus().add(menuSistema);
        }

        // Desconto: gerente tem acesso direto, cliente precisa autorizar
        if (LoginController.isGerente()) {
            campoDesconto.setDisable(false);
            btnAutorizarDesconto.setVisible(false);
            btnAutorizarDesconto.setManaged(false);
            lblDescontoStatus.setText("Gerente autorizado");
            lblDescontoStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #4CAF50;");
        }

        // Carregar dados
        carregarProdutos();
        carregarClientes();

        lblStatus.setText("Pronto - Selecione um cliente e inicie uma venda");
    }

    @FXML
    private void buscarProduto() {
        try {
            String busca = campoBuscaProduto.getText().trim();
            List<Produto> produtos;
            if (busca.isEmpty()) {
                produtos = facade.getProdutoDAO().listarTodos();
            } else {
                produtos = facade.getProdutoDAO().buscarPorNome(busca);
            }
            tabelaProdutos.setItems(FXCollections.observableArrayList(produtos));
        } catch (Exception e) {
            mostrarErro("Erro ao buscar produtos", e.getMessage());
        }
    }

    @FXML
    private void novaVenda() {
        Cliente clienteSelecionado = comboCliente.getValue();
        if (clienteSelecionado == null) {
            mostrarErro("Erro", "Selecione um cliente primeiro.");
            return;
        }
        try {
            facade.iniciarVenda(clienteSelecionado.getId());
            itensCarrinho.clear();
            atualizarTotais();
            lblStatus.setText("Venda iniciada para: " + clienteSelecionado.getNome());
        } catch (Exception e) {
            mostrarErro("Erro ao iniciar venda", e.getMessage());
        }
    }

    @FXML
    private void adicionarItem() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado == null) {
            mostrarErro("Erro", "Selecione um produto na tabela.");
            return;
        }
        if (facade.getVendaAtual() == null) {
            mostrarErro("Erro", "Inicie uma venda primeiro (clique 'Nova Venda').");
            return;
        }

        int qtd = spinnerQtd.getValue();
        try {
            facade.adicionarItem(produtoSelecionado.getId(), qtd);
            // Sincronizar o carrinho visual com os itens da venda
            itensCarrinho.setAll(facade.getVendaAtual().getItens());
            atualizarTotais();
            carregarProdutos();
            lblStatus.setText("Item adicionado: " + produtoSelecionado.getNome() + " x" + qtd);
        } catch (Exception e) {
            mostrarErro("Erro ao adicionar item", e.getMessage());
        }
    }

    @FXML
    private void removerItem() {
        ItemVenda selecionado = tabelaCarrinho.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Erro", "Selecione um item no carrinho.");
            return;
        }
        if (facade.getVendaAtual() != null) {
            facade.getVendaAtual().getItens().remove(selecionado);
            itensCarrinho.setAll(facade.getVendaAtual().getItens());
            atualizarTotais();
            lblStatus.setText("Item removido.");
        }
    }

    @FXML
    private void finalizarVenda() {
        if (facade.getVendaAtual() == null || itensCarrinho.isEmpty()) {
            mostrarErro("Erro", "Nenhuma venda em andamento ou carrinho vazio.");
            return;
        }

        String tipoPagamento = comboPagamento.getValue();
        double desconto = 0;
        try {
            desconto = Double.parseDouble(campoDesconto.getText().trim()) / 100.0;
        } catch (NumberFormatException ignored) {}

        try {
            facade.getVendaAtual().setDesconto(desconto);
            double total = facade.finalizarVenda(tipoPagamento);
            itensCarrinho.clear();
            atualizarTotais();
            carregarProdutos();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venda Finalizada");
            alert.setHeaderText("Venda realizada com sucesso!");
            String mensagem = "Subtotal: R$ " + String.format("%.2f", total) + "\nPagamento: " + tipoPagamento;
            if ("CARTAO".equals(tipoPagamento)) {
                double taxa = total * 0.025;
                double totalComTaxa = total + taxa;
                String bandeira = comboBandeira.getValue();
                mensagem += "\nBandeira: " + bandeira
                        + "\nTaxa (2.5%): R$ " + String.format("%.2f", taxa)
                        + "\nTotal cobrado: R$ " + String.format("%.2f", totalComTaxa);
            }
            alert.setContentText(mensagem);
            alert.showAndWait();

            // Verificar alertas de estoque
            if (facade.getAlertaGerente() != null && !facade.getAlertaGerente().getAlertas().isEmpty()) {
                List<String> alertas = facade.getAlertaGerente().getAlertas();
                String ultimoAlerta = alertas.get(alertas.size() - 1);
                Alert alertaEstoque = new Alert(Alert.AlertType.WARNING);
                alertaEstoque.setTitle("Alerta de Estoque");
                alertaEstoque.setHeaderText("Estoque Baixo!");
                alertaEstoque.setContentText(ultimoAlerta);
                alertaEstoque.showAndWait();
            }

            lblStatus.setText("Venda finalizada. Total: R$ " + String.format("%.2f", total));
        } catch (Exception e) {
            mostrarErro("Erro ao finalizar venda", e.getMessage());
        }
    }

    @FXML
    private void abrirProdutos() { abrirJanela("/com/frentedecaixa/view/produtos.fxml", "Cadastro de Produtos", 650, 500); }

    @FXML
    private void abrirClientes() { abrirJanela("/com/frentedecaixa/view/clientes.fxml", "Cadastro de Clientes", 650, 500); }

    @FXML
    private void abrirEstoque() { abrirJanela("/com/frentedecaixa/view/estoque.fxml", "Controle de Estoque", 750, 550); }

    @FXML
    private void abrirRelatorios() { abrirJanela("/com/frentedecaixa/view/relatorios.fxml", "Relatorios", 700, 500); }

    @FXML
    private void autorizarDesconto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Autorizacao de Desconto");
        dialog.setHeaderText("Apenas gerentes podem aplicar descontos.");
        dialog.setContentText("CPF do Gerente:");
        dialog.showAndWait().ifPresent(cpf -> {
            try {
                var gerenteDAO = facade.getGerenteDAO();
                var gerente = gerenteDAO.buscarPorCpf(cpf.trim());
                if (gerente != null) {
                    campoDesconto.setDisable(false);
                    btnAutorizarDesconto.setDisable(true);
                    lblDescontoStatus.setText("Autorizado por: " + gerente.getNome());
                    lblDescontoStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #4CAF50;");
                    lblStatus.setText("Desconto autorizado pelo gerente " + gerente.getNome());
                } else {
                    lblDescontoStatus.setText("CPF nao e de um gerente");
                    lblDescontoStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #f44336;");
                }
            } catch (Exception e) {
                mostrarErro("Erro", e.getMessage());
            }
        });
    }

    @FXML
    private void trocarUsuario() {
        try {
            LoginController.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/frentedecaixa/view/login.fxml"));
            Stage stage = (Stage) lblStatus.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 400, 300);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Frente de Caixa");
            stage.centerOnScreen();
        } catch (Exception e) {
            mostrarErro("Erro ao trocar usuario", e.getMessage());
        }
    }

    @FXML
    private void sair() {
        Stage stage = (Stage) lblStatus.getScene().getWindow();
        stage.close();
    }

    private void abrirJanela(String fxml, String titulo, int largura, int altura) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load(), largura, altura);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Recarregar dados ao fechar a janela
            carregarProdutos();
            carregarClientes();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir janela", e.getMessage());
        }
    }

    private void carregarProdutos() {
        try {
            List<Produto> produtos = facade.getProdutoDAO().listarTodos();
            tabelaProdutos.setItems(FXCollections.observableArrayList(produtos));
        } catch (Exception e) {
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void carregarClientes() {
        try {
            List<Cliente> clientes = facade.getClienteDAO().listarTodos();
            comboCliente.setItems(FXCollections.observableArrayList(clientes));
            if (!clientes.isEmpty() && comboCliente.getValue() == null) {
                comboCliente.setValue(clientes.get(0));
            }
        } catch (Exception e) {
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void atualizarTotais() {
        double subtotal = 0;
        for (ItemVenda item : itensCarrinho) {
            subtotal += item.calcularSubtotal();
        }
        lblSubtotal.setText("R$ " + String.format("%.2f", subtotal));

        double desconto = 0;
        try {
            desconto = Double.parseDouble(campoDesconto.getText().trim()) / 100.0;
        } catch (NumberFormatException ignored) {}

        double total = subtotal - (subtotal * desconto);
        lblTotal.setText("R$ " + String.format("%.2f", total));

        // Atualizar taxa do cartao
        if ("CARTAO".equals(comboPagamento.getValue())) {
            double taxa = total * 0.025;
            double totalComTaxa = total + taxa;
            lblTaxa.setText("R$ " + String.format("%.2f", taxa));
            lblTotalComTaxa.setText("R$ " + String.format("%.2f", totalComTaxa));
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
