package com.nikitakosh.BookBot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikitakosh.BookBot.model.Book;

import java.util.ArrayList;
import java.util.List;

public class JsonMapper {
    public static Book getBookFromJson(String bodyBookPage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Book book = new Book();
        try {
            JsonNode jsonBook = objectMapper.readTree(bodyBookPage);
            book.setLibraryId(jsonBook.get("id").asText());
            book.setTitle(jsonBook.get("volumeInfo").get("title").asText());
            List<String> authors = new ArrayList<>();
            JsonNode jsonAuthors = jsonBook.get("volumeInfo").get("authors");
            for (JsonNode jsonAuthor : jsonAuthors) {
                authors.add(jsonAuthor.asText());
            }
            book.setAuthors(String.join(", ", authors));
            book.setDescription(jsonBook.get("volumeInfo").get("description") != null ? jsonBook.get("volumeInfo").get("description").asText() : "unknown description");
            book.setPublishedDate(jsonBook.get("volumeInfo").get("publishedDate") != null ? jsonBook.get("volumeInfo").get("publishedDate").asText() : "unknown date");
            book.setPublisher(jsonBook.get("volumeInfo").get("publisher") != null ? jsonBook.get("volumeInfo").get("publisher").asText() : "unknown publisher");
            book.setPageCount(jsonBook.get("volumeInfo").get("pageCount") != null ? jsonBook.get("volumeInfo").get("pageCount").asInt() : 0);
            book.setPreviewLink(jsonBook.get("volumeInfo").get("previewLink") != null ? jsonBook.get("volumeInfo").get("previewLink").asText() : "unknown preview link");
            book.setUsers(new ArrayList<>());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return book;
    }

    public static List<Book> getBooksFromJson(String bodyBooksPage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Book> books = new ArrayList<>();
        try {
            JsonNode jsonBooks = objectMapper.readTree(bodyBooksPage).get("items");
            for (JsonNode jsonBook : jsonBooks) {
                List<String> authors = new ArrayList<>();
                JsonNode jsonAuthors = jsonBook.get("volumeInfo").get("authors");
                for (JsonNode jsonAuthor : jsonAuthors) {
                    authors.add(jsonAuthor.asText());
                }
                Book book = new Book();
                book.setLibraryId(jsonBook.get("id").asText());
                book.setTitle(jsonBook.get("volumeInfo").get("title").asText());
                book.setAuthors(String.join(", ", authors));
                book.setDescription(jsonBook.get("volumeInfo").get("description") != null ? jsonBook.get("volumeInfo").get("description").asText() : "");
                book.setPublishedDate(jsonBook.get("volumeInfo").get("publishedDate") != null ? jsonBook.get("volumeInfo").get("publishedDate").asText() : "unknown date");
                book.setPublisher(jsonBook.get("volumeInfo").get("publisher") != null ? jsonBook.get("volumeInfo").get("publisher").asText() : "unknown publisher");
                book.setPageCount(jsonBook.get("volumeInfo").get("pageCount") != null ? jsonBook.get("volumeInfo").get("pageCount").asInt() : 0);
                book.setPreviewLink(jsonBook.get("volumeInfo").get("previewLink") != null ? jsonBook.get("volumeInfo").get("previewLink").asText() : "unknown preview link");
                book.setUsers(new ArrayList<>());
                books.add(book);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
}
