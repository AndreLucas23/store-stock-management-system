package com.frentedecaixa.dao;

import com.frentedecaixa.model.ItemVenda;
import com.frentedecaixa.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemVendaDAO {

    private Connection conexao;

    public ItemVendaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public List<ItemVenda> buscarPorVenda(int vendaId) throws SQLException {
        String sql = "SELECT iv.*, p.nome as prod_nome, p.preco as prod_preco, p.qtd_estoque, p.categoria_id "
                   + "FROM item_venda iv JOIN produto p ON iv.produto_id = p.id "
                   + "WHERE iv.venda_id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, vendaId);
        ResultSet rs = stmt.executeQuery();
        List<ItemVenda> lista = new ArrayList<>();
        while (rs.next()) {
            ItemVenda item = new ItemVenda();
            item.setId(rs.getInt("id"));
            item.setVendaId(rs.getInt("venda_id"));
            item.setQuantidade(rs.getInt("quantidade"));
            item.setPrecoUnitario(rs.getDouble("preco_unitario"));

            Produto p = new Produto();
            p.setId(rs.getInt("produto_id"));
            p.setNome(rs.getString("prod_nome"));
            p.setPreco(rs.getDouble("prod_preco"));
            p.setQtdEstoque(rs.getInt("qtd_estoque"));
            p.setCategoriaId(rs.getInt("categoria_id"));
            item.setProduto(p);

            lista.add(item);
        }
        stmt.close();
        return lista;
    }
}
