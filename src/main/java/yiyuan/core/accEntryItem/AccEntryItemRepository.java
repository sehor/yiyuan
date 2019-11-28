package yiyuan.core.accEntryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AccEntryItemRepository extends MongoRepository<AccEntryItem,String>,AccEntryItemDataHelper {
}