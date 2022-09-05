package com.cursoudemy.springboot.app.repositories;

import com.cursoudemy.springboot.app.models.Banco;

import java.util.List;

public interface BancoRepository {
    List<Banco> findAll();

    Banco findById(Long id);

    void update(Banco banco);
}
