module com.frentedecaixa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.frentedecaixa to javafx.fxml;
    opens com.frentedecaixa.controller to javafx.fxml;
    opens com.frentedecaixa.model to javafx.base;

    exports com.frentedecaixa;
    exports com.frentedecaixa.model;
    exports com.frentedecaixa.controller;
    exports com.frentedecaixa.pattern.singleton;
    exports com.frentedecaixa.pattern.factory;
    exports com.frentedecaixa.pattern.facade;
    exports com.frentedecaixa.pattern.decorator;
    exports com.frentedecaixa.pattern.observer;
    exports com.frentedecaixa.pattern.strategy;
    exports com.frentedecaixa.dao;
}
