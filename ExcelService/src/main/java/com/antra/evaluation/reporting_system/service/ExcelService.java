package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.model.ExcelRequest;
import com.antra.evaluation.reporting_system.entity.ExcelFile;

public interface ExcelService {
    ExcelFile generateFile(ExcelRequest request);

    ExcelFile updateFile(ExcelRequest request, String id);

    ExcelFile deleteFile(String id);
}
