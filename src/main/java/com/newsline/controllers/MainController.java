package com.newsline.controllers;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;
import com.newsline.service.NewsService;
import com.newsline.service.NewsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {
    @Autowired
    private NewsService newsService;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/newsline/page/{page}")
    public ModelAndView listArticlesPageByPage(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("newsline");
        PageRequest pageable = PageRequest.of(page - 1, newsService.getPagesAmount());
        Page<News> articlePage = newsService.getPaginatedNews(pageable);
        int totalPages = articlePage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("news", articlePage.getContent());
        modelAndView.addObject("pagesAmount", newsService.getPagesAmount());
        return modelAndView;
    }

    @RequestMapping(value="/choose", method = RequestMethod.POST)
    public String changePagesAmount (@Valid String pagesAmount) {
        newsService.setPagesAmount(Integer.parseInt(pagesAmount));
        return "redirect:/newsline/page/1";
    }
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddNewsForm(News news) {
        return "add";
    }

//    @RequestMapping(value = "/add", method = RequestMethod.GET)
//    public String add(Model model) {
//        model.addAttribute("addedNews", new News());
//        return "/add";
//    }

    @RequestMapping(value="/new", method = RequestMethod.POST)
    public String addNews (@Valid News news, BindingResult bindingResult, Model model) {
//        ModelAttribute
        System.out.println("Title + " + news.getTitle());
        System.out.println("Date + " + news.getDate());
        System.out.println("Text + " + news.getText());
        System.out.println("image + " + news.getImage());
        if(news.getImage() != null){
            System.out.println(news.getImage().length);
        }
        if (bindingResult.hasErrors()) {
            return "add";
        }
        return "redirect:/";
    }
}
