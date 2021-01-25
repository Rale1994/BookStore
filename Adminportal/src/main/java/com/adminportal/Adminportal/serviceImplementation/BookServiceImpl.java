package com.adminportal.Adminportal.serviceImplementation;

import com.adminportal.Adminportal.domain.Book;
import com.adminportal.Adminportal.repository.BookRepository;
import com.adminportal.Adminportal.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return (List<Book>) bookRepository.findAll();
    }
}
