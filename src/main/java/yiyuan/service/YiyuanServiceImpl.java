package yiyuan.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yiyuan.domain.Customer;
import yiyuan.domain.SaleRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private int itemsRowBeginIndex=11,itemsColumnBeginIndex;
    @Override
    public void createContractFile(String saleFilePath, String contractFilepath, String saveFilePath) throws IOException {

        FileInputStream inputStreamContract=new FileInputStream(new File(contractFilepath));
        XSSFWorkbook workbookContract=new XSSFWorkbook(inputStreamContract);
        XSSFSheet sheet=workbookContract.getSheetAt(0);

        LocalDate date=LocalDate.parse("2019-01-29");
        List<SaleRecord> saleRecords=saleRecordService.getSaleRecordsFromXLXS(new File(saleFilePath),32,date);

        String fileName="fileName.xlsx";
        for(int i=0;i<saleRecords.size();i++){

              if(i>0&&!saleRecords.get(i).get公司名称().equals(saleRecords.get(i-1).get公司名称())){
                   Customer customer=getCustomerByName(saleRecords.get(i-1).get公司名称());
                   sheet.getRow(0).getCell(0).setCellValue(customer.get名称());

                   String[] addressAndPhone=customer.get地址电话().split("\\s+");

                   sheet.getRow(1).getCell(0).setCellValue(addressAndPhone.length>0?addressAndPhone[0]:"");
                   sheet.getRow(6).getCell(6).setCellValue(addressAndPhone.length>1?addressAndPhone[1]:"");
                   sheet.getRow(38).getCell(5).setCellValue(customer.get名称());

                    fileName=customer.get名称()+date.toString()+"合同"+".xlsx";
                    File xlsxFile=new File(saleFilePath+"\\"+fileName);
                    if(!xlsxFile.exists()){
                        xlsxFile.createNewFile();
                    }
                   FileInputStream inputStream=new FileInputStream(xlsxFile);


              }
        }
    }

 /*   private Map<String,String> customerNameMap(){
        Map<String,String> map= new HashMap<>();
        List<Customer> customers=customerService.getAllCustomers(new File("D:\\temp\\客户开票信息表.xlsx"));
        return map;
    }*/


    private Customer getCustomerByName(String name){

        List<Customer> customers=customerService.getAllCustomers(new File("D:\\temp\\客户开票信息表.xlsx"));
        for(Customer customer:customers){
            if(customer.get名称().contains(name)){
                return customer;
            }
        }
        return null;
    }
}
