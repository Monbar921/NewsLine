package com.newsline.controllers;

import com.newsline.dao.News;
import com.newsline.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {
    @Autowired
    private NewsService newsService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/newsline/page/{page}")
    public ModelAndView listArticlesPageByPage(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("newsline");
        PageRequest pageable = PageRequest.of(page - 1, newsService.getNewsOnPageAmount());
        Page<News> articlePage = newsService.getPaginatedNews(pageable);
        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("news", articlePage.getContent());
        modelAndView.addObject("pagesAmount", newsService.getNewsOnPageAmount());
        return modelAndView;
    }

    @RequestMapping(value = "/choose", method = RequestMethod.POST)
    public String changePagesAmount(@Valid String pagesAmount) {
        newsService.setNewsOnPageAmount(Integer.parseInt(pagesAmount));
        return "redirect:/newsline/page/1";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddNewsForm(News news) {
        return "add";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String addNews(@Valid News news, BindingResult bindingResult, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes)
            throws IOException {
        if (bindingResult.hasErrors()) {
            return "add";
        }
        String resultMessage = "Новость с таким заголовком и датой уже существует!";
        if(!newsService.isNewsExist(news)){
            news.setImage(file.getBytes());
            System.out.println(newsService.isNewsExist(news));
            resultMessage = "Вы успешно добавили новость!";
            newsService.saveNews(news);
        }
        redirectAttributes.addFlashAttribute("message",
                resultMessage);
        return "redirect:/";
    }
}
