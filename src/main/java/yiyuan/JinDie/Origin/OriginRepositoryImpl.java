package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OriginRepositoryImpl implements OriginDataHelper {
  @Autowired
   MongoOperations mongos;
	@Override
	public List<Origin> findInPeriod(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end));
		
		return mongos.find(query, Origin.class);
	}
}