package com.cyecize.app.dto;

import com.cyecize.app.adapters.CheckboxAdapter;
import com.cyecize.summer.areas.validation.annotations.ConvertedBy;
import com.cyecize.summer.areas.validation.constraints.MaxLength;
import com.cyecize.summer.areas.validation.constraints.NotEmpty;
import com.cyecize.summer.areas.validation.constraints.NotNull;

public class CreateDiffDto {

    @MaxLength(length = 50)
    private String fileName;

    @NotNull
    private Integer graphWidth;

    @NotNull
    private Integer graphHeight;

    @NotNull
    @ConvertedBy(CheckboxAdapter.class)
    private Boolean includeDiffValues;

    @NotEmpty
    private String graph1;

    @MaxLength(length = 100)
    private String graph1Name;

    @NotEmpty
    private String graph2;

    @MaxLength(length = 100)
    private String graph2Name;

    private String horizontalHeader;

    private String verticalHeader;

    public String getFileName() {
        return fileName;
    }

    public Integer getGraphWidth() {
        return graphWidth;
    }

    public Integer getGraphHeight() {
        return graphHeight;
    }

    public Boolean getIncludeDiffValues() {
        return includeDiffValues;
    }

    public String getGraph1() {
        return graph1;
    }

    public String getGraph1Name() {
        return graph1Name;
    }

    public String getGraph2() {
        return graph2;
    }

    public String getGraph2Name() {
        return graph2Name;
    }

    public String getHorizontalHeader() {
        return horizontalHeader;
    }

    public String getVerticalHeader() {
        return verticalHeader;
    }
}
