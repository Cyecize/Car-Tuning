package com.cyecize.app.controller;

import com.cyecize.summer.common.models.ModelAndView;

public class BaseController {
    private ModelAndView finalizeView(ModelAndView modelAndView) {
        //add additional logic here like globals for example
        return modelAndView;
    }

    protected ModelAndView redirect(String url) {
        return new ModelAndView("redirect:" + url);
    }

    protected ModelAndView view(String viewName) {
        return this.view(viewName, new ModelAndView());
    }

    protected ModelAndView view(String viewName, String modelName, Object model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(modelName, model);
        return this.view(viewName, modelAndView);
    }

    protected ModelAndView view(String viewName, ModelAndView modelAndView) {
        modelAndView.setView(viewName);
        return this.finalizeView(modelAndView);
    }
}
