package org.emoran.junit5app.ejemplos.models;

import org.emoran.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void test() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("145.1234"));
        String esperado = "Andres";
        String actual = cuenta.getPersona();
        assertNotNull(actual, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, actual, () -> "el nombre de la cuenta no es el que se esperaba: se esperaba " + esperado +
                " sin embargo fue " + actual);
        assertTrue(actual.equals("Andres"),()->"nombre cuenta esperaba debe ser igual a la real");
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("12345.5632"));
        assertEquals(12345.5632, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuenta() {
        Cuenta origen = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta destino = new Cuenta("Andres", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(destino, origen, new BigDecimal(500));
        assertEquals("1000.8989", destino.getSaldo().toPlainString());
        assertEquals("3000", origen.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuenta() {
        Cuenta origen = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta destino = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(origen);
        banco.addCuenta(destino);

        banco.setNombre("Banco del Estados");
        banco.transferir(destino, origen, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", destino.getSaldo().toPlainString()),
                () -> assertEquals("3000", origen.getSaldo().toPlainString()),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estados", origen.getBanco().getNombre()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Andres"))
                        .findFirst()
                        .get()
                        .getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Andres")))

        );

    }
}