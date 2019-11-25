package yiyuan.accountingEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountingEntryServiceImpl implements AccountingEntryService {
    @Autowired
    AccountingEntryRepository repository;
    @Override
    public AccountingEntry add(AccountingEntry accountingEntry) {

        return  repository.insert(accountingEntry);

    }

    @Override
    public List<AccountingEntry> findAll() {

        return repository.findAll();
    }

    @Override
    public void update(AccountingEntry accountingEntry) {
           repository.save(accountingEntry);
    }

    @Override
    public AccountingEntry findOne(String id) {
        return repository.findById(id).orElse(null);
    }
}
