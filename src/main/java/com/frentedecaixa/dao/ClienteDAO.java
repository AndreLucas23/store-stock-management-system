package com.frentedecaixa.dao;

import com.frentedecaixa.model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private Connection conexao;

    public ClienteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, email, tipo, data_cadastro, limite_credito) VALUES (?, ?, ?, 'CLIENTE', ?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCpf());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4, cliente.getDataCadastro() != null ? cliente.getDataCadastro().toString() : LocalDate.now().toString());
        stmt.setDouble(5, cliente.getLimiteCredito());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            cliente.setId(rs.getInt(1));
        }
        stmt.close();
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ? AND tipo = 'CLIENTE'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Cliente cliente = null;
        if (rs.next()) {
            cliente = mapear(rs);
        }
        stmt.close();
        return cliente;
    }

    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE cpf = ? AND tipo = 'CLIENTE'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();
        Cliente cliente = null;
        if (rs.next()) {
            cliente = mapear(rs);
        }
        stmt.close();
        return cliente;
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE tipo = 'CLIENTE' ORDER BY nome";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Cliente> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        stmt.close();
        return lista;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, email = ?, data_cadastro = ?, limite_credito = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCpf());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4, cliente.getDataCadastro() != null ? cliente.getDataCadastro().toString() : null);
        stmt.setDouble(5, cliente.getLimiteCredito());
        stmt.setInt(6, cliente.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ? AND tipo = 'CLIENTE'";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        String dataCad = rs.getString("data_cadastro");
        if (dataCad != null) {
            c.setDataCadastro(LocalDate.parse(dataCad));
        }
        c.setLimiteCredito(rs.getDouble("limite_credito"));
        return c;
    }
}
