package yiyuan.core.accountingEntry;

import java.util.List;


public interface AccountingEntryService {

    public AccountingEntry add(AccountingEntry accountingEntry);
    public List<AccountingEntry> findAll();
    public void update(AccountingEntry accountingEntry);
    public AccountingEntry findOne(String id);
    public void delete(AccountingEntry accountingEntry);
    public AccountingEntry saveShowEntry(ShowEntry showEntry);
}
