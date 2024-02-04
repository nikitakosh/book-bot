package com.nikitakosh.BookBot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "library_id")
    private String libraryId;
    @Column(name = "title")
    private String title;
    @Column(name = "authors")
    private String authors;
    @Column(name = "description", length = 3000)
    private String description;
    @Column(name = "published_date")
    private String publishedDate;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "page_count")
    private Integer pageCount;
    @Column(name = "preview_link")
    private String previewLink;
    @ManyToMany(mappedBy = "selectedBooks", fetch = FetchType.EAGER)
    private List<User> users;
}
