package yiyuan.utils.msofficetools;

import java.time.LocalDate;
import java.util.List;

import yiyuan.JinDie.JinDieRecord.JinDieRecord;
import yiyuan.JinDie.Origin.Origin;

public interface OriginProcess {
	public List<JinDieRecord> proccessToRecord(List<Origin> origins, LocalDate date);
	public void recordWriteToFile(String path,List<JinDieRecord> records);
	public OriginProcess setCompanyName(String companyName);
	public List<Origin> preProcessOrigin(List<Origin> origins);
}
