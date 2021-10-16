package com.chen.demo.service.impl;

import com.chen.demo.domain.Book;
import com.chen.demo.service.BookService;
import org.simpleframework.core.annotation.Service;

/**
 * HelloServiceImpl
 *
 * @author chenz
 * @version 1.0
 * @date 2021/10/16
 */
@Service
public class BookServiceImpl implements BookService {

    @Override
    public Book queryOne(Long id) {
        Book book;
        if (id == 1L) {
            book = new Book(1L, "《Minecraft Tutorials》", "Asura");
        } else if (id == 2L) {
            book = new Book(1L, "《Linux奇妙之旅》", "Unknown");
        } else if (id == 3L) {
            book = new Book(1L, "《Hello World》", "xxx");
        } else {
            book = null;
        }
        return book;
    }
}
