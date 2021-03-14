package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.model.PDFRequest;

import java.io.File;

public interface PDFGenerationService {
    void generate(PDFRequest request, File file);
}