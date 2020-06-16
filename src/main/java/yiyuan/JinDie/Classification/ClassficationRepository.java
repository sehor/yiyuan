package yiyuan.JinDie.Classification;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassficationRepository extends MongoRepository<Classfication, String>, ClassficationDataHelper {
	List<Classfication> findBy名称(String 名称);
	List<Classfication> findBy名称AndCompanyName(String 名称,String companyName);
	List<Classfication> findByCompanyName(String companyName);
    List<Classfication> findBy编码(String num);
    List<Classfication> findByMutilName(String mutilName);
}