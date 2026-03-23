package com.frentedecaixa.dao;

import com.frentedecaixa.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private Connection conexao;

    public ProdutoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (nome, preco, qtd_estoque, categoria_id) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.setInt(3, produto.getQtdEstoque());
        stmt.setInt(4, produto.getCategoriaId());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            produto.setId(rs.getInt(1));
        }
        stmt.close();
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Produto produto = null;
        if (rs.next()) {
            produto = mapear(rs);
        }
        stmt.close();
        return produto;
    }

    public List<Produto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM produto ORDER BY nome";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Produto> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    public List<Produto> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM produto WHERE nome LIKE ? ORDER BY nome";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();
        List<Produto> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, preco = ?, qtd_estoque = ?, categoria_id = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.setInt(3, produto.getQtdEstoque());
        stmt.setInt(4, produto.getCategoriaId());
        stmt.setInt(5, produto.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void atualizarEstoque(int produtoId, int novaQtd) throws SQLException {
        String sql = "UPDATE produto SET qtd_estoque = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, novaQtd);
        stmt.setInt(2, produtoId);
        stmt.executeUpdate();
        stmt.close();
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getDouble("preco"));
        p.setQtdEstoque(rs.getInt("qtd_estoque"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        return p;
    }
}
