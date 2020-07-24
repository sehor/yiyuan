package yiyuan.JinDie.JinDieRecord;

import java.time.LocalDate;
import java.util.List;

import jinDieEntryXLS.beans.Origin;
import jinDieEntryXLS.beans.accountEntry.Record;


public interface JinDieRecordService {

	
	public List<Record> processToRecordsFromDataBase(LocalDate begin,LocalDate end,String companyName);

	public List<Record> processToRecords(List<Origin> origins);
	public List<Record> processToRecordsEechAccountPeriod(LocalDate begin, LocalDate end,List<Origin> origins);
	
	
}