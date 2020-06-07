package yiyuan.core.accEntryItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yiyuan.core.accountingEntry.AccountingEntry;
import yiyuan.core.accountingEntry.AccountingEntryRepository;

import java.util.List;

@Service
public class AccEntryItemServiceImpl implements AccEntryItemService {
    @Autowired
    AccEntryItemRepository repository;
    @Autowired
    AccountingEntryRepository accountingEntryRepository;

    @Override
    public AccEntryItem addAccEntryItem(AccEntryItem accEntryItem) {
        AccountingEntry entry=accountingEntryRepository.findById(accEntryItem.getEntryId()).orElse(null);
        AccEntryItem accEntryItem1=repository.save(accEntryItem);
        assert entry != null;
        entry.getEntryItemsId().add(accEntryItem1.getId());
        accountingEntryRepository.save(entry);
        return accEntryItem1;
    }

    //保存在同一分录下的一批记录
    @Override
    public List<AccEntryItem> addSomeInSameEntry(List<AccEntryItem> accEntryItems) {
        String entryId=accEntryItems.get(0).getEntryId();
        AccountingEntry entry=accountingEntryRepository.findById(entryId).orElse(null);
        assert entry!=null;
        List<AccEntryItem> results=repository.saveAll(accEntryItems);
        results.forEach(item->{  //把记录id都保存到所属分录下
            entry.getEntryItemsId().add(item.getId());
        });
        accountingEntryRepository.save(entry);

        return results;
    }

    @Override
    public AccEntryItem getAccEntryItem(String id) {
        return repository.findById(id).get();
    }

    @Override
    public AccEntryItem updateAccEntryItem(AccEntryItem accEntryItem) {
        return repository.save(accEntryItem);
    }

    @Override
    public void deleteAccEntryItem(AccEntryItem accEntryItem) {
        repository.delete(accEntryItem);
    }

    @Override
    public void deleteAccEntryItem(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<AccEntryItem> findAll() {
        return repository.findAll();
    }
}