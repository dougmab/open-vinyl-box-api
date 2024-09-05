package com.github.dougmab.openvinylboxapi.controller;

import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.payload.ApiResponse;
import com.github.dougmab.openvinylboxapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService service;

    public ProductController(@Autowired ProductService service) {
        this.service = service;
    }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<ProductDTO>>> findAll(Pageable pageable) {
        Page<ProductDTO> list = service.findAllPaged(pageable);

        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> insert(@RequestBody @Valid ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(ApiResponse.ok(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> update(@PathVariable Long id, @RequestBody @Valid ProductDTO newDto) {
        newDto = service.update(id, newDto);

        return ResponseEntity.ok(ApiResponse.ok(newDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
