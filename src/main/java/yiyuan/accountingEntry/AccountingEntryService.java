package yiyuan.accountingEntry;

import org.springframework.stereotype.Service;

import java.util.List;


public interface AccountingEntryService {

    public AccountingEntry add(AccountingEntry accountingEntry);
    public List<AccountingEntry> findAll();
    public void update(AccountingEntry accountingEntry);
    public AccountingEntry findOne(String id);
}
