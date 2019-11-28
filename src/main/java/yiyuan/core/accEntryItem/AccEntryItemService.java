package yiyuan.core.accEntryItem;

import java.util.List;

public interface AccEntryItemService {
    AccEntryItem addAccEntryItem(AccEntryItem accEntryItem);

    AccEntryItem getAccEntryItem(String id);

    AccEntryItem updateAccEntryItem(AccEntryItem accEntryItem);

    void deleteAccEntryItem(AccEntryItem accEntryItem);

    void deleteAccEntryItem(String id);

    List<AccEntryItem> findAll();
}