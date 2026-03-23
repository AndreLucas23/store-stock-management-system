package com.frentedecaixa.pattern.facade;

import com.frentedecaixa.dao.*;
import com.frentedecaixa.model.*;
import com.frentedecaixa.pattern.decorator.DescontoDecorator;
import com.frentedecaixa.pattern.factory.PagamentoFactory;
import com.frentedecaixa.pattern.observer.AlertaGerente;
import com.frentedecaixa.pattern.observer.Estoque;
import com.frentedecaixa.pattern.singleton.ConexaoBD;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FrenteDeCaixaFacade {

    private Estoque estoque;
    private VendaDAO vendaDAO;
    private ProdutoDAO produtoDAO;
    private ClienteDAO clienteDAO;
    private GerenteDAO gerenteDAO;
    private ItemVendaDAO itemVendaDAO;
    private Venda vendaAtual;
    private AlertaGerente alertaGerente;

    public FrenteDeCaixaFacade() {
        Connection conn = ConexaoBD.getInstancia().getConexao();
        this.produtoDAO = new ProdutoDAO(conn);
        this.estoque = new Estoque(produtoDAO);
        this.vendaDAO = new VendaDAO(conn);
        this.clienteDAO = new ClienteDAO(conn);
        this.gerenteDAO = new GerenteDAO(conn);
        this.itemVendaDAO = new ItemVendaDAO(conn);

        try {
            List<Gerente> gerentes = gerenteDAO.listarTodos();
            if (!gerentes.isEmpty()) {
                alertaGerente = new AlertaGerente(gerentes.get(0));
                estoque.addObserver(alertaGerente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registrar observer: " + e.getMessage());
        }
    }

    public void iniciarVenda(int clienteId) throws SQLException {
        Cliente c = clienteDAO.buscarPorId(clienteId);
        if (c == null) {
            throw new IllegalArgumentException("Cliente nao encontrado com ID: " + clienteId);
        }
        vendaAtual = new Venda();
        vendaAtual.setClienteId(clienteId);
        vendaAtual.setData(LocalDate.now());
    }

    public void adicionarItem(int produtoId, int qtd) throws SQLException {
        if (vendaAtual == null) {
            throw new IllegalStateException("Nenhuma venda iniciada");
        }
        if (!estoque.verificarDisponibilidade(produtoId, qtd)) {
            throw new IllegalStateException("Estoque insuficiente para o produto ID: " + produtoId);
        }
        Produto p = produtoDAO.buscarPorId(produtoId);
        ItemVenda item = new ItemVenda(p, qtd, p.getPreco());
        vendaAtual.adicionarItem(item);
    }

    public double finalizarVenda(String tipoPagamento) throws SQLException {
        if (vendaAtual == null) {
            throw new IllegalStateException("Nenhuma venda iniciada");
        }

        double percentualDesconto = vendaAtual.getDesconto();

        // Decorator: aplica desconto se > 0
        Venda vendaParaProcessar = vendaAtual;
        if (percentualDesconto > 0) {
            vendaParaProcessar = new DescontoDecorator(vendaAtual, percentualDesconto);
        }

        double total = vendaParaProcessar.calcularTotalBase();

        // Factory: cria pagamento pelo tipo
        Pagamento pagamento = PagamentoFactory.criarPagamento(tipoPagamento);
        boolean sucesso = pagamento.processar(total);

        if (!sucesso) {
            throw new RuntimeException("Falha no processamento do pagamento");
        }

        // Observer: reduz estoque (notifica se baixo)
        for (ItemVenda item : vendaAtual.getItens()) {
            estoque.reduzir(item.getProduto().getId(), item.getQuantidade());
        }

        // Persistencia via Singleton (conforme diagrama de sequencia)
        vendaAtual.setTipoPagamento(tipoPagamento);
        vendaAtual.setTotal(total);
        ConexaoBD.getInstancia().salvar(vendaAtual);

        vendaAtual = null;
        return total;
    }

    public void cancelarVenda() {
        vendaAtual = null;
    }

    // Getters para uso nos controllers
    public Venda getVendaAtual() { return vendaAtual; }
    public ProdutoDAO getProdutoDAO() { return produtoDAO; }
    public ClienteDAO getClienteDAO() { return clienteDAO; }
    public GerenteDAO getGerenteDAO() { return gerenteDAO; }
    public VendaDAO getVendaDAO() { return vendaDAO; }
    public ItemVendaDAO getItemVendaDAO() { return itemVendaDAO; }
    public Estoque getEstoque() { return estoque; }
    public AlertaGerente getAlertaGerente() { return alertaGerente; }
}
