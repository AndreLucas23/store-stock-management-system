package com.frentedecaixa.pattern.singleton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import com.frentedecaixa.dao.VendaDAO;
import com.frentedecaixa.model.Venda;

public class ConexaoBD {

    private static ConexaoBD instancia;
    private Connection conexao;

    private ConexaoBD() {
        try {
            conexao = DriverManager.getConnection("jdbc:sqlite:frentedecaixa.db");
            conexao.createStatement().execute("PRAGMA foreign_keys = ON");
            inicializarSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static synchronized ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    public Connection getConexao() {
        return conexao;
    }

    public void salvar(Venda venda) throws SQLException {
        VendaDAO vendaDAO = new VendaDAO(conexao);
        vendaDAO.inserir(venda);
    }

    public void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        instancia = null;
    }

    private void inicializarSchema() {
        try {
            InputStream is = getClass().getResourceAsStream("/db/schema.sql");
            if (is == null) {
                System.err.println("schema.sql nao encontrado no classpath");
                return;
            }
            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            Statement stmt = conexao.createStatement();
            for (String comando : sql.split(";")) {
                String trimmed = comando.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar schema: " + e.getMessage());
        }
    }
}
