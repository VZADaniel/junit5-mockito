package com.cursoudemy.springboot.app.controllers;

import com.cursoudemy.springboot.app.models.Cuenta;
import com.cursoudemy.springboot.app.models.TransaccionDto;
import com.cursoudemy.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
    @Autowired
    CuentaService cuentaService;

    @GetMapping
    @ResponseStatus(OK)
    public List<Cuenta> findAll(){
        return cuentaService.findAll();
    }


    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public Cuenta findById(@PathVariable(name = "id") Long id) {
        return cuentaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cuenta save(@RequestBody Cuenta cuenta) {
        return cuentaService.save(cuenta);
    }

    @PostMapping("/transferir")
    @ResponseStatus(OK)
    public ResponseEntity<?> save(@RequestBody TransaccionDto dto) {
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", OK);
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);

        return ResponseEntity.ok(response);
    }
}
