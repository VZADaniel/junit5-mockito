package com.cursoudemy.appmockito.services;

import com.cursoudemy.appmockito.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen save(Examen examen);
}
