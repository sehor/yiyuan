package yiyuan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;

import yiyuan.other.FileStructure;
import yiyuan.other.VTACllector;
import yiyuan.other.Visitor;
import yiyuan.utils.AccountPeriod;

public class NoSpringTest {

	//@Test
	public void test1() {

		AccountPeriod accountPeriod = new AccountPeriod("2019-04-2");
		AccountPeriod accountPeriod1 = new AccountPeriod("2019-07-2");
		LocalDate currentBegin = accountPeriod.getBeginDay();
		LocalDate currentEnd;

		/*
		 * System.out.println(accountPeriod.getBeginDay().isBefore(accountPeriod1.
		 * getBeginDay()));
		 * System.out.println(accountPeriod.getLastDay().getDayOfMonth());
		 * System.out.println(accountPeriod.getBeginDay().getDayOfMonth());
		 * 
		 * 
		 */
		for (int i = 1; i <= 12; i++) {
			/*
			 * currentBegin = currentBegin.plusMonths(1); currentEnd =
			 * currentBegin.with(TemporalAdjusters.lastDayOfMonth());
			 * System.out.println(currentBegin); System.out.println(currentEnd);
			 */
		}
		
      Double a=33018.87,b=231320.64;
      float f=264339.51f;
      BigDecimal _a=new BigDecimal(String.valueOf(a));
      BigDecimal _b=new BigDecimal(String.valueOf(b));
      System.out.println(_a+" "+_b);
      System.out.println(a+b);
      System.out.println(Double.valueOf(f));

	}

}
