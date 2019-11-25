package yiyuan.accountingEntry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingEntryRepository extends MongoRepository <AccountingEntry,String>,AccountingEntryDataHelper{
}
