package com.hgcode.junit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @GetMapping("/books")
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    @GetMapping("/books/{id}")
    public Book getBookById(@PathVariable Long id){
        return bookRepository.findById(id).get();
    }

    @PostMapping("/books")
    public Book createBook(@RequestBody Book book){
        return bookRepository.save(book);
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) throws ClassNotFoundException {
        if (book == null || book.getId() == null){
            throw new ClassNotFoundException("Book or id : null");
        }
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (!optionalBook.isPresent()){
            throw new ClassNotFoundException("Book is not exist");
        }
        Book existBook = optionalBook.get();
        existBook.setName(book.getName());
        existBook.setSummary(book.getSummary());
        existBook.setRating(book.getRating());
        return bookRepository.save(existBook);
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) throws ClassNotFoundException {
        if (!bookRepository.findById(id).isPresent()){
            throw new ClassNotFoundException("Book id " + id + "not exist");
        }
        bookRepository.deleteById(id);
    }
}
