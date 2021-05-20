package yiyuan.JinDie.JinDieRecord;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jinDieEntryXLS.JinDieEntryXLS;
import jinDieEntryXLS.beans.RawInfo;
import jinDieEntryXLS.beans.accountEntry.Record;
import yiyuan.JinDie.Origin.OriginService;

@Service
public class JinDieRecordServiceImpl implements JinDieRecordService {

	@Autowired
	OriginService originService;
	@Autowired 
	JinDieEntryXLS jinDie;

	@Override
	public List<Record> processToRecordsFromDataBase(LocalDate begin, LocalDate end, String companyName) {

		return processToRecordsEechAccountPeriod(begin, end,
				originService.originToSupClass(originService.getInPeriod(companyName, begin, end)));
	}
	
	

	@Override
	public List<Record> processToRecords(List<RawInfo> origins) {

		
		List<Record> records = jinDie.produceRecords(origins);
		return records;
	}

	@Override
	public List<Record> processToRecordsEechAccountPeriod(LocalDate begin, LocalDate end,
			List<RawInfo> origins) {

		return processInEachPeriod(begin, end, origins,li->{
			return jinDie.produceRecords(li);
		});
	}



	@Override
	public List<Record> processToCostRecord(LocalDate begin, LocalDate end,String companyName,double rate) {
		// TODO Auto-generated method stub
		List<RawInfo> origins=originService.originToSupClass(originService.getInPeriod(companyName, begin, end));
		return processInEachPeriod(begin, end, origins,li->{
			return jinDie.produceToCostRecord(li,rate);
		});
	}

	
	
	
	//在每个会计期间运行 function.apply:   origins -> records;
	private List<Record> processInEachPeriod(LocalDate begin, LocalDate end,
			List<jinDieEntryXLS.beans.RawInfo> origins,Function<List<RawInfo>, List<Record>> function){
		
		List<jinDieEntryXLS.beans.RawInfo> periodOrigins = new ArrayList<>();
		List<Record> records = new ArrayList<>();
		LocalDate currentEnd = begin.with(TemporalAdjusters.lastDayOfMonth()); // 当前会计期间最后一天
		LocalDate currentBegin = LocalDate.of(begin.getYear(), begin.getMonth(), 1);// 当前会计期间第一天

		while (!currentBegin.isAfter(end)) {
			for (jinDieEntryXLS.beans.RawInfo origin : origins) {
				if (!origin.getOccur_date().isBefore(currentBegin) && !origin.getOccur_date().isAfter(currentEnd)) {
					periodOrigins.add(origin);
				}
			}

			records.addAll(function.apply(periodOrigins)); // 同一会计期间的
			periodOrigins.clear();

			currentBegin = currentBegin.plusMonths(1);
			currentEnd = currentBegin.with(TemporalAdjusters.lastDayOfMonth());

		}
		return records;
	}



	@Override
	public List<Record> processToRecordsFromDataBase(LocalDate begin, LocalDate end, String companyName,
			String typeString) {
		
		return processToRecordsEechAccountPeriod(begin, end,
				originService.originToSupClass(originService.getInPeriod(companyName, begin, end,typeString)));
	}
	
	
	
	
	
	
}