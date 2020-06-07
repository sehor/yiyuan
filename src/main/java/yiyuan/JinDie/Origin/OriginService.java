package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

public interface OriginService {
	Origin addOrigin(Origin origin);

	Origin getOrigin(String id);

	Origin updateOrigin(Origin origin);

	void deleteOrigin(Origin origin);

	void deleteOrigin(String id);

	List<Origin> getAll();
	
	List<Origin> saveAll(List<Origin> origins);
	
	List<Origin> getInPeriod(String companyName,LocalDate begin,LocalDate end);
	List<Origin> deleteByCompanyName(String companyName);
}