package com.nikitakosh.BookBot.repository;

import com.nikitakosh.BookBot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByLibraryId(String libraryId);
}
