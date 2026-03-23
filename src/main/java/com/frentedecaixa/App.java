package com.frentedecaixa;

import com.frentedecaixa.pattern.singleton.ConexaoBD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Inicializa o banco (Singleton)
        ConexaoBD.getInstancia();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/frentedecaixa/view/login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        stage.setTitle("Frente de Caixa - Sistema de Vendas");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        ConexaoBD.getInstancia().fechar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
