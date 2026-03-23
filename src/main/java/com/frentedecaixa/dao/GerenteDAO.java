package com.frentedecaixa.dao;

import com.frentedecaixa.model.Gerente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GerenteDAO {

    private Connection conexao;

    public GerenteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Gerente gerente) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, email, tipo, nivel_acesso) VALUES (?, ?, ?, 'GERENTE', ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, gerente.getNome());
        stmt.setString(2, gerente.getCpf());
        stmt.setString(3, gerente.getEmail());
        stmt.setInt(4, gerente.getNivelAcesso());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            gerente.setId(rs.getInt(1));
        }
        stmt.close();
    }

    public Gerente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE cpf = ? AND tipo = 'GERENTE'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();
        Gerente gerente = null;
        if (rs.next()) {
            gerente = mapear(rs);
        }
        stmt.close();
        return gerente;
    }

    public Gerente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ? AND tipo = 'GERENTE'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Gerente gerente = null;
        if (rs.next()) {
            gerente = mapear(rs);
        }
        stmt.close();
        return gerente;
    }

    public List<Gerente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE tipo = 'GERENTE' ORDER BY nome";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Gerente> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    private Gerente mapear(ResultSet rs) throws SQLException {
        Gerente g = new Gerente();
        g.setId(rs.getInt("id"));
        g.setNome(rs.getString("nome"));
        g.setCpf(rs.getString("cpf"));
        g.setEmail(rs.getString("email"));
        g.setNivelAcesso(rs.getInt("nivel_acesso"));
        return g;
    }
}
