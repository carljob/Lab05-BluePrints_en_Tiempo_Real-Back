package com.eci.blueprints.rt;

import com.eci.blueprints.rt.dto.BlueprintUpdate;
import com.eci.blueprints.rt.dto.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el CRUD de blueprints.
 * Expone los endpoints que consume la tabla de planos del frontend.
 */
@RestController
@RequestMapping("/api/blueprints")
@CrossOrigin(origins = "http://localhost:5173")
public class BlueprintRestController {

    private final BlueprintStore store;

    public BlueprintRestController(BlueprintStore store) {
        this.store = store;
    }

    // GET /api/blueprints?author=juan  lista los planos del autor
    @GetMapping
    public List<BlueprintUpdate> listByAuthor(@RequestParam String author) {
        return store.listByAuthor(author);
    }

    // GET /api/blueprints/juan/plano-1  un plano específico
    @GetMapping("/{author}/{name}")
    public BlueprintUpdate getOne(@PathVariable String author, @PathVariable String name) {
        return store.get(author, name);
    }

    // POST /api/blueprints  crea un plano nuevo
    @PostMapping
    public ResponseEntity<BlueprintUpdate> create(@RequestBody CreateRequest req) {
        boolean created = store.create(req.author(), req.name());
        if (!created) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // ya existe
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(store.get(req.author(), req.name()));
    }

    // PUT /api/blueprints/juan/plano-1  actualiza los puntos del plano
    @PutMapping("/{author}/{name}")
    public BlueprintUpdate update(@PathVariable String author, @PathVariable String name,
                                  @RequestBody List<Point> points) {
        store.update(author, name, points);
        return store.get(author, name);
    }

    // DELETE /api/blueprints/juan/plano-1  elimina el plano
    @DeleteMapping("/{author}/{name}")
    public ResponseEntity<Void> delete(@PathVariable String author, @PathVariable String name) {
        store.delete(author, name);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(String author, String name) {}
}