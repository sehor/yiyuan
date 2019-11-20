package yiyuan.service;

import org.springframework.stereotype.Service;
import yiyuan.domain.SaleRecord;

import java.io.File;

import java.time.LocalDate;
import java.util.List;
@Service
public interface SaleRecordService {
    List<SaleRecord> getSaleRecordsFromXLXS(File file,int sheetIndex);
    List<SaleRecord> getSaleRecordsFromXLXS(File file, int sheetIndex , LocalDate date);
    List<SaleRecord> getSaleRecordsFromWorkbook(File file,int begin,int end,int beginMoth);
}
