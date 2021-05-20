package yiyuan.JinDie.Origin;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import jinDieEntryXLS.beans.RawInfo;

public interface OriginService {
	@CacheEvict(value = "origin", allEntries = true)
	Origin addOrigin(Origin origin);

	@Cacheable("origin")
	Origin getOrigin(String id);

	@CacheEvict(value = "origin", allEntries = true)
	Origin updateOrigin(Origin origin);

	@CacheEvict(value = "origin", allEntries = true)
	void deleteOrigin(Origin origin);

	@CacheEvict(value = "origin", allEntries = true)
	void deleteOrigin(String id);

	//@Cacheable("origin")
	List<Origin> getAll();

	@CacheEvict(value = "origin", allEntries = true)
	List<Origin> saveAll(List<Origin> origins);

	//@Cacheable(value="origin")
	List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end);
	
	//@Cacheable(value="origin")
	List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end,String typeString);

	@CacheEvict(value = "origin", allEntries = true)
	List<Origin> deleteByCompanyName(String companyName);
	
    double findSalary(String companyName, LocalDate begin, LocalDate end);
	
	double findPersonSecurity(String companyName, LocalDate begin, LocalDate end);
	
	double findPersonTax(String companyName, LocalDate begin, LocalDate end);
	
	double findPersonFund(String companyName, LocalDate begin, LocalDate end); //个人公积金
	
	List<Origin> getFromFile(File file,String companyName);
	List<Origin> getFromFilePath(String filePath,String companyName);
	
	List<RawInfo> originToSupClass(List<Origin> origins);
	
	List<RawInfo> getExpenseOriginFromFile(File file,String companyName);
	
}