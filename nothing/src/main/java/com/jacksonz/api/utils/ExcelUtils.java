package com.foundation.api.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    /**
     * 导出excel
     *
     * @param response
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel(HttpServletResponse response, List<String> header, List<List<String>> dataList) throws Exception {
        exportExcel(response, "主标题", "副标题", header, dataList);
    }

    /**
     * 导出excel
     *
     * @param response
     * @param title
     * @param subheading
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel(HttpServletResponse response, String title, String subheading, List<String> header, List<List<String>> dataList) throws Exception {
        //获取一个HSSFWorkbook对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle style = getHSSFCellStyle(workbook);
        //创建一个sheet
        HSSFSheet sheet = workbook.createSheet("Sheet1");
        //创建一个标题行
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, header.size());
        //创建一个副标题行
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 0, header.size());
        sheet.addMergedRegion(cellRangeAddress);
        sheet.addMergedRegion(cellRangeAddress2);

        //标题，居中
        HSSFRow row0 = sheet.createRow(0);
        HSSFCell cell0 = row0.createCell(0);
        cell0.setCellValue(title);
        cell0.setCellStyle(style);
        // 第一行
        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = row1.createCell(0);
        //副标题
        cell1.setCellValue(subheading);
        cell1.setCellStyle(style);

        //表头
        HSSFRow row = sheet.createRow(2);

        HSSFCell cell = null;
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(style);
        }

        //数据
        for (int i = 0; i < dataList.size(); i++) {
            row = sheet.createRow(i + 3);
            for (int j = 0; j < dataList.get(i).size(); j++) {
                row.createCell(j).setCellValue(dataList.get(i).get(j));
            }
        }

        OutputStream outputStream = response.getOutputStream();
        //设置页面不缓存
        response.reset();
        String filename = title;
        //设置返回文件名的编码格式
        response.setCharacterEncoding("utf-8");
        filename = URLEncoder.encode(filename, "utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xls");
        response.setContentType("application/msexcel");
        workbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出excel
     *
     * @param title
     * @param subheading
     * @param header
     * @param dataList
     * @throws Exception
     */
    public static void exportExcel(File file, String title, String subheading, List<String> header, List<List<String>> dataList) throws Exception {
        //获取一个HSSFWorkbook对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle style = getHSSFCellStyle(workbook);
        //创建一个sheet
        HSSFSheet sheet = workbook.createSheet("Sheet1");
        //创建一个标题行
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, header.size());
        //创建一个副标题行
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 1, 0, header.size());
        sheet.addMergedRegion(cellRangeAddress);
        sheet.addMergedRegion(cellRangeAddress2);

        //标题，居中
        HSSFRow row0 = sheet.createRow(0);
        HSSFCell cell0 = row0.createCell(0);
        cell0.setCellValue(title);
        cell0.setCellStyle(style);
        // 第一行
        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = row1.createCell(0);
        //副标题
        cell1.setCellValue(subheading);
        cell1.setCellStyle(style);

        //表头
        HSSFRow row = sheet.createRow(2);

        HSSFCell cell = null;
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(style);
        }

        //数据
        for (int i = 0; i < dataList.size(); i++) {
            row = sheet.createRow(i + 3);
            for (int j = 0; j < dataList.get(i).size(); j++) {
                row.createCell(j).setCellValue(dataList.get(i).get(j));
            }
        }

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }


    /**
     * 导入数据（单页）
     *
     * @param file        文件
     * @param sheetIndex  页名的索引(从0开始，-1代表全部页)
     * @param headerIndex 表头的索引（用于获取共多少列以及第几行开始读数据）
     * @return
     */
    public static List<List<String>> importExcel(MultipartFile file, int sheetIndex, int headerIndex, int rows) throws Exception {
        Workbook workbook = null;
        //返回的data
        List<List<String>> data = new ArrayList<>();
        workbook = getWorkbook(file);
        //导入某一页
        if (sheetIndex != -1 && sheetIndex > -1) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            List<List<String>> lists = importOneSheet(sheet, headerIndex, rows);
            data.addAll(lists);
        } else {
            //导入全部
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                List<List<String>> lists = importOneSheet(sheet, headerIndex, rows);
                data.addAll(lists);
            }
        }
        return data;
    }

    /**
     * 导入数据（单页）
     *
     * @param file        文件
     * @param sheetIndex  页名的索引(从0开始，-1代表全部页)
     * @param headerIndex 表头的索引（用于获取共多少列以及第几行开始读数据）
     * @return
     */
    public static List<List<String>> importExcel(File file, int sheetIndex, int headerIndex, int rows) throws Exception {
        Workbook workbook = null;
        //返回的data
        List<List<String>> data = new ArrayList<>();
        workbook = getWorkbook(file);
        //导入某一页
        if (sheetIndex != -1 && sheetIndex > -1) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            List<List<String>> lists = importOneSheet(sheet, headerIndex, rows);
            data.addAll(lists);
        } else {
            //导入全部
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                List<List<String>> lists = importOneSheet(sheet, headerIndex, rows);
                data.addAll(lists);
            }
        }
        return data;
    }

    /**
     * 导入数据（所有页）
     *
     * @param file        文件
     * @param headerIndex 表头的索引（用于获取共多少列以及第几行开始读数据）
     * @return
     */
    public static List<List<String>> importExcel(MultipartFile file, int headerIndex, int rows) throws Exception {
        return importExcel(file, -1, headerIndex, rows);
    }

    /**
     * 创建一个style
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle getHSSFCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        //居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return style;
    }


    /**
     * 获取一个sheet里的数据
     *
     * @param sheet
     * @param headerIndex
     * @return
     * @throws Exception
     */
    private static List<List<String>> importOneSheet(Sheet sheet, int headerIndex, int rows) throws Exception {
        List<List<String>> data = new ArrayList<>();
        int row = sheet.getLastRowNum();
        //row = -1 表格中没有数据
        //row = headerIndex 表格中表头以下没有数据（指没有有用数据）
        if (row == -1) {
            throw new Exception("表格中没有有用数据!");
        }
        if (headerIndex != -1) {
            //通过表头获取共多少列
            int coloumNum = rows;
            //从表头下一行开始取数据
            for (int i = headerIndex + 1; i <= row; i++) {
                Row row1 = sheet.getRow(i);
                List<String> list = new ArrayList<>();
                if (row1 != null) {
                    for (int j = 0; j < coloumNum; j++) {
                        list.add(getCellValue(row1.getCell(j)));
                    }
                }
                data.add(list);
            }
        } else {
            //通过表头获取共多少列
            int coloumNum = rows;
            //从表头下一行开始取数据
            for (int i = 0; i <= row; i++) {
                Row row1 = sheet.getRow(i);
                List<String> list = new ArrayList<>();
                if (row1 != null) {
                    for (int j = 0; j < row1.getLastCellNum(); j++) {
                        list.add(getCellValue(row1.getCell(j)));
                    }
                }
                data.add(list);
            }
        }
        return data;
    }

    /**
     * 获取workbook
     *
     * @return
     */
    private static Workbook getWorkbook(MultipartFile file) throws Exception {
        Workbook workbook = null;
        //获取文件名
        String fileName = file.getOriginalFilename();
        //判断文件格式
        if (fileName.toUpperCase().endsWith("XLS")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else if (fileName.toUpperCase().endsWith("XLSX")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new Exception("文件格式有误!");
        }
        return workbook;
    }

    /**
     * 获取workbook
     *
     * @return
     */
    private static Workbook getWorkbook(File file) throws Exception {
        Workbook workbook = null;
        //获取文件名
        String fileName = file.getName().toLowerCase();
        //判断文件格式
        if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (fileName.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } else {
            throw new Exception("文件格式有误!");
        }
        return workbook;
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        String cellValue = "";
        DecimalFormat df = new DecimalFormat("#");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getRichStringCellValue().getString().trim();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                try {
                    if (String.valueOf(cell).contains("-")) {
                        cellValue = simpleDateFormat.format(cell.getDateCellValue());
                    } else {
                        cellValue = String.valueOf(cell);
                    }
                } catch (Exception e) {
                    cellValue = df.format(cell.getNumericCellValue()).toString();
                }
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }
        return cellValue.trim();
    }

}
