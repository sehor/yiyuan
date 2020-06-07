package yiyuan.core.accEntryItem;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccEntryItemRepository extends MongoRepository<AccEntryItem, String>, AccEntryItemDataHelper {
    List<AccEntryItem> findByEntryId(String entryId);
}