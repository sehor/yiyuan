package yiyuan.JinDie.JinDieRecord;

import java.time.LocalDate;
import java.util.List;

import yiyuan.JinDie.Origin.Origin;

public interface JinDieRecordService {
	JinDieRecord addJinDieRecord(JinDieRecord jinDieRecord);

	JinDieRecord getJinDieRecord(String id);

	JinDieRecord updateJinDieRecord(JinDieRecord jinDieRecord);

	void deleteJinDieRecord(JinDieRecord jinDieRecord);

	void deleteJinDieRecord(String id);
	
	public List<JinDieRecord> processToRecords(LocalDate begin,LocalDate end,String companyName);
	public List<JinDieRecord> processToRecords(LocalDate begin, LocalDate end,String companyName,String type);
}