package yiyuan.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class AccountPeriod {

	private LocalDate date;

	public AccountPeriod(int year, int month) {

		this.date = LocalDate.of(year, month, 1);
	}

	public AccountPeriod(String preriod) {
		try {
			this.date = LocalDate.parse(preriod.substring(0, "yyyyMM".length())+"01", DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (Exception e) {
			try {
				this.date = LocalDate.parse(preriod.substring(0, "yyyy-MM".length())+"-01",
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (Exception e1) {
				try {
					this.date = LocalDate.parse(preriod.substring(0, "yyyy-M".length())+"-1",
							DateTimeFormatter.ofPattern("yyyy-M-d"));
				} catch (Exception e2) {
                    System.out.println("日期格式错！ please 输入 如 202001或 2020-01或2020-1的格式");
				}

			}

		}

	}

	public LocalDate getBeginDay() {

		return this.date;
	}

	public LocalDate getLastDay() {

		return this.date.with(TemporalAdjusters.lastDayOfMonth());
	}
}
