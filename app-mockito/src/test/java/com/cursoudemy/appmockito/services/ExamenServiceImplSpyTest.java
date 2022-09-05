package com.cursoudemy.appmockito.services;

import com.cursoudemy.appmockito.models.Examen;
import com.cursoudemy.appmockito.repositories.ExamenRepositoryImpl;
import com.cursoudemy.appmockito.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @InjectMocks
    private ExamenServiceImpl service;
    @Spy
    private ExamenRepositoryImpl repository;
    @Spy
    private PreguntaRepositoryImpl preguntaRepository;

    @Test
    void testSpy() {
        List<String> preguntas = Arrays.asList("aritmetica");
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
}