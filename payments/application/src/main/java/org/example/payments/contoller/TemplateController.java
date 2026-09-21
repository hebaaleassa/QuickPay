package org.example.payments.contoller;

import com.progressoft.training.fileparser.domain.Template;
import org.example.payments.mapper.TemplateMapper;
import org.example.payments.resource.TemplateRequest;
import org.example.payments.resource.TemplateResponse;
import org.example.payments.service.TemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;
    private final TemplateMapper mapper;

    public TemplateController(TemplateService service, TemplateMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAll() {

        List<TemplateResponse> responses = service.findAll().stream().map(mapper::toResponse).toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<TemplateResponse> getBy(@PathVariable String name) {
        return service.findByName(name).map(mapper::toResponse).map
                (ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getBy(@PathVariable Long id) {
        return service.findBy(id).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@RequestBody TemplateRequest request) {
        Template template = mapper.toDomain(request);
        Template created = service.create(template);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }


    @PutMapping ("/{name}")
    public ResponseEntity<TemplateResponse> update(@PathVariable String name, @RequestBody TemplateRequest request) {
        request.setName(name);
        Template template = mapper.toDomain(request);
        Template updated = service.update(template);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        service.delete(name);
        return ResponseEntity.noContent().build();
    }
}
