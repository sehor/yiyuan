
package yiyuan.JinDie;

public enum OriginType {

	BanK(100, "Bank"), Bank_Income_Receivable(102, "Bank_Income_Receivable"),
	Bank_Income_OtherReceivable(103, "Bank_Income_OtherReceivable"),

	Bank_Income_OtherPayable(104, "Bank_Income_OtherPayable"), Bank_Income_Interest(105, "Bank_Income_Interest"),
	Bank_Income_Other(106, "Bank_Income_Other"),

	Bank_Pay_Payable(107, "Bank_Pay_Payable"), Bank_Pay_Salary(108, "Bank_Pay_Salary"),
	Bank_Pay_Expensive(109, "Bank_Pay_Expensive"), Bank_Pay_Tax(110, "Bank_Pay_Tax"),
	Bank_Pay_SocialSecurity(111, "Bank_Pay_SocialSecurity"), Bank_Pay_BankFee(112, "Bank_Pay_BankFee"),
	Bank_Pay_OtherPayable(113, "Bank_Pay_OtherPayable"), Bank_Pay_Other(114, "Bank_Pay_Other"),Bank_Pay_OtherReceivable(115,"Bank_Pay_OtherReceivable"),
	Bank_Pay_Receivable(116,"Bank_Pay_Receivable"),
	BanK_DefChargePerson(117,"Bank_Pay_DefChargePersion"),
	Issue_Invoice(200, "Issue_Invoice"), Receive_Invoice(300, "Receive_Invoice"), Handle_VTA(400, "Handle_VTA"),
	Accrued_Salary(500, "Accrued_Salary");

	
	public final int key;
	public final String value;

	private OriginType(int key, String value) {
		this.key = key;
		this.value = value;

	}

}
