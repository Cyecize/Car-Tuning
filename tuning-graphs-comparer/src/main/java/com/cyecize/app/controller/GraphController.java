package com.cyecize.app.controller;

import com.cyecize.app.Endpoints;
import com.cyecize.app.dto.CreateDiffDto;
import com.cyecize.app.service.GraphDiffService;
import com.cyecize.solet.HttpSoletResponse;
import com.cyecize.summer.areas.validation.annotations.Valid;
import com.cyecize.summer.areas.validation.interfaces.BindingResult;
import com.cyecize.summer.common.annotations.Controller;
import com.cyecize.summer.common.annotations.routing.GetMapping;
import com.cyecize.summer.common.annotations.routing.PostMapping;
import com.cyecize.summer.common.models.ModelAndView;
import com.cyecize.summer.common.models.RedirectAttributes;
import com.google.common.net.MediaType;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    public Object graphPostAction(@Valid CreateDiffDto dto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  HttpSoletResponse httpSoletResponse) throws IOException {
        redirectAttributes.addAttribute("model", dto);
        if (bindingResult.hasErrors()) {
            return super.redirect(Endpoints.GRAPHS);
        }

        final Workbook workbook = this.graphDiffService.generateDiffFile(dto);

        final String fileName = String.format("%s_%s.xlsx",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss")),
                Objects.requireNonNullElse(dto.getFileName(), "_graph_diff_results")
        );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (workbook) {
            workbook.write(outputStream);
        }

        httpSoletResponse.setContent(outputStream.toByteArray());
        httpSoletResponse.addHeader("Content-Type", "OOXML_SHEET");
        httpSoletResponse.addHeader("Content-disposition", "attachment; filename=" + fileName);
        httpSoletResponse.addHeader("Content-Length", outputStream.size() + "");

        //Returning null will prevent summer from overriding the HTTP Response content.
        return null;
    }
}
