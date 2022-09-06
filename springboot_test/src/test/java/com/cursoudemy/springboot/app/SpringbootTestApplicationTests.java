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
import java.util.Arrays;
import java.util.List;

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
        assertEquals("1000.12", saldoOrigen.toPlainString());
        assertEquals("2000.12", saldoDestino.toPlainString());

        service.transferir(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigen = service.revisarSaldo(1L);
        saldoDestino = service.revisarSaldo(2L);
        assertEquals("900.12", saldoOrigen.toPlainString());
        assertEquals("2100.12", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(1, total);
        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));

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
        assertEquals("1000.12", saldoOrigen.toPlainString());
        assertEquals("2000.12", saldoDestino.toPlainString());

        assertThrows(SaldoInsuficienteException.class, () -> {
            service.transferir(1L, 2L, new BigDecimal("1300"), 1L);
        });

        assertEquals("1000.12", saldoOrigen.toPlainString());
        assertEquals("2000.12", saldoDestino.toPlainString());

        int total = service.revisarTotalTransferencias(1L);
        assertEquals(0, total);
        verify(cuentaRepository, times(2)).findById(1L);
        verify(cuentaRepository, times(1)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));

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

    @Test
    void testFindAll() {
        //  Given
        List<Cuenta> datos = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
        when(cuentaRepository.findAll()).thenReturn(datos);

        //  When
        List<Cuenta> cuentas = service.findAll();

        //  Then
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

        verify(cuentaRepository).findAll();
    }

    @Test
    void testSave() {
        //  Given
        Cuenta dato = new Cuenta(null, "Jose", new BigDecimal("3000.12"));
        when(cuentaRepository.save(any())).then(invocationOnMock -> {
            Cuenta c = invocationOnMock.getArgument(0);
            c.setId(3L);
            return c;
        });

        //  When
        Cuenta cuenta = service.save(dato);

        //  Then
        assertEquals("Jose", cuenta.getNombre());
        assertEquals(3, cuenta.getId());
        assertEquals("3000.12", cuenta.getSaldo().toPlainString());

        verify(cuentaRepository).save(any());
    }
}
