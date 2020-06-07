package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

public interface OriginDataHelper {
	List<Origin> findInPeriod(String companyName,LocalDate begin,LocalDate end);
}