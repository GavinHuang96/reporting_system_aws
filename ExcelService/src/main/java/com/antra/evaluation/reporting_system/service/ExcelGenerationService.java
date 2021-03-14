package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.model.ExcelRequest;

import java.io.File;

public interface ExcelGenerationService {
    void generate(ExcelRequest request, File file);
}
