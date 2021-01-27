package com.adminportal.Adminportal.service;

import com.adminportal.Adminportal.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book save(Book book);

    List<Book> findAll();

    Book getOne(Long id);
}
