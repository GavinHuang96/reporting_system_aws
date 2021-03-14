package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.exception.ExcelGenerationException;
import com.antra.evaluation.reporting_system.model.ExcelRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

@Service
public class ExcelGenerationServiceImpl implements ExcelGenerationService {
    private void validateData(List<String> header, List<List<String>> data) {
        for (List<String> row : data) {
            if (row.size() != header.size()) {
                throw new RuntimeException("Excel Data Error: data has different length from header");
            }
        }
    }

    @Override
    public void generate(ExcelRequest request, File file) {
        List<String> header = request.getHeaders();
        List<List<String>> data = request.getData();
        validateData(header, data);

        // create excel
        XSSFWorkbook workbook = new XSSFWorkbook();

        // create sheet
        Sheet sheet = workbook.createSheet("sheet1");

        // create sheet header
        Row excelHeader = sheet.createRow(0);
        for (int i = 0; i < header.size(); i++) {
            Cell headerCell = excelHeader.createCell(i);
            headerCell.setCellValue(header.get(i));
        }

        // create sheet data
        for (int i = 0; i < data.size(); i++) {
            Row excelDataRow = sheet.createRow(i + 1);
            List<String> dataRow = data.get(i);
            for (int j = 0; j < dataRow.size(); j++) {
                Cell cell = excelDataRow.createCell(j);
                cell.setCellValue(dataRow.get(j));
            }
        }

        // resize data
        for (int i = 0; i < header.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // write to file
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new ExcelGenerationException(e);
        }
    }
}
