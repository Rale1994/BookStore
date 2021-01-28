package com.adminportal.Adminportal.controller;

import com.adminportal.Adminportal.domain.Book;
import com.adminportal.Adminportal.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addBook(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);
        return "addBook";

    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addBookPost(@ModelAttribute("book") Book book, HttpServletRequest request) {
        bookService.save(book);
        MultipartFile bookImage = book.getBookImage();
        try {
            byte[] bytes = bookImage.getBytes();
            String name = book.getId() + ".png";
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
            stream.write(bytes);
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:bookList";

    }

    @RequestMapping("/bookList")
    public String bookList(Model model) {
        List<Book> bookList = bookService.findAll();
        model.addAttribute("bookList", bookList);

        return "bookList";
    }

    @RequestMapping("/bookInfo")
    public String bookInfo(@RequestParam("id") Long id, Model model) {
        Book book = bookService.getOne(id);
        model.addAttribute("book", book);
        return "bookInfo";
    }

    @RequestMapping("/updateBook")
    public String updateBook(@RequestParam("id") Long id, Model model) {
        Book book = bookService.getOne(id);
        model.addAttribute("book", book);
        return "updateBook";
    }

    @RequestMapping(value = "/updateBook", method = RequestMethod.POST)
    public String updateBookPost(@ModelAttribute("book") Book book, HttpServletRequest request) {
        bookService.save(book);
        MultipartFile bookImage = book.getBookImage();
        if (!bookImage.isEmpty()) {
            try {
                byte[] bytes = bookImage.getBytes();
                String name = book.getId() + ".png";

                //first delete old image file
                Files.deleteIfExists(Paths.get("src/main/resources/static/image/book/"+name));

                //now add a new update file image
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
                stream.write(bytes);
                stream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/book/bookInfo?id=" + book.getId();
    }
}
