package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.model.PDFRequest;
import com.antra.evaluation.reporting_system.entity.PDFFile;

public interface PDFService {
    PDFFile generateFile(PDFRequest request);

    PDFFile updateFile(PDFRequest request, String id);

    PDFFile deleteFile(String id);
}
