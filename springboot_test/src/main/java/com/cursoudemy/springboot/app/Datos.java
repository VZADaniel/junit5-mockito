package com.cursoudemy.springboot.app;

import com.cursoudemy.springboot.app.models.Banco;
import com.cursoudemy.springboot.app.models.Cuenta;

import java.math.BigDecimal;

public class Datos {
//    public static final Cuenta CUENTA_001 = new Cuenta(1L, "Daniel", new BigDecimal("1000.123"));
//    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Carlos", new BigDecimal("2000.123"));
//    public static final Banco BANCO = new Banco(1L, "Banco Financiero", 0);

    public static Cuenta crearCuenta001() {
        return new Cuenta(1L, "Daniel", new BigDecimal("1000.123"));
    }

    public static Cuenta crearCuenta002() {
        return new Cuenta(2L, "Carlos", new BigDecimal("2000.123"));
    }

    public static Banco crearBanco() {
        return new Banco(1L, "Banco Financiero", 0);
    }
}
