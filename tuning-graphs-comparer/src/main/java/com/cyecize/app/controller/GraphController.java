package com.cyecize.app.controller;

import com.cyecize.app.Endpoints;
import com.cyecize.app.dto.CreateDiffDto;
import com.cyecize.app.service.GraphDiffService;
import com.cyecize.summer.areas.validation.annotations.Valid;
import com.cyecize.summer.areas.validation.interfaces.BindingResult;
import com.cyecize.summer.common.annotations.Controller;
import com.cyecize.summer.common.annotations.routing.GetMapping;
import com.cyecize.summer.common.annotations.routing.PostMapping;
import com.cyecize.summer.common.models.ModelAndView;
import com.cyecize.summer.common.models.RedirectAttributes;

@Controller
public class GraphController extends BaseController {

    private final GraphDiffService graphDiffService;

    public GraphController(GraphDiffService graphDiffService) {
        this.graphDiffService = graphDiffService;
    }

    @GetMapping(Endpoints.GRAPHS)
    public ModelAndView graphsGetAction() {
        return super.view("pages/graphs.html.twig");
    }

    @PostMapping(Endpoints.GRAPHS)
    public ModelAndView graphPostAction(@Valid CreateDiffDto dto,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("model", dto);
        if (bindingResult.hasErrors()) {
            return super.redirect(Endpoints.GRAPHS);
        }

        final String fileName = this.graphDiffService.generateDiffFile(dto);

        return super.redirect(Endpoints.GRAPHS + String.format("?msg=File %s was created!", fileName));
    }
}
