package com.nikitakosh.BookBot.service;

import com.nikitakosh.BookBot.exceptions.BookNotFoundException;
import com.nikitakosh.BookBot.model.Book;
import com.nikitakosh.BookBot.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    @Value("${google.key}")
    private String key;

    @Transactional(readOnly = true)
    public Book findById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("book not found by id"));
    }

    @Transactional(readOnly = true)
    public Optional<Book> findByLibraryId(String libraryId) {
        return bookRepository.findByLibraryId(libraryId);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

}
