package yiyuan.service;

import java.io.IOException;
import java.time.LocalDate;

public interface YiyuanService {
    public void createContractFile(String saleFilePath, String contractFilepath, String saveFilePath, LocalDate date,int sheetIndex) throws IOException;

}
