package com.cursoudemy.springboot.app;

import com.cursoudemy.springboot.app.exceptions.SaldoInsuficienteException;
import com.cursoudemy.springboot.app.models.Banco;
import com.cursoudemy.springboot.app.models.Cuenta;
import com.cursoudemy.springboot.app.repositories.BancoRepository;
import com.cursoudemy.springboot.app.repositories.CuentaRepository;
import com.cursoudemy.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static com.cursoudemy.springboot.app.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {

    @MockBean
    CuentaRepository cuentaRepository;
    @MockBean
    BancoRepository bancoRepository;
    @Autowired
    CuentaService service;

    @BeforeEach
    void setUp() {
//        cuentaRepository = mock(CuentaRepository.class);
//        bancoRepository = mock(BancoRepository.class);
//        service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
//        Datos.CUENTA_001.setSaldo(new BigDecimal("1000.123"));
//        Datos.CUENTA_002.setSaldo(new BigDecimal("2000.123"));
//        Datos.BANCO.setTotalTransferencias(0);
    }

    @Test
    void contextLoads() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);
        assertEquals("1000.123", saldoOrigen.toPlainString());
        assertEquals("2000.123", saldoDestino.toPlainString());

        service.transferir(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);
        assertEquals("900.123", saldoOrigen.toPlainString());
        assertEquals("2100.123", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(1, total);
        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(2)).update(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).update(any(Banco.class));

        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = service.revisarSaldo(1L);
        BigDecimal saldoDestino = service.revisarSaldo(2L);
        assertEquals("1000.123", saldoOrigen.toPlainString());
        assertEquals("2000.123", saldoDestino.toPlainString());

        assertThrows(SaldoInsuficienteException.class, () -> {
            service.transferir(1L, 2L, new BigDecimal("1300"), 1L);
        });

        assertEquals("1000.123", saldoOrigen.toPlainString());
        assertEquals("2000.123", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(0, total);
        verify(cuentaRepository, times(2)).findById(1L);
        verify(cuentaRepository, times(1)).findById(2L);
        verify(cuentaRepository, never()).update(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).update(any(Banco.class));

        verify(cuentaRepository, times(3)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads3() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

        Cuenta cuenta1 = service.findById(1L);
        Cuenta cuenta2 = service.findById(1L);

        assertSame(cuenta1, cuenta2);
        assertTrue(cuenta1 == cuenta2);
        assertEquals("Daniel", cuenta1.getNombre());
        assertEquals("Daniel", cuenta2.getNombre());
        verify(cuentaRepository, times(2)).findById(1L);
    }
}
