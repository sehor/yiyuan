package yiyuan.bankList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface BankListService {
    BankList addBankList(BankList bankList);

    BankList getBankList(Integer id);

    BankList updateBankList(BankList bankList);

    void deleteBankList(BankList bankList);

    void deleteBankList(Integer id);

    List<BankList> getBankListFromXLSFile(File file,int beginSheetIndex,int n) throws IOException;

    List<BankList> getAll();
}