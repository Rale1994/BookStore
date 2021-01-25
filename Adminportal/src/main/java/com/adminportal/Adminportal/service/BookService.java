package com.adminportal.Adminportal.service;

import com.adminportal.Adminportal.domain.Book;

import java.util.List;

public interface BookService {

    Book save(Book book);

    List<Book> findAll();
}
