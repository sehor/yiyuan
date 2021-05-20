package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

public interface OriginDataHelper {
	List<Origin> findInPeriod(String companyName,LocalDate begin,LocalDate end);
	List<Origin> findInPeriod(String companyName,LocalDate begin,LocalDate end,String typeString);
	public double findSalary(String companyName, LocalDate begin, LocalDate end);
	public double findPersonSecurity(String companyName,LocalDate begin,LocalDate end);
	public double findPersonTax(String companyName, LocalDate begin, LocalDate end);
	public double findPersonFund(String companyName, LocalDate begin, LocalDate end);
}