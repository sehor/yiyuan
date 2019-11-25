package yiyuan.bankList;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankListServiceImpl implements BankListService {
    @Autowired
    BankListRepository repository;

    @Override
    public BankList addBankList(BankList bankList) {
        return repository.save(bankList);
    }

    @Override
    public BankList getBankList(Integer id) {
        return repository.findById(id).get();
    }

    @Override
    public BankList updateBankList(BankList bankList) {
        return repository.save(bankList);
    }

    @Override
    public void deleteBankList(BankList bankList) {
        repository.delete(bankList);
    }

    @Override
    public void deleteBankList(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<BankList> getBankListFromXLSFile(File file, int beginSheetIndex, int n) throws IOException {

        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        List<BankList> bankLists = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        for (int m = 0; m < n; m++) {

            XSSFSheet sheet = workbook.getSheetAt(beginSheetIndex+m);
            System.out.println("表名：  "+sheet.getSheetName());
            int beginIndex = 1;
            int rowNum = sheet.getLastRowNum();

            for (; beginIndex < rowNum; beginIndex++) {
                if(sheet.getRow(beginIndex)==null) continue;
                XSSFCell cell = sheet.getRow(beginIndex).getCell(0);
                cell.setCellType(CellType.STRING);
                if (cell.getStringCellValue().trim().equals("记账日")) break;
            }


            for (int i = beginIndex + 1; i < rowNum; i++) {


                BankList bankList = new BankList();
                XSSFRow row = sheet.getRow(i);

                XSSFCell dateCell = row.getCell(1);
                dateCell.setCellType(CellType.STRING);

                if (dateCell.getStringCellValue() == null || dateCell.getStringCellValue().isEmpty()) {
                    continue;
                }


                XSSFCell incomeCell = row.getCell(5);
                incomeCell.setCellType(CellType.STRING);
                if (Float.valueOf(incomeCell.getStringCellValue()) == 0) {
                    continue;
                }

                bankList.setDate(LocalDate.parse(dateCell.getStringCellValue(), dtf));
                bankList.setIncome(Float.valueOf(incomeCell.getStringCellValue()));

                XSSFCell briefCell = row.getCell(3);
                briefCell.setCellType(CellType.STRING);
                bankList.setBrief(briefCell.getStringCellValue());


                XSSFCell relateAccountCell = row.getCell(8);
                relateAccountCell.setCellType(CellType.STRING);
                bankList.setRelateAccount(relateAccountCell.getStringCellValue());


                bankLists.add(bankList);
            }
        }

        workbook.close();
        inputStream.close();

        return bankLists;
    }

    @Override
    public List<BankList> getAll() {
        return repository.findAll();
    }
}