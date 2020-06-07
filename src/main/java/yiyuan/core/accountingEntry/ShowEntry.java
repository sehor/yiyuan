package yiyuan.core.accountingEntry;

import yiyuan.core.accEntryItem.AccEntryItem;

import java.util.ArrayList;
import java.util.List;

public class ShowEntry {

    private AccountingEntry entry;
    private List<AccEntryItem> entryItems=new ArrayList<>();

    public AccountingEntry getEntry() {
        return entry;
    }

    public void setEntry(AccountingEntry entry) {
        this.entry = entry;
    }

    public List<AccEntryItem> getEntryItems() {
        return entryItems;
    }

    public void setEntryItems(List<AccEntryItem> entryItems) {
        this.entryItems = entryItems;
    }
}
