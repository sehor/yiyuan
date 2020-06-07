package yiyuan.core.accountingEntry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yiyuan.core.accEntryItem.AccEntryItem;

import java.util.List;

@Repository
public interface AccountingEntryRepository extends MongoRepository<AccountingEntry, String>, AccountingEntryDataHelper {

}
