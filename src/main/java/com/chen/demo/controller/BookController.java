package com.chen.demo.controller;

import com.chen.demo.domain.Book;
import com.chen.demo.service.BookService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.type.ModelAndView;
import org.simpleframework.mvc.type.RequestMethod;

/**
 * HelloController
 *
 * @author chenz
 * @version 1.0
 * @date 2021/10/16
 */
@Controller
@RequestMapping("book")
public class BookController {

    @Autowired
    private BookService service;

    @ResponseBody
    @RequestMapping(value = "/one", method = RequestMethod.GET)
    public Book queryOne(@RequestParam("id") Long id) {
        return service.queryOne(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView addBook(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "author") String author
    ) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("/addBook.jsp")
                .addViewData("name", name)
                .addViewData("author", author);

        return modelAndView;
    }
}
