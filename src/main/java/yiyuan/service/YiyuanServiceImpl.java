package yiyuan.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yiyuan.domain.Customer;
import yiyuan.domain.CustomerSaleRecord;
import yiyuan.domain.SaleRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YiyuanServiceImpl implements YiyuanService {
    @Autowired
    CustomerService customerService;
    @Autowired
    SaleRecordService saleRecordService;
    @Autowired
    CustomerSaleRecordService customerSaleRecordService;

    private int itemsRowBeginIndex = 11, itemsColumnBeginIndex;
    private int n;

    @Override
    public void createContractFile(String saleFilePath, String contractFilepath, String saveFilePath, LocalDate date, int sheetIndex) throws IOException {
        int tableBeginRowIndex=11;
        int tableDefautRowNum=11;
        int tableBeginColumnIndex=0;


        List<SaleRecord> saleRecords = saleRecordService.getSaleRecordsFromXLXS(new File(saleFilePath), sheetIndex, date);
        List<CustomerSaleRecord> customerSaleRecords = customerSaleRecordService.getCustomerSaleRecords(saleRecords);


        String dirPath = date.getYear() + "-" + date.getMonthValue();

        File dir = new File(saveFilePath + "\\" + dirPath);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        String fileName = "fileName.xlsx";
        for (CustomerSaleRecord record : customerSaleRecords) {
            FileInputStream inputStreamContract = new FileInputStream(new File(contractFilepath));
            HSSFWorkbook workbookContract = new HSSFWorkbook(inputStreamContract);
            HSSFSheet sheet = workbookContract.getSheetAt(0);
            sheet.getRow(0).getCell(0).setCellValue(record.getCustomer().get名称()); //名称
            String[] addressAndPhone = record.getCustomer().get地址电话().split("\\s+");//拆分地址电话

            sheet.getRow(1).getCell(0).setCellValue(addressAndPhone.length > 0 ? addressAndPhone[0] : "");//地址
            sheet.getRow(6).getCell(6).setCellValue(addressAndPhone.length > 1 ? addressAndPhone[1] : "");//电话

            //不够行数，插入空行

            int extraRow = record.getSaleRecords().size() - tableDefautRowNum;
            if (extraRow > 0) {
                HSSFRow sourceRow=sheet.getRow(tableBeginRowIndex);
                sheet.getRow(tableBeginRowIndex+tableDefautRowNum-1).getCell(0).setCellValue("");
                sheet.shiftRows(tableBeginRowIndex+tableDefautRowNum-1,
                        sheet.getLastRowNum(), extraRow, true, true);

                for (int i = 0; i < extraRow; i++) {

                    HSSFRow newRow = sheet.createRow(tableBeginRowIndex+tableDefautRowNum -1+ i);
                    newRow.setHeight(sourceRow.getHeight());
                    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                       HSSFCell cell= newRow.createCell(j);
                       cell.setCellStyle(sourceRow.getCell(j).getCellStyle());
                    }
                }

            }
            int signatureIndex=extraRow>0?(38+extraRow):38;
            sheet.getRow(signatureIndex).getCell(5).setCellValue("购    方：" + record.getCustomer().get名称());//表尾名称

            //填表格
            int i = 0;
            for (; i < record.getSaleRecords().size(); i++) {
                HSSFRow row = sheet.getRow(tableBeginRowIndex + i);
                row.getCell(2).setCellValue(record.getSaleRecords().get(i).get品名());
                row.getCell(3).setCellValue(record.getSaleRecords().get(i).get规格());
                row.getCell(4).setCellValue("个");
                row.getCell(5).setCellValue(record.getSaleRecords().get(i).get数量());
                row.getCell(6).setCellValue(record.getSaleRecords().get(i).get单价());
                row.getCell(7).setCellValue(record.getSaleRecords().get(i).get含税金额());
                row.getCell(8).setCellValue(record.getSaleRecords().get(i).getDate().toString());
                row.getCell(0).setCellValue(i+1);
            }
            if (i < 8) {
                sheet.getRow(tableBeginRowIndex + i).getCell(4).setCellValue("以下空白");
            }

            fileName = record.getCustomer().get名称() + "-合同" + "-" + date.getYear() + "-" + date.getMonthValue() + ".xls";
            String filePath = dirPath + "\\" + fileName;
            System.out.println(filePath);
            sheet.setForceFormulaRecalculation(true);

            File xlsFile = new File(dir, fileName);
            if (!xlsFile.exists()) {
                xlsFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            workbookContract.write(outputStream);
            workbookContract.close(); //关闭文件
            outputStream.close();  //关闭流
            inputStreamContract.close();//关闭输入流
        }

    }

 /*   private Map<String,String> customerNameMap(){
        Map<String,String> map= new HashMap<>();
        List<Customer> customers=customerService.getAllCustomers(new File("D:\\temp\\客户开票信息表.xlsx"));
        return map;
    }*/


    private Customer getCustomerByName(String name) {

        List<Customer> customers = customerService.getAllCustomers(new File("D:\\temp\\客户开票信息表.xlsx"));
        for (Customer customer : customers) {
            if (customer.get名称().contains(name)) {
                return customer;
            }
        }
        return null;
    }
}
