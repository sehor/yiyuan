package yiyuan.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface YiyuanService {
    public void createContractFile(String saleFilePath,String contractFilepath,String saveFilePath) throws IOException;

}
