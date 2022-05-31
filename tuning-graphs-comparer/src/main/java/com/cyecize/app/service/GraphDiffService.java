package com.cyecize.app.service;

import com.cyecize.app.dto.CreateDiffDto;

public interface GraphDiffService {

    String generateDiffFile(CreateDiffDto dto);
}
