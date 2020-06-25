package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import yiyuan.JinDie.OriginType;
import yiyuan.utils.msofficetools.ExcelUtil;

public class OriginRepositoryImpl implements OriginDataHelper {
  @Autowired
   MongoOperations mongos;
	@Override
	public List<Origin> findInPeriod(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end));
		
		return mongos.find(query, Origin.class);
	}
	
	@Override
	public double findSalary(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end));
		
		List<Origin> origins=mongos.find(query, Origin.class);
		origins=ExcelUtil.filter(origins, e->e.getBrief().contains("工资"));
		double sum=0;
		for(Origin origin:origins) {
			sum+=origin.getBank_pay();
		}
		
		return sum;
	}
	
	@Override
	public double findPersonSecurity(String companyName,LocalDate begin,LocalDate end) {
	
		
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end).and("type").regex("Accrued_SalaryAndSecurity"));
		
		List<Origin> origins=mongos.find(query, Origin.class);
		double sum=0;
		for(Origin origin:origins) {
			sum+=origin.getPayedPersonSecurity();
		}
		
		return sum;
		
	}

	@Override
	public double findPersonTax(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end).and("brief").regex("\\w*个人所得税\\w*"));
		
		List<Origin> origins=mongos.find(query, Origin.class);
		double sum=0;
		for(Origin origin:origins) {
			sum+=origin.getBank_pay();
		}
		
		return sum;
	}

	@Override
	public double findPersonFund(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		Query query=Query.query(Criteria.where("companyName").is(companyName).and("occur_date").gte(begin).lte(end).and("brief").regex("\\w*公积金\\w*"));
		
		List<Origin> origins=mongos.find(query, Origin.class);
		double sum=0;
		for(Origin origin:origins) {
			sum+=origin.getBank_pay()/2; //一般是单位个人各一半
		}
		
		return sum;
	}
}