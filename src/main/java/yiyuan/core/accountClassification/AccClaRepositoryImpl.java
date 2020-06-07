package yiyuan.core.accountClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


public class AccClaRepositoryImpl implements AccClaDataHelper {
    @Autowired
    MongoOperations operations;

    @Override
    public void updateById(AccCla accCla,String id) {
        Query query=new Query(Criteria.where("id").is(id));
        List<AccCla> accClas=operations.find(query,AccCla.class);
        Update update=new Update();
        update.set("nameZh",accCla.getNameZh());
        AccCla result=operations.findAndModify(query,update,AccCla.class);
        System.out.println(accClas.get(0).getNameZh());
        assert result != null;
        System.out.println(result.toString());

    }
}