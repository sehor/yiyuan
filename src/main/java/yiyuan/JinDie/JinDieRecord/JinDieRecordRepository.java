package yiyuan.JinDie.JinDieRecord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JinDieRecordRepository extends MongoRepository<JinDieRecord, String>, JinDieRecordDataHelper {
}