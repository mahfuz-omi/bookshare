package com.example.omi.goodreadsapidemo.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

/**
 * Created by omi on 10/12/2017.
 */

@Root(name = "results", strict = false)
public class Results {

    @ElementList(required = false,entry = "work")
    public List<Book> books;




    public Results(List<Book> bookList) {
        this.books = bookList;
    }



    public Results() {
    }

    public List<Book> getBookList() {
        return books;
    }

    public void setBookList(List<Book> bookList) {
        this.books = bookList;
    }
}
