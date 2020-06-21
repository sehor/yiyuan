package yiyuan.JinDie;

public enum CommonAccount {

	YFGZ("应付职工薪酬-应付工资"), YHFY("财务费用-银行手续费"), GLFY_SJ("管理费用-归集在管理费用的税金"), YJGESDS("应交税费-应交个人所得税"), BGSZJ("管理费用-办公租金"),
	
	YJGRGJJ("其他应付款-应交公积金（个人）"),YJDWGJJ("其他应付款-应交公积金（单位）"),YJDWSB("其他应付款-应交社会保险费（单位）"),
	YJGRSB("其他应付款-应交社会保险费（个人）"),
	
	ZZSXXS("应交增值税-销项税额"),ZZSJXS("应交增值税-进项税额");
	
	
	public final String val;

	private CommonAccount(String val) {
		this.val = val;
	}
}
