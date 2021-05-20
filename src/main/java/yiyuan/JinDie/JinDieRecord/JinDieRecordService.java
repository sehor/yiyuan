package yiyuan.JinDie.JinDieRecord;

import java.time.LocalDate;
import java.util.List;

import jinDieEntryXLS.beans.RawInfo;
import jinDieEntryXLS.beans.accountEntry.Record;


public interface JinDieRecordService {

	
	public List<Record> processToRecordsFromDataBase(LocalDate begin,LocalDate end,String companyName);
	public List<Record> processToRecordsFromDataBase(LocalDate begin,LocalDate end,String companyName,String typeString);

	public List<Record> processToRecords(List<RawInfo> origins);
	public List<Record> processToRecordsEechAccountPeriod(LocalDate begin, LocalDate end,List<RawInfo> origins);
	public List<Record> processToCostRecord(LocalDate begin, LocalDate end,String companyName,double rate);
	
	
}