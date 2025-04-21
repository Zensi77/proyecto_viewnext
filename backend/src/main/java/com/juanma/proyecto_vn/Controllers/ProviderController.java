package com.juanma.proyecto_vn.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;
import com.juanma.proyecto_vn.interfaces.IProviderService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    @Autowired
    private IProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderDto>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> getProvider(@RequestParam String id) {
        return ResponseEntity.ok(providerService.getProvider(id));
    }

    @PostMapping()
    public ResponseEntity<ProviderDto> createProvider(@RequestBody ProviderDto providerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.createProvider(providerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@RequestParam String id) {
        providerService.deleteProvider(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
