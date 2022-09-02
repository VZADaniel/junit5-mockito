package com.cursoudemy.appmockito.repositories;

import com.cursoudemy.appmockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();

}
