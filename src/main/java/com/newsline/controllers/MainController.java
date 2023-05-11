package com.newsline.controllers;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;
import com.newsline.service.NewsService;
import com.newsline.service.NewsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {
    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {
        News news = newsDAO.findAllByOrderById().get(0);
//        System.out.println(news.);
        model.addAttribute("news", newsDAO.findAllByOrderById());
        return "index";
    }


    @RequestMapping(value = "/page/{page}")
    public ModelAndView listArticlesPageByPage(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("index");
        PageRequest pageable = PageRequest.of(page - 1, newsService.getPageAmount());
        Page<News> articlePage = newsService.getPaginatedNews(pageable);
        int totalPages = articlePage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("news", articlePage.getContent());
        return modelAndView;
    }
}
