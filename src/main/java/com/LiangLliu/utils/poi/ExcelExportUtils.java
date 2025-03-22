package com.LiangLliu.utils.poi;

import com.LiangLliu.utils.file.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExcelExportUtils {
    public static final String ERROR_REPORT_FOLDER =
            System.getProperty("user.home") + File.separator + "file" + File.separator + "download" + File.separator;


    public static <R, H> File generateExcel(String fileName,
                                            Iterable<R> resources,
                                            List<H> headers,
                                            Function<H, String> headerValueMapper,
                                            BiFunction<R, H, String> cellValueMapper) {

        return generateExcel(fileName, resources, headers, headerValueMapper, cellValueMapper, 20);
    }

    public static <R, H> File generateExcel(String fileName,
                                            Iterable<R> resources,
                                            List<H> headers,
                                            Function<H, String> headerValueMapper,
                                            BiFunction<R, H, String> cellValueMapper, int columnWidth) {
        return generateExcel(fileName, resources, headers, headerValueMapper, cellValueMapper,
                workbook -> new HashMap<>(), columnWidth);
    }

    public static <R, H> File generateExcel(String fileName,
                                            Iterable<R> resources,
                                            List<H> headers,
                                            Function<H, String> headerValueMapper,
                                            BiFunction<R, H, String> cellValueMapper,
                                            Function<Workbook, Map<H, CellStyle>> cellStyleMapper) {
        return generateExcel(fileName, resources, headers, headerValueMapper, cellValueMapper, cellStyleMapper, 20);
    }

    public static <R, H> File generateExcel(String fileName,
                                            Iterable<R> resources,
                                            List<H> headers,
                                            Function<H, String> headerValueMapper,
                                            BiFunction<R, H, String> cellValueMapper,
                                            Function<Workbook, Map<H, CellStyle>> cellStyleMapper,
                                            int columnWidth) {
        try (Workbook workbook = WorkbookFactory.create(true)) {
            Sheet sheet = workbook.createSheet();
            sheet.setDefaultColumnWidth(columnWidth);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            generateHeader(sheet, headerStyle, headers, headerValueMapper);

            var cellStyles = cellStyleMapper.apply(workbook);
            generateDataRows(sheet, resources, headers, cellValueMapper, cellStyles);

            return FileUtils.save(workbook, ERROR_REPORT_FOLDER + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Generate report failed");
        }
    }

    private static <H> void generateHeader(Sheet sheet, CellStyle headerStyle, List<H> headers,
                                           Function<H, String> headerValueMapper) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0, headersSize = headers.size(); i < headersSize; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerValueMapper.apply(headers.get(i)));
            headerCell.setCellStyle(headerStyle);
        }
    }

    private static <R, H> void generateDataRows(Sheet sheet,
                                                Iterable<R> resources,
                                                List<H> headers,
                                                BiFunction<R, H, String> cellValueMapper) {
        generateDataRows(sheet, resources, headers, cellValueMapper, new HashMap<>());
    }


    private static <R, H> void generateDataRows(Sheet sheet, Iterable<R> resources, List<H> headers,
                                                BiFunction<R, H, String> cellValueMapper, Map<H, CellStyle> cellStyles) {
        if (headers == null || cellStyles == null) {
            throw new IllegalStateException("Unexpected header or cell style");
        }

        if (resources == null) {
            return;
        }

        for (var resource : resources) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            for (int i = 0, headersSize = headers.size(); i < headersSize; i++) {
                var header = headers.get(i);
                var cellValue = cellValueMapper.apply(resource, header);
                Cell cell = row.createCell(i);
                cell.setCellValue(cellValue);

                if (cellStyles.get(header) != null) {
                    cell.setCellStyle(cellStyles.get(header));
                }
            }
        }
    }


    /**
     * 大数据
     */
    public static <R, H> File generateWithSXSS(String fileName,
                                               Iterable<R> resources,
                                               List<H> headers,
                                               Function<H, String> headerValueMapper,
                                               BiFunction<R, H, String> cellValueMapper,
                                               Function<Workbook, Map<H, CellStyle>> cellStyleMapper,
                                               int columnWidth) {

        File excelFile = FileUtils.create(fileName);

        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(columnWidth);

        CellStyle headerStyle = generateCellStyle(workbook);

        generateHeader(sheet, headerStyle, headers, headerValueMapper);

        var cellStyles = cellStyleMapper.apply(workbook);
        generateDataRows(sheet, resources, headers, cellValueMapper, cellStyles);

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(excelFile)
        ) {
            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return excelFile;
        } catch (IOException ignored) {

        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    private static CellStyle generateCellStyle(Workbook workbook) {

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    public static <R, H> File generateWithSXSS(String fileName,
                                               Iterable<R> resources,
                                               List<H> headers,
                                               Function<H, String> headerValueMapper,
                                               BiFunction<R, H, String> cellValueMapper) {
        return generateWithSXSS(fileName, resources, headers, headerValueMapper, cellValueMapper, 20);
    }

    public static <R, H> File generateWithSXSS(String fileName,
                                               Iterable<R> resources,
                                               List<H> headers,
                                               Function<H, String> headerValueMapper,
                                               BiFunction<R, H, String> cellValueMapper, int columnWidth) {
        return generateWithSXSS(fileName, resources, headers, headerValueMapper, cellValueMapper,
                workbook -> new HashMap<>(), columnWidth);
    }

    public static <R, H> File generateWithSXSS(String fileName,
                                               Iterable<R> resources,
                                               List<H> headers,
                                               Function<H, String> headerValueMapper,
                                               BiFunction<R, H, String> cellValueMapper,
                                               Function<Workbook, Map<H, CellStyle>> cellStyleMapper) {
        return generateWithSXSS(fileName, resources, headers, headerValueMapper, cellValueMapper, cellStyleMapper, 20);
    }

}
