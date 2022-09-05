package com.cursoudemy.appmockito.repositories;

import com.cursoudemy.appmockito.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {
    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamenId");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarPreguntas(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarPreguntas");
    }
}
