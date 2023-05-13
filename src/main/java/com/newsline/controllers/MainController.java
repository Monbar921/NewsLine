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
public final class MainController {
    @Autowired
    private NewsService newsService;

    /*    Method, that handle "GET" request and show start page*/
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /*    Method, that handle "GET" request and show each newsline page*/
    @RequestMapping(value = "/newsline/page/{page}", method = RequestMethod.GET)
    public ModelAndView listArticlesPageByPage(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("newsline");
        /*        Request of needed news of page create at this line*/
        PageRequest pageable = PageRequest.of(page - 1, newsService.getNewsOnPageAmount());
        /*        Get page with needed amount of news on this page*/
        Page<News> articlePage = newsService.getPaginatedNews(pageable);
        int totalPages = articlePage.getTotalPages();
        if (totalPages > 0) {
            /*        Create list of pages indexes for showing this numbers on view pages*/
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("news", articlePage.getContent());
        modelAndView.addObject("newsOnPageAmount", newsService.getNewsOnPageAmount());
        return modelAndView;
    }

    /*    Method, that handle "POST" request when user change amount of news that show on each page*/
    @RequestMapping(value = "/choose", method = RequestMethod.POST)
    public String changePagesAmount(@Valid String newsOnPageAmount) {
        newsService.setNewsOnPageAmount(Integer.parseInt(newsOnPageAmount));
        return "redirect:/newsline/page/1";
    }

    /*    Method, that handle "GET" request when user want to add news*/
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddNewsForm(News news) {
        return "add";
    }

    /**
     * @param news               - Valid annotation on news parameter checks bound which put on News class private fields
     *                           (such as NotNull and Pattern)
     * @param bindingResult      - is used for check if html input forms have errors
     * @param file               - uploaded file
     * @param redirectAttributes - object for transport message about successful/unsuccessful insert operation
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String addNews(@Valid News news, BindingResult bindingResult, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes)
            throws IOException {
        /*    Method, that handle "POST" request after user fill news form.*/

        if (bindingResult.hasErrors()) {
            return "add";
        }
        String resultMessage = "Новость с таким заголовком и датой уже существует!";
        if (!newsService.isNewsExist(news)) {
            news.setImage(file.getBytes());
            resultMessage = "Вы успешно добавили новость!";
            newsService.saveNews(news);
        }
        redirectAttributes.addFlashAttribute("message",
                resultMessage);
        return "redirect:/";
    }
}
