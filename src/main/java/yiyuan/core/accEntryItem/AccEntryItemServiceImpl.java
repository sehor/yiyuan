package yiyuan.core.accEntryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccEntryItemServiceImpl implements AccEntryItemService {
@Autowired
AccEntryItemRepository repository;
     @Override
    public AccEntryItem addAccEntryItem(AccEntryItem accEntryItem) {
        return repository.save(accEntryItem);
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