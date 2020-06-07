package yiyuan.core.accountingEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

public class AccountingEntryRepositoryImpl implements AccountingEntryDataHelper {

    @Autowired
    MongoOperations mongo;


}
