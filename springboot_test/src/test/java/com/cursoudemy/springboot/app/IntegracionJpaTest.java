package com.cursoudemy.springboot.app;

import com.cursoudemy.springboot.app.models.Cuenta;
import com.cursoudemy.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_jpamvn")
@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("Daniel", cuenta.orElseThrow().getNombre());
    }

    @Test
    void testFindByNombre() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Daniel");

        assertTrue(cuenta.isPresent());
        assertEquals("Daniel", cuenta.orElseThrow().getNombre());
        assertEquals("1000.12", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByNombreException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Jimeno");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        //  Given
        Cuenta cuentaJavier = new Cuenta(null, "Javier", new BigDecimal("3000.21"));

        //  When
        Cuenta cuenta = cuentaRepository.save(cuentaJavier);

//        Cuenta cuenta = cuentaRepository.findByNombre("Javier").orElseThrow();
//        Cuenta cuenta = cuentaRepository.findById(cuentaSave.getId()).orElseThrow();

        //  Then
        assertEquals("Javier", cuenta.getNombre());
        assertEquals("3000.21", cuenta.getSaldo().toPlainString());
//        assertEquals(3, cuenta.getId());
    }

    @Test
    void testUpdate() {
        //  Given
        Cuenta cuentaJavier = new Cuenta(null, "Javier", new BigDecimal("3000.21"));

        //  When
        Cuenta cuenta = cuentaRepository.save(cuentaJavier);
        cuenta.setSaldo(new BigDecimal("3890.23"));
        cuentaRepository.save(cuenta);

        //  Then
        assertEquals("Javier", cuenta.getNombre());
        assertEquals("3890.23", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Carlos", cuenta.getNombre());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
//            cuentaRepository.findByNombre("Carlos").orElseThrow();
            cuentaRepository.findById(2L).orElseThrow();
        });
    }
}
