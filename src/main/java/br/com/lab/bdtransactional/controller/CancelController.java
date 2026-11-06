package br.com.lab.bdtransactional.controller;

import br.com.lab.bdtransactional.dto.CancelRequest;
import br.com.lab.bdtransactional.exception.CancelNotFoundException;
import br.com.lab.bdtransactional.exception.SaleNotFoundException;
import br.com.lab.bdtransactional.model.Cancel;
import br.com.lab.bdtransactional.service.CancelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cancels")
public class CancelController {

    private final CancelService cancelService;

    public CancelController(CancelService cancelService) {
        this.cancelService = cancelService;
    }

    @PostMapping
    public ResponseEntity<Cancel> create(@RequestBody CancelRequest request) {
        try {
            var cancel = cancelService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cancel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cancel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cancelService.findById(id));
    }

    @ExceptionHandler({SaleNotFoundException.class, CancelNotFoundException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
