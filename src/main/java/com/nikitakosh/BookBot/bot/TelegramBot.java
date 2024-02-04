package com.nikitakosh.BookBot.bot;

import com.nikitakosh.BookBot.config.BotConfig;
import com.nikitakosh.BookBot.model.Book;
import com.nikitakosh.BookBot.service.BookService;
import com.nikitakosh.BookBot.service.GoogleLibraryService;
import com.nikitakosh.BookBot.service.UserService;
import com.nikitakosh.BookBot.utils.SendMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;
    private final GoogleLibraryService googleLibraryService;
    private final BookService bookService;
    private final UserService userService;
    private final Map<Long, Command> userLastCommand;

    @Autowired
    public TelegramBot(BotConfig botConfig, BookService bookService, GoogleLibraryService googleLibraryService, UserService userService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.googleLibraryService = googleLibraryService;
        this.bookService = bookService;
        this.userService = userService;
        this.userLastCommand = new HashMap<>();
    }


    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String[] callbackData = update.getCallbackQuery().getData().split(" ");
            Long chatId = update.getCallbackQuery().getFrom().getId();
            switch (callbackData[0]) {
                case "save" -> {
                    if (!userService.saveBook(chatId, callbackData[1])) {
                        sendMessage(chatId, "This book already saved");
                    }
                }
                case "remove" -> userService.removeSavedBook(chatId, callbackData[1]);
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            LOGGER.info("chatId = " + chatId);
            if (message.startsWith("/")) {
                switch (message) {
                    case "/find_books_by_title" -> {
                        userLastCommand.put(chatId, Command.FIND_BY_TITLE);
                        sendMessage(chatId, "Please, enter a title of book");
                    }
                    case "/find_books_by_author" -> {
                        userLastCommand.put(chatId, Command.FIND_BY_AUTHOR);
                        sendMessage(chatId, "Please, enter a author of book");
                    }
                    case "/saved_books" -> getUserSavedBooks(update.getMessage().getChatId());
                    default -> sendMessage(chatId, "Unrecognized command. Choose command from menu");
                }
            } else {
                if (userLastCommand.containsKey(chatId)) {
                    switch (userLastCommand.get(chatId)) {
                        case FIND_BY_TITLE -> {
                            findBooksByTitle(chatId, message);
                            userLastCommand.remove(chatId);
                        }
                        case FIND_BY_AUTHOR -> {
                            findBooksByAuthor(chatId, message);
                            userLastCommand.remove(chatId);
                        }
                    }
                } else {
                    sendMessage(chatId, "Unrecognized command. Choose command from menu");
                }
            }

        }
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(
                String.valueOf(chatId),
                message
        );
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void getUserSavedBooks(Long chatId) {
        List<Book> userSavedBooks = userService.getUserSavedBooks(chatId);
        for (Book book : userSavedBooks) {
            SendMessage bookMessage = SendMessageUtils.createButtonForMessage(
                    SendMessageUtils.createBookMessage(chatId, book),
                    "Remove book",
                    "remove " + book.getLibraryId()
            );
            try {
                execute(bookMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void findBooksByTitle(Long chatId, String title) {
        for (Book book : googleLibraryService.findBooksByTitle(title)) {
            SendMessage bookMessage = SendMessageUtils.createButtonForMessage(
                    SendMessageUtils.createBookMessage(chatId, book),
                    "Save book",
                    "save " + book.getLibraryId()
            );
            try {
                execute(bookMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void findBooksByAuthor(Long chatId, String author) {
        for (Book book : googleLibraryService.findBooksByAuthor(author)) {
            SendMessage bookMessage = SendMessageUtils.createButtonForMessage(
                    SendMessageUtils.createBookMessage(chatId, book),
                    "Save book",
                    "save " + book.getLibraryId()
            );
            try {
                execute(bookMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
