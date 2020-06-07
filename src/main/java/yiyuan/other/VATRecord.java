package yiyuan.other;

import yiyuan.utils.msofficetools.ExcelUtil;

public class VATRecord {

	private String date;
	private double 应交税款;
	private double 累计应交税款;
	private double 累计进项税额;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double get应交税款() {
		return 应交税款;
	}
	public void set应交税款(double 应交税款) {
		this.应交税款 = ExcelUtil.set2Dig(应交税款);;
	}
	public double get累计应交税款() {
		return 累计应交税款;
	}
	public void set累计应交税款(double 累计应交税款) {
		this.累计应交税款 = ExcelUtil.set2Dig(累计应交税款);
	}
	public double get累计进项税额() {
		return 累计进项税额;
	}
	public void set累计进项税额(double 累计进项税额) {
		this.累计进项税额 = ExcelUtil.set2Dig(累计进项税额);
	}

	
}
