package com.example.demo.controller;

import com.example.demo.dto.IssueRequest;
import com.example.demo.model.Transaction;
import com.example.demo.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final LibraryService svc;

    public TransactionController(LibraryService svc) {
        this.svc = svc;
    }

    @PostMapping("/issue")
    public ResponseEntity<?> issue(@RequestBody IssueRequest req) {
        try {
            Transaction t = svc.issueBook(req);
            return ResponseEntity.ok(t);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<?> ret(@PathVariable Long id) {
        try {
            Transaction t = svc.returnBook(id);
            return ResponseEntity.ok(t);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/active")
    public List<Transaction> active() {
        return svc.listActiveIssues();
    }

    @GetMapping
    public List<Transaction> all() {
        return svc.listAllTransactions();
    }
}
