package com.nikitakosh.BookBot.service;

import com.nikitakosh.BookBot.exceptions.UserNotFoundException;
import com.nikitakosh.BookBot.model.Book;
import com.nikitakosh.BookBot.model.User;
import com.nikitakosh.BookBot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookService bookService;
    private final GoogleLibraryService googleLibraryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found by id"));
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public List<Book> getUserSavedBooks(Long chatId) {
        return findUserByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("user not found by chat_id"))
                .getSelectedBooks();
    }

    @Transactional
    public void selectBookByUser(Long userId, Long bookId) {
        User user = findById(userId);
        user.getSelectedBooks().add(bookService.findById(bookId));
    }

    @Transactional
    public boolean saveBook(Long chatId, String libraryId) {
        if (findUserByChatId(chatId).isEmpty()) {
            User user = new User();
            user.setChatId(chatId);
            user.setSelectedBooks(new ArrayList<>());
            save(user);
        }
        User user = findUserByChatId(chatId).get();
        Book book;
        if (bookService.findByLibraryId(libraryId).isEmpty()) {
            book = googleLibraryService.findBookByIdLibrary(String.valueOf(libraryId));
            bookService.save(book);
        }
        book = bookService.findByLibraryId(libraryId).get();
        LOGGER.info(book.getTitle());
        LOGGER.info(book.getUsers().toString());
        if (book.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return false;
        } else {
            selectBookByUser(user.getId(), book.getId());
        }
        return true;
    }

    @Transactional
    public void removeSavedBook(Long chatId, String libraryId) {
        User user = findUserByChatId(chatId)
                .orElseThrow(() -> new UserNotFoundException("user not found by chat_id"));
        user.getSelectedBooks().removeIf(book -> Objects.equals(book.getLibraryId(), libraryId));
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

}
