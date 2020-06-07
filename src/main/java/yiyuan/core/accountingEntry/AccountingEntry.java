package yiyuan.core.accountingEntry;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document
public class
AccountingEntry {

    private String id;
    private String creator;
    private String audit;
    private String closing;
    private LocalDate date;

    private List<String> entryItemsId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getEntryItemsId() {
        return entryItemsId;
    }

    public void setEntryItemsId(List<String> entryItemsId) {
        this.entryItemsId = entryItemsId;
    }
}
