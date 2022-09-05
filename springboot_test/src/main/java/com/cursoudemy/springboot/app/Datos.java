package com.cursoudemy.springboot.app;

import com.cursoudemy.springboot.app.models.Banco;
import com.cursoudemy.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
//    public static final Cuenta CUENTA_001 = new Cuenta(1L, "Daniel", new BigDecimal("1000.123"));
//    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Carlos", new BigDecimal("2000.123"));
//    public static final Banco BANCO = new Banco(1L, "Banco Financiero", 0);

    public static Optional<Cuenta> crearCuenta001() {
        return Optional.of(new Cuenta(1L, "Daniel", new BigDecimal("1000.123")));
    }

    public static Optional<Cuenta> crearCuenta002() {
        return Optional.of(new Cuenta(2L, "Carlos", new BigDecimal("2000.123")));
    }

    public static Optional<Banco> crearBanco() {
        return Optional.of(new Banco(1L, "Banco Financiero", 0));
    }
}
