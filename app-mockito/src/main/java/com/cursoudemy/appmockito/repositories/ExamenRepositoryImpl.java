package com.cursoudemy.appmockito.repositories;

import com.cursoudemy.appmockito.Datos;
import com.cursoudemy.appmockito.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository {
    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Datos.EXAMENES;
    }

    @Override
    public Examen save(Examen examen) {
        System.out.println("ExamenRepositoryImpl.save");
        return Datos.EXAMEN;
    }
}
