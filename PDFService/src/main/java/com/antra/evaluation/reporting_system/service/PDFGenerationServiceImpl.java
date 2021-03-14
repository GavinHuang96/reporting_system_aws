package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.model.PDFRequest;
import com.antra.evaluation.reporting_system.exception.PDFGenerationException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class PDFGenerationServiceImpl implements PDFGenerationService {
    public void generate(PDFRequest request, File file) {
        // content = header + data
        StringBuilder builder = new StringBuilder();
        builder.append(request.getHeaders());
        builder.append("\r\n");
        for (List<String> row : request.getData()) {
            builder.append(String.join(", ", row));
            builder.append("\r\n");
        }

        // parameter of jasper
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("desc_str", request.getDescription());
        parameters.put("content_str", builder.toString());

        // data source of jasper
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of("Empty"));

        // generate file
        try {
            JasperPrint jprint = JasperFillManager.fillReport(ResourceUtils.getFile("classpath:Coffee_Landscape.jasper").getAbsolutePath(), parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jprint, file.getAbsolutePath());
        } catch (Exception e) {
            throw new PDFGenerationException(e);
        }
    }
}
