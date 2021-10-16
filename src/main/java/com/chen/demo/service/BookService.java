package com.chen.demo.service;

import com.chen.demo.domain.Book;

/**
 * HelloService
 *
 * @author chenz
 * @version 1.0
 * @date 2021/10/16
 */
public interface BookService {
    Book queryOne(Long id);
}
