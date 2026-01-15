package rest.Controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rest.Model.Proba;
import rest.Persistence.Interface.IProbaRepository;
import rest.Start.ServiceException;

import java.util.List;

@RestController
@RequestMapping("/api/probas")
public class ProbaController {
    private final IProbaRepository probaRepository;

    public ProbaController(IProbaRepository probaRepository) {
        this.probaRepository = probaRepository;
    }

    @GetMapping
    public List<Proba> getAllProbas() {
        return probaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proba> getProbaById(@PathVariable Integer id) {
        return probaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> createProba(@Valid @RequestBody Proba proba, BindingResult result) {
        System.out.println("Validation errors: " + result.getAllErrors());
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (probaRepository.findByDistantaAndStil(proba.getDistanta(), proba.getStil()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Proba already exists");
        }
        proba.setId(null);
        Proba saved = probaRepository.save(proba);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProba(@PathVariable Integer id, @Valid @RequestBody Proba proba, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (!probaRepository.existsById(id)) {
            throw new ServiceException("Proba with id " + id + " not found");
        }
        proba.setId(id);
        Proba updated = probaRepository.save(proba);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProba(@PathVariable Integer id) {
        if (!probaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        probaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-validation")
    public ResponseEntity<?> testValidation(@Valid @RequestBody Proba proba, BindingResult result) {
        System.out.println("Test validation errors: " + result.getAllErrors());
        return result.hasErrors() ? ResponseEntity.badRequest().body(result.getAllErrors()) : ResponseEntity.ok("Valid");
    }
}