package com.frentedecaixa.dao;

import com.frentedecaixa.model.ItemVenda;
import com.frentedecaixa.model.Venda;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    private Connection conexao;

    public VendaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Venda venda) throws SQLException {
        conexao.setAutoCommit(false);
        try {
            String sqlVenda = "INSERT INTO venda (data, cliente_id, tipo_pagamento, desconto, total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtVenda = conexao.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setString(1, venda.getData().toString());
            stmtVenda.setInt(2, venda.getClienteId());
            stmtVenda.setString(3, venda.getTipoPagamento());
            stmtVenda.setDouble(4, venda.getDesconto());
            stmtVenda.setDouble(5, venda.getTotal());
            stmtVenda.executeUpdate();

            ResultSet rs = stmtVenda.getGeneratedKeys();
            if (rs.next()) {
                venda.setId(rs.getInt(1));
            }
            stmtVenda.close();

            String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtItem = conexao.prepareStatement(sqlItem);
            for (ItemVenda item : venda.getItens()) {
                stmtItem.setInt(1, venda.getId());
                stmtItem.setInt(2, item.getProduto().getId());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.addBatch();
            }
            stmtItem.executeBatch();
            stmtItem.close();

            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    public Venda buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM venda WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Venda venda = null;
        if (rs.next()) {
            venda = mapear(rs);
        }
        stmt.close();
        return venda;
    }

    public List<Venda> listarTodas() throws SQLException {
        String sql = "SELECT * FROM venda ORDER BY data DESC";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Venda> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    public List<Venda> listarPorData(LocalDate data) throws SQLException {
        String sql = "SELECT * FROM venda WHERE data = ? ORDER BY id DESC";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, data.toString());
        ResultSet rs = stmt.executeQuery();
        List<Venda> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    public List<Venda> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT * FROM venda WHERE data >= ? AND data <= ? ORDER BY data DESC, id DESC";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, inicio.toString());
        stmt.setString(2, fim.toString());
        ResultSet rs = stmt.executeQuery();
        List<Venda> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setData(LocalDate.parse(rs.getString("data")));
        v.setClienteId(rs.getInt("cliente_id"));
        v.setTipoPagamento(rs.getString("tipo_pagamento"));
        v.setDesconto(rs.getDouble("desconto"));
        v.setTotal(rs.getDouble("total"));
        return v;
    }
}
