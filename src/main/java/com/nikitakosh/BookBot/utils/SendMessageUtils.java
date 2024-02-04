package com.nikitakosh.BookBot.utils;

import com.nikitakosh.BookBot.model.Book;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class SendMessageUtils {


    public static SendMessage createBookMessage(Long chatId, Book book) {
        String message = String.format("""
                        **Название книги:** *%s*
                                            
                        **Автор(ы):** *%s*
                                            
                        **Описание:** *%s*
                                            
                        **Дата публикации:** *%s*
                                            
                        **Количество страниц:** *%d*
                                            
                        **Ссылка на страницу книги:** [Перейти](%s)
                                            
                        """,
                book.getTitle(),
                book.getAuthors(),
                book.getDescription(),
                book.getPublishedDate(),
                book.getPageCount(),
                book.getPreviewLink());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public static SendMessage createButtonForMessage(SendMessage sendMessage, String text, String callbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton saveButton = new InlineKeyboardButton();
        saveButton.setText(text);
        saveButton.setCallbackData(callbackData);
        inlineKeyboardMarkup.setKeyboard(List.of(List.of(saveButton)));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
