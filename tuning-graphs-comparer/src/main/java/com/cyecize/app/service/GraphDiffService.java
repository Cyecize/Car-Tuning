package com.cyecize.app.service;

import com.cyecize.app.dto.CreateDiffDto;
import org.apache.poi.ss.usermodel.Workbook;

public interface GraphDiffService {

    Workbook generateDiffFile(CreateDiffDto dto);
}
