package com.cursoudemy.appmockito.services;

import com.cursoudemy.appmockito.models.Examen;
import com.cursoudemy.appmockito.repositories.ExamenRepository;
import com.cursoudemy.appmockito.repositories.ExamenRepositoryOtro;
import com.cursoudemy.appmockito.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExamenServiceImplTest {
    private ExamenService service;
    private ExamenRepository repository;
    private PreguntaRepository preguntaRepository;

    @BeforeEach
    void setUp() {
        repository = mock(ExamenRepositoryOtro.class);
        preguntaRepository = mock(PreguntaRepository.class);
        service = new ExamenServiceImpl(repository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertAll(
                () -> assertTrue(examen.isPresent()),
                () -> assertEquals("Matematicas", examen.orElseThrow().getNombre()),
                () -> assertEquals(5L, examen.orElseThrow().getId())
        );
    }

    @Test
    void findExamenPorNombreListEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(7L)).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }
}