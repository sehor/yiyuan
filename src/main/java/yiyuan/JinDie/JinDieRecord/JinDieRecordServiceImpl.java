package yiyuan.JinDie.JinDieRecord;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jinDieEntryXLS.JinDieEntryXLS;
import jinDieEntryXLS.beans.accountEntry.Record;
import jinDieEntryXLS.service.data.DataService;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;

@Service
public class JinDieRecordServiceImpl implements JinDieRecordService {

	@Autowired
	OriginService originService;
	@Autowired
	DataService dataService;

	@Override
	public List<Record> processToRecordsFromDataBase(LocalDate begin, LocalDate end, String companyName) {

		return processToRecordsEechAccountPeriod(begin, end,
				originService.originToSubcalss(originService.getInPeriod(companyName, begin, end)));
	}
	
	

	@Override
	public List<Record> processToRecords(List<jinDieEntryXLS.beans.Origin> origins) {

		JinDieEntryXLS jinDie = new JinDieEntryXLS(dataService);
		List<Record> records = jinDie.produceRecords(origins);
		return records;
	}

	@Override
	public List<Record> processToRecordsEechAccountPeriod(LocalDate begin, LocalDate end,
			List<jinDieEntryXLS.beans.Origin> origins) {
		List<jinDieEntryXLS.beans.Origin> periodOrigins = new ArrayList<>();
		List<Record> records = new ArrayList<>();
		LocalDate currentEnd = begin.with(TemporalAdjusters.lastDayOfMonth()); // 当前会计期间最后一天
		LocalDate currentBegin = LocalDate.of(begin.getYear(), begin.getMonth(), 1);// 当前会计期间第一天

		while (!currentBegin.isAfter(end)) {
			for (jinDieEntryXLS.beans.Origin origin : origins) {
				if (!origin.getOccur_date().isBefore(currentBegin) && !origin.getOccur_date().isAfter(currentEnd)) {
					periodOrigins.add(origin);
				}
			}

			records.addAll(processToRecords(periodOrigins)); // 同一会计期间的
			periodOrigins.clear();

			currentBegin = currentBegin.plusMonths(1);
			currentEnd = currentBegin.with(TemporalAdjusters.lastDayOfMonth());

		}
		return records;
	}

}