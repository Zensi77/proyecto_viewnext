package com.juanma.proyecto_vn.interfaces.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.domain.service.IProviderService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.ProviderDtoMapper;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {
    @Autowired
    private IProviderService providerService;

    @Autowired
    private ProviderDtoMapper providerDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<ProviderDto>> getAllProviders() {
        List<ProviderDto> providers = providerService.getAllProviders().stream()
                .map(providerDtoMapper::toDto).toList();

        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> getProvider(@PathVariable UUID id) {
        ProviderDto provider = providerDtoMapper.toDto(providerService.getProvider(id));
        return ResponseEntity.ok(provider);
    }

    @PostMapping("/")
    public ResponseEntity<?> createProvider(@RequestBody @Valid ProviderDto providerDto, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(providerDtoMapper.toDto(providerService.createProvider(providerDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable @Valid UUID id, @RequestBody ProviderDto providerDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        ProviderDto updatedProvider = providerDtoMapper.toDto(providerService.updateProvider(id, providerDto));
        return ResponseEntity.ok(updatedProvider);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        providerService.deleteProvider(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put("message", "Error en campo " + err.getField() + ": " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

}
