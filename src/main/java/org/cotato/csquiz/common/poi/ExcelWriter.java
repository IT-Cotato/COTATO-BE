package org.cotato.csquiz.common.poi;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RequiredArgsConstructor
public class ExcelWriter {

    public static final String FILE_NAME = "fileName";
    public static final String SHEETS = "sheets";

    private final Map<String, Object> data;
    private final HttpServletResponse response;

    @SuppressWarnings("unchecked")
    public void create() {
        Workbook workbook = new XSSFWorkbook();
        setFileName(response, (String) data.get(FILE_NAME));

        Map<String, ?> dataBySheet = (Map<String, ?>) data.get(SHEETS);

        for (Entry<String, ?> sheetAndDatas : dataBySheet.entrySet()) {
            String sheetName = sheetAndDatas.getKey();
            Sheet sheet = workbook.createSheet(sheetName);
            createHeaderRow(sheet, sheetAndDatas.getValue());
            createDataRow(sheet, sheetAndDatas.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void createHeaderRow(Sheet sheet, Object datas) {
        Row headerRow = sheet.createRow(0);
        List<ExcelData> excelData = (List<ExcelData>) datas;
        int cellNumber = 0;
        ExcelData cellData = excelData.get(0);
        List<CellData> headers = cellData.headers();

        for (CellData header : headers) {
            Cell cell = headerRow.createCell(cellNumber++);
            cell.setCellValue(header.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void createDataRow(Sheet sheet, Object datas) {
        List<ExcelData> excelData = (List<ExcelData>) datas;

        int rowNumber = 1;
        for (ExcelData columnValue : excelData) {
            Row row = sheet.createRow(rowNumber++);
            createData(row, columnValue.datas());
        }
    }

    private void createData(Row row, List<CellData> datas) {
        int columnNumber = 0;
        for (CellData cellData : datas) {
            Cell cell = row.createCell(columnNumber++);
            cell.setCellValue(cellData.getValue());
        }
    }

    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + ".xlsx" + "\"");
    }
}
