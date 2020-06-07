package yiyuan.JinDie.Origin;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginRepository extends MongoRepository<Origin, String>, OriginDataHelper {

	List<Origin> deleteByCompanyName(String companyName);
	List<Origin> findByCompanyNameAndType(String companyName,String type);
}