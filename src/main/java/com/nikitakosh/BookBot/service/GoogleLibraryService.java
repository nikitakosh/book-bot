package com.nikitakosh.BookBot.service;

import com.nikitakosh.BookBot.model.Book;
import com.nikitakosh.BookBot.utils.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GoogleLibraryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleLibraryService.class);

    @Value("${google.key}")
    private String key;

    public String sendHttpRequest(String uri, String parameter) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(
                        uri, parameter.replace(" ", "+"), key
                )))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public List<Book> findBooksByTitle(String title) {
        return JsonMapper.getBooksFromJson(
                sendHttpRequest(
                        "https://www.googleapis.com/books/v1/volumes?q=intitle:%s&key=%s",
                        title
                )
        );
    }

    public List<Book> findBooksByAuthor(String author) {
        return JsonMapper.getBooksFromJson(
                sendHttpRequest(
                        "https://www.googleapis.com/books/v1/volumes?q=inauthor:%s&key=%s",
                        author
                )
        );
    }

    public Book findBookByIdLibrary(String bookIdLibrary) {
        return JsonMapper.getBookFromJson(
                sendHttpRequest(
                        "https://www.googleapis.com/books/v1/volumes/%s",
                        bookIdLibrary
                )
        );
    }


}
