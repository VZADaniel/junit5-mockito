package com.cursoudemy.springboot.app.controllers;

import com.cursoudemy.springboot.app.models.Cuenta;
import com.cursoudemy.springboot.app.models.TransaccionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {
        //  Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", OK);
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);

        //  When
        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                //  Then
                .expectStatus().isOk()
                .expectBody()
//                .expectBody(String.class)
                .consumeWith(res -> {
                    try {
//                        String jsonStr = res.getResponseBody();
//                        JsonNode json = mapper.readTree(jsonStr);
                        JsonNode json = mapper.readTree(res.getResponseBody());
                        assertEquals("Transferencia realizada con exito", json.path("mensaje").asText());
                        assertEquals(dto.getCuentaOrigenId(), json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals(dto.getMonto().toPlainString(), json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(valor -> assertEquals("Transferencia realizada con exito", valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(mapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void testFindById() throws JsonProcessingException {
        Cuenta cuenta = new Cuenta(1L, "Daniel", new BigDecimal("900.12"));

        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Daniel")
                .jsonPath("$.saldo").isEqualTo(900.12)
                .json(mapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(3)
    void testFindById2() {
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(res -> {
                    Cuenta cuenta = res.getResponseBody();
                    assertEquals("Carlos", cuenta.getNombre());
                    assertEquals("2100.12", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    void testFindAll() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Daniel")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900.12)
                .jsonPath("$[1].nombre").isEqualTo("Carlos")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100.12)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testFindAll2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(res -> {
                    List<Cuenta> cuentas = res.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals("Daniel", cuentas.get(0).getNombre());
                    assertEquals("900.12", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("Carlos", cuentas.get(1).getNombre());
                    assertEquals("2100.12", cuentas.get(1).getSaldo().toPlainString());
                    assertEquals(2L, cuentas.get(1).getId());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(6)
    void testSave() {
        //  Given
        Cuenta cuenta = new Cuenta(null, "Alfredo", new BigDecimal("4000.12"));

        //  When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                //  Then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").value(is("Alfredo"))
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.saldo").isEqualTo(4000.12);
    }

    @Test
    @Order(7)
    void testSave2() {
        //  Given
        Cuenta cuenta = new Cuenta(null, "Camila", new BigDecimal("3000.12"));

        //  When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                //  Then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(res -> {
                    Cuenta c = res.getResponseBody();
                    assertNotNull(c);
                    assertEquals("Camila", c.getNombre());
                    assertEquals("3000.12", c.getSaldo().toPlainString());
                    assertEquals(4L, c.getId());
                });
    }

    @Test
    @Order(8)
    void testDeleteById() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/4").exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/4").exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}