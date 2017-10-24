package com.example.omi.goodreadsapidemo.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Created by omi on 9/28/2017.
 */

@Root(strict=false,name="work")
public class Book {


    @Path("best_book")
    @Element(name="id")
    private String bookId;


    @Path("best_book")
    @Element(name="title")
    private String bookName;


    @Path("best_book/author")
    @Element(name="id")
    private String authorId;


    @Path("best_book/author")
    @Element(name = "name")
    private String authorName;


    @Path("best_book")
    @Element(name = "small_image_url")
    private String imageUrl;

    //@Path("average_rating")
    @Element(name = "average_rating")
    private String rating;



    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Book(String bookName, String authorName) {
        this.bookName = bookName;
        this.authorName = authorName;
    }

    public Book() {
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


}
