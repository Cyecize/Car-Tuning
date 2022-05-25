package com.cyecize.app.controller;

import com.cyecize.summer.common.annotations.Controller;
import com.cyecize.summer.common.annotations.routing.GetMapping;
import com.cyecize.summer.common.models.ModelAndView;

@Controller
public class HomeController {
    @GetMapping("/")
    public ModelAndView homeAction() {
        return new ModelAndView("pages/index.html.twig");
    }
}
