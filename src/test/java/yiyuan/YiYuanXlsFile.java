package yiyuan;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import yiyuan.bankList.BankList;
import yiyuan.bankList.BankListService;
import yiyuan.core.accountClassification.AccCla;


import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YiYuanXlsFile {
    @Autowired
    BankListService bankListService;

    //@Test
    public void test() throws IOException {

    }

    //@Test
    public void dateTest() {
        LocalDate d = LocalDate.parse("2019-01-01");
        LocalTime t = LocalTime.now();
        LocalDateTime dt = LocalDateTime.now();
        ZonedDateTime zdt = dt.atZone(ZoneId.of("America/New_York"));
        System.out.println(d);
        System.out.println(t);
        System.out.println(dt);
        System.out.println(zdt);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dt1 = LocalDateTime.parse("2019/12/31 10:50:32", dtf);
        System.out.println(dt1);
        System.out.println(d.plusMonths(1));
        System.out.println(d.withYear(2020));

    }

    @Test
    public void xlsTest() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\temp\\收支1.xls"));
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\temp\\收支2.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        List<BankList> bankLists = bankListService.getAll();
        insertBankList(workbook, bankLists);
        workbook.write(outputStream);
        workbook.close();
        inputStream.close();
        outputStream.close();
    }

    private void insertBankList(HSSFWorkbook workbook, List<BankList> bankLists) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow sourceRow = sheet.getRow(1);
        int course = 4;
        int year = 2029, moth = 12, day = 31;
        for (BankList bankList : bankLists) {
            LocalDate date = bankList.getDate();
            for (int i = course; i <= sheet.getLastRowNum() + 1; i++) {
                HSSFRow row = sheet.getRow(i);
                if (row.getCell(1) != null && row.getCell(1).getCellType() != CellType.BLANK) { //未超出有数据列
                    year = (int) row.getCell(1).getNumericCellValue();
                    moth = (int) row.getCell(2).getNumericCellValue();
                    day = (int) row.getCell(3).getNumericCellValue();
                    if (date.compareTo(LocalDate.of(year, moth, day)) <= 0) {
                        HSSFRow newRow = insertRow(i, sheet);
                        copyRow(sourceRow, newRow);
                        setRow(bankList, newRow);
                        course = i + 1;
                        break;
                    }
                } else {
                    HSSFRow newRow1 = sheet.createRow(i);
                    copyRow(sourceRow, newRow1);
                    setRow(bankList, newRow1);
                    course = i + 1;
                    break;
                }
            }


        }
    }

    private void setRow(BankList bankList, HSSFRow row) {
        System.out.println("row index is:" + row.getRowNum());
        row.getCell(0).setCellValue("（入账）" + bankList.getDate().toString());
        row.getCell(1).setCellValue(bankList.getDate().getYear());
        row.getCell(2).setCellValue(bankList.getDate().getMonthValue());
        row.getCell(3).setCellValue(bankList.getDate().getDayOfMonth());
        row.getCell(5).setCellValue(bankList.getBrief() + "(" + bankList.getRelateAccount() + ")");
        row.getCell(10).setCellValue(bankList.getIncome());
    }


    private HSSFRow insertRow(int index, HSSFSheet sheet) {
        HSSFRow row = null;
        sheet.shiftRows(index, sheet.getLastRowNum(), 1);
        row = sheet.createRow(index);
        return row;
    }

    private void copyRow(HSSFRow sourceRow, HSSFRow targetRow) {
/*
        String formula_建行私账="L5-J6+K6";
        String formula_工行="I5-G6+H6";
        String formula_建行公帐="O5-M6+N6";*/

        targetRow.setHeight(sourceRow.getHeight());  //复制高度

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            HSSFCell cell = sourceRow.getCell(i);
            if (cell == null) continue;  //无效单元格，跳过
            HSSFCell targetCell = targetRow.createCell(i);

            //如果单元格类型是公式，必须复制和重设公式（默认用当前行索引替代公式的数字）
 /*           if(cell.getCellType()==CellType.FORMULA){

                targetCell.setCellFormula(cell.getCellFormula().replaceAll("\\d+",String.valueOf( targetRow.getRowNum())));
            }
            //复制类型
            else {
                targetCell.setCellType(cell.getCellType());
            }*/

            //复制格式
            targetCell.setCellStyle(cell.getCellStyle());
        }
    }

    @Test
    public void readAccountSubjects() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\temp\\会计科目表.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        List<AccCla> accClas = new ArrayList<>();
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) return;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) return;

            AccCla accCla = new AccCla();
            accCla.setId(row.getCell(1).getStringCellValue());
            accCla.setNameZh(row.getCell(2).getStringCellValue());
            accCla.getCategories().put("categoryInBalance", "assert");
            accCla.setLevel("supreme");
            if (row.getCell(3).getCellType() != CellType.BLANK) {
                accCla.setForIndustry(row.getCell(3).getStringCellValue());
            }

            AccCla accCla1 = new AccCla();
            accCla1.setId(row.getCell(5).getStringCellValue());
            accCla1.setNameZh(row.getCell(6).getStringCellValue());
            accCla1.getCategories().put("categoryInBalance", "assert");
            accCla1.setLevel("supreme");
            if (row.getCell(7).getCellType() != CellType.BLANK) {
                accCla1.setForIndustry(row.getCell(7).getStringCellValue());
            }


        }

    }
}
