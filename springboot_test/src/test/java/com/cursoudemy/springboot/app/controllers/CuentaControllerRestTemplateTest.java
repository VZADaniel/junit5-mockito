package com.cursoudemy.springboot.app.controllers;

import com.cursoudemy.springboot.app.models.Cuenta;
import com.cursoudemy.springboot.app.models.TransaccionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;

@Tag("integracion_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate client;

    private ObjectMapper mapper;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    private String crearUri(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    @Order(1)
    void findAll() throws JsonProcessingException {
        //  Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        ResponseEntity<String> response = client.postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);
        String json = response.getBody();

        assertEquals(OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con exito"));

        JsonNode jsonNode = mapper.readTree(json);
        assertEquals("Transferencia realizada con exito", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals(dto.getMonto().toPlainString(), jsonNode.path("transaccion").path("monto").asText());
        assertEquals(dto.getCuentaOrigenId(), jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", OK);
        response2.put("mensaje", "Transferencia realizada con exito");
        response2.put("transaccion", dto);

        assertEquals(mapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    void testFindById() {
        ResponseEntity<Cuenta> response = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
        Cuenta cuenta = response.getBody();
        assertEquals(OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertNotNull(cuenta);
        assertEquals(1L, cuenta.getId());
        assertEquals("Daniel", cuenta.getNombre());
        assertEquals("900.12", cuenta.getSaldo().toPlainString());
        assertEquals(new Cuenta(1L, "Daniel", new BigDecimal("900.12")), cuenta);
    }

    @Test
    @Order(3)
    void testFindAll() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertNotNull(response.getBody());
        assertEquals(OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertNotNull(cuentas);
        assertEquals(2, cuentas.size());
        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Daniel", cuentas.get(0).getNombre());
        assertEquals("900.12", cuentas.get(0).getSaldo().toPlainString());
        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("Carlos", cuentas.get(1).getNombre());
        assertEquals("2100.12", cuentas.get(1).getSaldo().toPlainString());

        JsonNode json = mapper.readTree(mapper.writeValueAsString(cuentas));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Daniel", json.get(0).path("nombre").asText());
        assertEquals("900.12", json.get(0).path("saldo").asText());
        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("Carlos", json.get(1).path("nombre").asText());
        assertEquals("2100.12", json.get(1).path("saldo").asText());
    }

    @Test
    @Order(4)
    void testSave() {
        Cuenta cuenta = new Cuenta(null, "Miguel", new BigDecimal("3000.12"));

        ResponseEntity<Cuenta> response = client.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
        assertNotNull(response.getBody());
        assertEquals(CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Cuenta cuentaCreada = response.getBody();
        assertNotNull(cuentaCreada);
        assertEquals(3L, cuentaCreada.getId());
        assertEquals("Miguel", cuentaCreada.getNombre());
        assertEquals("3000.12", cuentaCreada.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    void testDeleteById() {
        ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());
        assertEquals(3, cuentas.size());

//        client.delete(crearUri("/api/cuentas/3"));
//        ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/3"), HttpMethod.DELETE, null, Void.class);
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, Void.class, pathVariables);
        assertEquals(NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());
        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> responseDetalle = client.getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);
        assertEquals(NOT_FOUND, responseDetalle.getStatusCode());
        assertFalse(responseDetalle.hasBody());

    }
}