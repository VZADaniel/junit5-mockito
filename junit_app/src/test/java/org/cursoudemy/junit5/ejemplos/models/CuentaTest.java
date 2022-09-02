package org.cursoudemy.junit5.ejemplos.models;

import org.cursoudemy.junit5.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class CuentaTest {
    private Cuenta cuenta;

    @BeforeEach
    void setUp(TestInfo info, TestReporter reporter) {
        this.cuenta = new Cuenta("Daniel", new BigDecimal("1000.12345"));
        System.out.println("Ejecutando: " + info.getDisplayName() + " " + info.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + info.getTags());
    }

    @AfterEach
    void tearDown() {
//        System.out.println("Finalizando el metodo");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inciando CuentaTest");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando CuentaTest");
    }

    @Tag("nombreCuenta")
    @Nested
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("Probando nombre de la Cuenta")
        void testNombreCuenta() {
            String esperado = "Daniel";
            String real = cuenta.getNombre();

            assertNotNull(real, () -> "El nombre de cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el esperado: se esperaba " + esperado
                    + " sin embargo fue " + real);
            assertTrue(real.equals("Daniel"), () -> "Nombre de cuenta esperada debe ser igual a la Real");
        }

        @Test
        @DisplayName("Probando el saldo de la cuenta")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando referencias que sean iguales con metodo equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("Daniel", new BigDecimal("8900.99997"));
            Cuenta cuenta2 = new Cuenta("Daniel", new BigDecimal("8900.99997"));

//        assertNotEquals(cuenta, cuenta2);
            assertEquals(cuenta, cuenta2);
        }
    }

    @Tag("cuenta")
    @Nested
    class CuentaOperacionesTest {
        @Test
        @DisplayName("Probando descuentos al saldo de una cuenta")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        @DisplayName("Probando abono al saldo de una cuenta")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        @DisplayName("Probando transferencia entre cuentas")
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Daniel", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Carlos", new BigDecimal("1500.8989"));
            Banco banco = new Banco();
            banco.setNombre("Bando del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }
    }


    @Tag("error")
    @Tag("banco")
    @Test
    @DisplayName("Probando excepciones de saldo insuficiente")
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "El saldo de la cuenta es insuficiente";

        assertEquals(esperado, actual);
    }


    @Test
    @DisplayName("Probando relaciones entre las cuentas y el banco con AssertAll")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Daniel", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Carlos", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene la cantidad de cuentas esperadas"),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Daniel", banco.getCuentas().stream()
                        .filter(c -> c.getNombre().equals("Daniel"))
                        .findFirst()
                        .get().getNombre()),
                () -> assertTrue(banco.getCuentas().stream()
                        .filter(c -> c.getNombre().equals("Daniel"))
                        .findFirst().isPresent()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getNombre().equals("Daniel")))
        );
    }

    @Tag("OS")
    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void testSoloLinuxOMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNOWindows() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        }
    }

    @Tag("javaVersion")
    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_18)
        void soloJdk18() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_18)
        void testNoJdk18() {
        }
    }

    @Tag("systemProperty")
    @Nested
    class SystemPropertyTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "18.0.2")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "dvasqual")
        void testUserName() {
        }
    }

    @Tag("environment")
    @Nested
    @DisplayName("Variables de Entorno")
    class VariablesDeEntornoTest {
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }

        @Test
        void imprimirVariablesDeEntorno() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @RepeatedTest(value = 5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
        void testSaldoCuentaDev(RepetitionInfo info) {
            if (info.getCurrentRepetition() == 3) {
                System.out.println("Estamos en la repeticion " + info.getCurrentRepetition());
            }
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumingThat(esDev, () -> {
                assertNotNull(cuenta.getSaldo());
                assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
                assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
                assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            });

        }
    }

    @Tag("param")
    @Nested
    @DisplayName("Pruebas Parametrizadas")
    class PruebasParametrizadas {
        @ParameterizedTest(name = "Numero repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        void testDebitoCuentaValueSource(String monto) {
            BigDecimal resultado = cuenta.getSaldo().subtract(new BigDecimal(monto));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertEquals(resultado, cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "Numero repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            BigDecimal resultado = cuenta.getSaldo().subtract(new BigDecimal(monto));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertEquals(resultado, cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "Numero repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100", "250,200", "320,300", "540,500", "850,700", "1000.12345,1000.12345"})
        void testDebitoCuentaCsvSource2(String saldo, String monto) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            BigDecimal resultado = cuenta.getSaldo().subtract(new BigDecimal(monto));

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertEquals(resultado, cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "Numero repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            BigDecimal resultado = cuenta.getSaldo().subtract(new BigDecimal(monto));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertEquals(resultado, cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Tag("param")
    @ParameterizedTest(name = "Numero repeticion {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        BigDecimal resultado = cuenta.getSaldo().subtract(new BigDecimal(monto));
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertEquals(resultado, cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class TimeOut {
        @Test
        @Timeout(2)
        void TimeoutTest() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
        void TimeoutTest2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(2), () -> {
                TimeUnit.SECONDS.sleep(1);
            });
        }
    }
}