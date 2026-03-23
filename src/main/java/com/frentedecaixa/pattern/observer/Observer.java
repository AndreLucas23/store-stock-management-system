package com.frentedecaixa.pattern.observer;

import com.frentedecaixa.model.Produto;

public interface Observer {
    void atualizar(Produto produto);
}
