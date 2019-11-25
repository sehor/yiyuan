package yiyuan.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import yiyuan.domain.SaleRecord;
import yiyuan.utils.msofficetools.DefaultXLSToBeanTransform;
import yiyuan.utils.msofficetools.XLSToBeanTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public class SaleRecordServiceImpl implements SaleRecordService{
    @Override
    public List<SaleRecord> getSaleRecordsFromXLXS(File file,int sheetIndex) {
         List<SaleRecord> saleRecords=new ArrayList<>();
         FileInputStream inputStream=null;
         XSSFWorkbook workbook=null;
        try {
            inputStream=new FileInputStream(file);
            workbook=new XSSFWorkbook(inputStream);
            XLSToBeanTransform<SaleRecord> xf=new DefaultXLSToBeanTransform<>(new SaleRecord(),workbook);
            saleRecords=xf.creatBeans(sheetIndex);
            if(saleRecords.size()<=0) return saleRecords;

            for(int i=0;i<saleRecords.size();i++){
                if(saleRecords.get(i).get公司名称().equals("")&&i>0){
                    saleRecords.get(i).set公司名称(saleRecords.get(i-1).get公司名称());
                };
                if(saleRecords.get(i).get规格().equals("")){
                    saleRecords.get(i).set规格(saleRecords.get(i-1).get规格());
                }
            }
            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();

        }finally {

            try {
                workbook.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return saleRecords;
    }

    @Override
    public List<SaleRecord> getSaleRecordsFromXLXS(File file, int sheetIndex, LocalDate date) {
        List<SaleRecord> saleRecords=getSaleRecordsFromXLXS(file,sheetIndex);
        saleRecords.forEach(r-> r.setDate(date));
        return saleRecords;
    }

    @Override
    public List<SaleRecord> getSaleRecordsFromWorkbook(File file, int begin, int end, int beginMoth) {
        return null;
    }
}
