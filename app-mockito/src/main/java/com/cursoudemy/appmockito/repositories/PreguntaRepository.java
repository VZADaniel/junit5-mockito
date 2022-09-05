package com.cursoudemy.appmockito.repositories;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPreguntasPorExamenId(Long id);

    void guardarPreguntas(List<String> preguntas);
}
