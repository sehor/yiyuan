package yiyuan.core.accountClassification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccClaRepository extends MongoRepository<AccCla, String>, AccClaDataHelper {
    List<AccCla> findByParentId(String parentId);
}