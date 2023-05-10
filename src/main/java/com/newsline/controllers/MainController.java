package com.newsline.controllers;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
    @Autowired
    private NewsDAO newsDAO;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
        News news = newsDAO.findAllByOrderById().get(0);
//        System.out.println(news.);
        model.addAttribute("news", newsDAO.findAllByOrderById());
        return "index";
    }
}
