package yiyuan.core.accountingEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yiyuan.core.accEntryItem.AccEntryItem;
import yiyuan.core.accEntryItem.AccEntryItemRepository;
import yiyuan.core.accountClassification.AccCla;

import java.util.List;
@Service
public class AccountingEntryServiceImpl implements AccountingEntryService {
    @Autowired
    AccountingEntryRepository repository;
    @Autowired
    AccEntryItemRepository itemRepository;

    @Override
    public AccountingEntry add(AccountingEntry accountingEntry) {

        return  repository.save(accountingEntry);

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

    @Override
    public void delete(AccountingEntry accountingEntry) {
        List<AccEntryItem> items=itemRepository.findByEntryId(accountingEntry.getId());
        itemRepository.deleteAll(items);
        repository.delete(accountingEntry);
    }

    @Override
    public AccountingEntry saveShowEntry(ShowEntry showEntry) {

        AccountingEntry entry=showEntry.getEntry();
        if(entry.getId()==null){   //新的分录，新增分录
             AccountingEntry entrySave=repository.save(entry);
             showEntry.getEntryItems().forEach(item->item.setEntryId(entrySave.getId())); // 记录获得entryId
             List<AccEntryItem> items=itemRepository.saveAll(showEntry.getEntryItems()); //保存记录
             items.forEach(item-> entrySave.getEntryItemsId().add(item.getId()));//把记录id保存到新的分录
             return  repository.save(entrySave);//再次保存分录
        }else {//更新分录
            //查出要删除的entryItems,
            itemRepository.findByEntryId(entry.getId()).forEach(item->{
                if(!showEntry.getEntryItems().contains(item)) itemRepository.delete(item);
            });
            List<AccEntryItem> items=itemRepository.saveAll(showEntry.getEntryItems());
            entry.getEntryItemsId().clear(); //清空旧的记录id
            items.forEach(item-> entry.getEntryItemsId().add(item.getId()));//把新记录id保存到新的分录
            return repository.save(entry);
        }

    }

}
