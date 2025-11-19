package com.example.demo.service;

import com.example.demo.dto.IssueRequest;
import com.example.demo.model.Book;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public LibraryService(BookRepository bookRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // Book CRUD
    public Book addOrUpdateBook(Book book) {
        if (book.getTotalCopies() != null && book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        return bookRepository.save(book);
    }

    public List<Book> listBooks() { return bookRepository.findAll(); }

    public Book getBook(Long id) { return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found")); }

    public Book updateBook(Long id, Book updated) {
        Book b = getBook(id);
        b.setTitle(updated.getTitle());
        b.setAuthor(updated.getAuthor());
        b.setCategory(updated.getCategory());
        b.setTotalCopies(updated.getTotalCopies());
        b.setAvailableCopies(updated.getAvailableCopies());
        return bookRepository.save(b);
    }

    public void deleteBook(Long id) { bookRepository.deleteById(id); }

    public List<User> listUsers() { return userRepository.findAll(); }

    @Transactional
    public Transaction issueBook(IssueRequest req) {
        Book book = bookRepository.findById(req.getBookId()).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available");
        }

        int days = (req.getDays() == null) ? 7 : req.getDays();
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(days);

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setUser(user);
        transaction.setIssueDate(issueDate);
        transaction.setDueDate(dueDate);
        transaction.setFine(0.0);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction returnBook(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (tx.getReturnDate() != null) throw new RuntimeException("Book already returned");

        LocalDate returnDate = LocalDate.now();
        tx.setReturnDate(returnDate);

        long overdue = 0;
        if (tx.getDueDate() != null) {
            overdue = ChronoUnit.DAYS.between(tx.getDueDate(), returnDate);
            if (overdue < 0) overdue = 0;
        }
        double fine = overdue * 5.0;
        tx.setFine(fine);

        Book b = tx.getBook();
        b.setAvailableCopies((b.getAvailableCopies() == null ? 0 : b.getAvailableCopies()) + 1);
        bookRepository.save(b);

        return transactionRepository.save(tx);
    }

    public List<Transaction> listActiveIssues() {
        return transactionRepository.findByReturnDateIsNull();
    }

    public List<Transaction> listAllTransactions() {
        return transactionRepository.findAll();
    }
}
