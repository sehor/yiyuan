package yiyuan.utils.msofficetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.Classification.Classfication;
import yiyuan.JinDie.Classification.ClassficationService;
import yiyuan.JinDie.JinDieRecord.JinDieRecord;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.utils.CompanyProperties;

@Service
public class OriginProcessImpl implements OriginProcess {
	@Autowired
	CompanyProperties companyProperties;
	@Autowired
	ClassficationService classficationService;
	@Autowired
	OriginService originService;

	private final float CJ_Tax_Rate = 0.07f;
	private final float JY_Tax_Rate = 0.03f;
	private final float DFJY_Tax_Rate = 0.02f;

	private String companyName = "未指点公司名称";

	private EntryHelper entryHelper = new EntryHelper(); // 主类内部共享一个唯一内部类实例

	@Override
	public OriginProcess setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	@Override
	public List<JinDieRecord> proccessToRecord(List<Origin> origins, LocalDate date) {
		// TODO Auto-generated method stub

		this.entryHelper.initMap(); // 初始化

		List<JinDieRecord> records = new ArrayList<>();
		
		/*records.addAll(proccessBankToRecord(origins, date));
		
		 * records.addAll(processIssueInvoiceToRecords(origins, date));
		 * records.addAll(processReceiveInvoiceToRecords(origins, date));
		 * records.addAll(processHandleVTAToRecords(origins, date));
		 */
		  records.addAll(processSalaryToRecords(origins, date));
		
		return records;
	}

	@Override
	public List<Origin> preProcessOrigin(List<Origin> origins) {
		// TODO Auto-generated method stub
		origins = preHandleBankOrigin(origins); // 先处理银行的origin
		origins = groupByRelatedAccount(origins); // 先合并同一Type并且relative_account相同的origins, 比如开出或收到的发票，银行费用等
		return origins;
	}

	@SuppressWarnings("deprecation")
	private List<JinDieRecord> processReceiveInvoiceToRecordsByType(List<Origin> origins, LocalDate date, String type) {

		List<JinDieRecord> records = new ArrayList<>();
		if (origins.size() < 1)
			return records;

		double tax = 0, amount = 0;
		for (Origin origin : origins) {
			// System.out.println("receive: " + origin.getRelative_account());
			JinDieRecord record = new JinDieRecord();
			tax += origin.getInvoice_tax();
			amount += origin.getInvoice_amount();

			record.set日期(date);
			record.set凭证字("记");
			record.set凭证号(this.entryHelper.genEntryNum(origin));

			record.set分录序号(this.entryHelper.genItemNum(origin));

			record.set摘要("收到发票，确认费用");

			String accountName = getAccountNum(origin.getRelative_account());
			// System.out.println("accountNum: " + accountName);
			record.set科目代码(accountName); //
			record.set科目名称(origin.getRelative_account());
			record.set贷方金额(origin.getInvoice_tax() + origin.getInvoice_amount());

			records.add(record);
		}
		Origin origin = origins.get(0);
		JinDieRecord record1 = new JinDieRecord();
		record1.set日期(date);
		record1.set凭证字("记");
		record1.set凭证号(this.entryHelper.genEntryNum(origin));
		record1.set分录序号(this.entryHelper.genEntryNum(origin));

		record1.set科目代码(companyProperties.getCompanies().get(this.companyName).get("cost")); // 成本科目

		record1.set借方金额(amount);
		record1.set摘要("收到发票，确认费用");

		JinDieRecord record2 = new JinDieRecord();
		record2.set日期(date);
		record2.set凭证字("记");
		record2.set凭证号(this.entryHelper.genEntryNum(origin));
		record2.set分录序号(this.entryHelper.genEntryNum(origin));

		record2.set科目代码("22210101"); // 进项税额
		record2.set借方金额(tax);
		record2.set摘要("收到发票，确认费用");

		records.addAll(List.of(record1, record2));
		return records;

	}

	private List<JinDieRecord> processReceiveInvoiceToRecords(List<Origin> origins, LocalDate date) {

		List<Origin> list = origins.stream().filter(e -> e.getType().equals("Receive_Invoice"))
				.collect(Collectors.toList());
		List<JinDieRecord> records = new ArrayList<>();
		if (list.size() <= 0)
			return records;

		// 把对应应付账款的记录分出来
		List<Origin> list1 = list.stream().filter(e -> {
			return getAccountNum(e.getRelative_account()).startsWith("2202"); // 应付账款
		}).collect(Collectors.toList());

		if (list1.size() > 0) {
			records.addAll(processReceiveInvoiceToRecordsByType(list1, date, "Receive_Invoice_Payable"));
		} // 对应应付账款的做在同一分录

		list.removeAll(list1);
		records.addAll(processReceiveInvoiceToRecordsByType(list, date, "Receive_Invoice_Other")); // 剩下的在另一个分录

		return records;

	}

	private List<JinDieRecord> processHandleVTAToRecords(List<Origin> origins, LocalDate date) {

		List<Origin> list = origins.stream().filter(e -> e.getType().equals("Handle_VTA")).collect(Collectors.toList());
		List<JinDieRecord> records = new ArrayList<>();
		if (list.size() <= 0)
			return records;

		for (Origin origin : list) {
			System.out.println("handle VTA: " + origin.getRelative_account());
			for (int i = 0; i <= 5; i++) {
				JinDieRecord record = new JinDieRecord();
				record.set日期(date);
				record.set凭证字("记");
				record.set凭证号(this.entryHelper.genEntryNum(origin));
				record.set摘要("月末增值税处理");

				record.set分录序号(this.entryHelper.genItemNum(origin));

				records.add(record);
			}
			records.get(0).set借方金额(origin.getAmout());// 未交增值税
			records.get(0).set科目代码("22210105");

			records.get(1).set贷方金额(origin.getAmout());// 转出未交增值税
			records.get(1).set科目代码("222102");

			records.get(3).set贷方金额(origin.getAmout() * this.CJ_Tax_Rate);// 城建税
			records.get(3).set科目代码(companyProperties.getCompanies().get(this.companyName).get("cj"));

			records.get(4).set贷方金额(origin.getAmout() * this.JY_Tax_Rate);// 教育费
			records.get(4).set科目代码(companyProperties.getCompanies().get(this.companyName).get("jyf"));

			records.get(5).set贷方金额(origin.getAmout() * this.DFJY_Tax_Rate);// 地方教育费
			records.get(5).set科目代码(companyProperties.getCompanies().get(this.companyName).get("dfjyf"));

			records.get(2).set借方金额(records.get(3).get贷方金额() + records.get(4).get贷方金额() + records.get(5).get贷方金额()); // 税金及附加
			records.get(2).set科目代码("540314");

		}

		return records;

	}

	private List<JinDieRecord> processSalaryToRecords(List<Origin> origins, LocalDate date) {

		List<Origin> list = origins.stream().filter(e -> e.getType().contains("Accrued_Salary"))
				.collect(Collectors.toList());
		List<JinDieRecord> records = new ArrayList<>();
		if (list.size() <= 0)
			return records;

		for (Origin origin : list) {
			// System.out.println("salary: " + origin.getSalary_payable());
			JinDieRecord record_fee=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_fee.set摘要("计提工资");
			record_fee.set借方金额(origin.getBasicSalary());
			record_fee.set科目名称("工资费用");
			record_fee.set科目代码(getAccountNum("工资费用"));
			records.addAll(List.of(record_fee));
			
			//如果有单位社保费计提社保
			if(origin.getPayedCompanySecurity()>0.01) {
			JinDieRecord record_comSecu=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_comSecu.set摘要("计提社保费用");
			record_comSecu.set借方金额(origin.getPayedCompanySecurity());
			record_comSecu.set科目名称("社会保险费（单位）");
			record_comSecu.set科目代码(getAccountNum("社会保险费（单位）"));
			
			
			
			JinDieRecord record_comSecu1=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_comSecu1.set摘要("计提社保费");
			record_comSecu1.set贷方金额(origin.getPayedCompanySecurity());
			record_comSecu1.set科目名称("应交社会保险费（单位）");
			record_comSecu1.set科目代码(getAccountNum("应交社会保险费（单位）"));
	
			records.addAll(List.of(record_comSecu,record_comSecu1));
			}
			
			//如果有个人社保费计提社保
			if(origin.getPayedPersonSecurity()>0.01) {
			JinDieRecord record_perSecu=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_perSecu.set摘要("计提社保");
			record_perSecu.set贷方金额(origin.getPayedPersonSecurity());
			record_perSecu.set科目名称("应交社会保险费（个人）");
			record_perSecu.set科目代码(getAccountNum("应交社会保险费（个人）"));
			records.addAll(List.of(record_perSecu));
			}
			
			
			//如果有个人所得税计提个人所得税
			if(origin.getSalary_personTax()>0.01) {
			JinDieRecord record_perTax=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_perTax.set摘要("计提个人所得税");
			record_perTax.set贷方金额(origin.getSalary_personTax());
			record_perTax.set科目名称("应交个人所得税");
			record_perTax.set科目代码(getAccountNum("应交个人所得税"));
			records.addAll(List.of(record_perTax));
			}
			
			
			//如果有公积金计提公积金
			if(origin.getSalary_funds()>0.01) {
			JinDieRecord record_fund=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_fund.set摘要("计提公积金");
			record_fund.set借方金额(origin.getSalary_funds()/2);// 单位和个人平分
			record_fund.set科目名称("公积金费用");
			record_fund.set科目代码(getAccountNum("公积金费用"));
			
			JinDieRecord record_fund1=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_fund1.set摘要("计提公积金");
			record_fund1.set贷方金额(origin.getSalary_funds()/2); // 单位和个人平分
			record_fund1.set科目名称("应交公积金（个人）");
			record_fund1.set科目代码(getAccountNum("应交公积金（个人）"));
			
			JinDieRecord record_fund2=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_fund2.set摘要("计提公积金");
			record_fund2.set贷方金额(origin.getSalary_funds()/2); // 单位和个人平分
			record_fund2.set科目名称("应交公积金（单位）");
			record_fund2.set科目代码(getAccountNum("应交公积金（单位）"));
			records.addAll(List.of(record_fund,record_fund1,record_fund2));
			}
			
			//应付工资
			JinDieRecord record_salaryPayable=createRecord(origin, this.entryHelper.genEntryNum(origin), this.entryHelper.genItemNum(origin));
			record_salaryPayable.set摘要("计提工资");
			record_salaryPayable.set贷方金额(origin.getBasicSalary()-origin.getPayedPersonSecurity()-origin.getSalary_funds()/2-origin.getSalary_personTax());// 单位和个人平分
			record_salaryPayable.set科目名称("应付工资");
			record_salaryPayable.set科目代码(getAccountNum("应付工资"));
			records.addAll(List.of(record_salaryPayable));
		}

		return records;

	}
	
	
	

	public void recordWriteToFile(String path, List<JinDieRecord> records) {

		HSSFWorkbook workbook = new DefaultBeansToXLSTransform<JinDieRecord>(JinDieRecord.class)
				.createWorkbook(records);
		File file = new File(path);
		FileOutputStream outputStream = null;
		if (!file.exists())
			try {
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				workbook.write(outputStream);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					workbook.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	// 获得一个唯一的凭证号list(0)，和顺序增加的记录号 list(1)

	private String getAccountNum(String accountName) {
		Classfication classfication = classficationService.getByNameAndCompanyName(accountName, this.companyName);
		if (classfication == null)
			return "未找到";
		return classfication.get编码();
	}

	private List<Origin> groupByRelatedAccount(List<Origin> origins) {

		// 一些需要group的类型
		String[] types = { OriginType.Issue_Invoice.value, OriginType.Receive_Invoice.value,
				OriginType.Bank_Income_Other.value, OriginType.Bank_Pay_Other.value, OriginType.Bank_Pay_BankFee.value,
				OriginType.Bank_Pay_Salary.value };
		for (String type : types) {

			// 相同type-->
			List<Origin> sameTypeList = origins.stream().filter(e -> e.getType().equals(type)
					&& e.getRelative_account_number() != null && !e.getRelative_account_number().isEmpty())
					.collect(Collectors.toList());

			Map<String, Origin> map = new HashMap<>(); // 存储相同Relative_account_number的origin
			// 有相同Relative_account_number的origin汇总到map
			for (int i = 0; i < sameTypeList.size(); i++) {
				Origin originL = sameTypeList.get(i); // 将要汇总到originM的
				Origin originM = map.get(originL.getRelative_account_number()); // 在map的汇总origin
				if (originM != null) {
					originM.setAmout(originM.getAmout() + originL.getAmout());
					originM.setInvoice_amount(originL.getInvoice_amount() + originM.getInvoice_amount());
					originM.setInvoice_tax(originM.getInvoice_tax() + originL.getInvoice_tax());
					originM.setOccur_date(originL.getOccur_date()); // 最晚的occurred date
				} else if (originM == null) {
					map.put(originL.getRelative_account_number(), originL); // 如果没有，把第一个遇到的加到到map
				}

				// 从origins清空sameTypeList, 把map数据加上
				if (sameTypeList.size() > 0) {
					origins.removeAll(sameTypeList);
					origins.addAll(map.values());
				}
			}

			// <----相同type
		}

		return origins;
	}

	/*
	 * 设置bank origin的相关科目，和明细Type
	 */
	private List<Origin> preHandleBankOrigin(List<Origin> origins) {

		for (Origin origin : origins) {
			if (!origin.getType().contains("Bank")) {
				continue;
			}
			origin.setAmout(origin.getBank_income() + origin.getBank_pay());
			origin.setBrief(origin.getBank_brief1() + " " + origin.getBank_brief2());

			List<Classfication> classfications = classficationService.getAllByName(origin.getRelative_account(),
					this.companyName);
			String 应收账款类的科目 = classficationService.getNumber(classfications, companyName, "应收账款");
			String 其他应收款的科目 = classficationService.getNumber(classfications, companyName, "其他应收款");
			String 其他应收款_其他 = classficationService.getNumber("其他", companyName, "其他应收款");
			String 应付账款类的科目 = classficationService.getNumber(classfications, companyName, "应付账款");
			String 其他应付款类的科目 = classficationService.getNumber(classfications, companyName, "其他应付款");
			String 其他应付款_其他 = classficationService.getNumber("其他", companyName, "其他应付款");

			// bank income，收款是收款，按应收账款-.>其他应收款-,>应付账款->其他应付款的顺序赋值，如果是付款，顺序反之
			if (origin.getBank_income() >= 0.01) {
				if (应收账款类的科目 != null) {
					origin.setType(OriginType.Bank_Income_Receivable.value);
					origin.setRelative_account_number(应收账款类的科目);
				} else if (其他应收款的科目 != null) {
					// origin.setType(OriginType.Bank_Income_Receivable.value);
					setOtherAccountType(origin, OriginType.Bank_Income_Receivable.value);
					origin.setRelative_account_number(其他应收款的科目);
				} else if (应付账款类的科目 != null) {
					origin.setType(OriginType.Bank_Income_Other.value);
					origin.setRelative_account_number(应付账款类的科目);
				} else if (其他应付款类的科目 != null) {
					setOtherAccountType(origin, OriginType.Bank_Income_OtherPayable.value);
					origin.setRelative_account_number(其他应付款类的科目);
				} else if (isContainKeyword(origin.getBrief(), "bankInterest")) {
					origin.setRelative_account_number(
							companyProperties.getCompanies().get(this.companyName).get("bankFee"));
					origin.setType(OriginType.Bank_Income_Interest.value);
				} else if (classfications.size() == 0) { // 没有对应的科目
					origin.setType(OriginType.Bank_Income_Other.value);
					if (其他应收款_其他 != null) {
						origin.setRelative_account_number(其他应收款_其他);
					} else {
						origin.setRelative_account_number(其他应付款_其他 != null ? 其他应付款_其他 : "未找到");
					}
				}
			} else if (origin.getBank_pay() >= 0.01) {
				if (应付账款类的科目 != null) {
					origin.setType(OriginType.Bank_Pay_Payable.value);
					origin.setRelative_account_number(应付账款类的科目);
				} else if (其他应付款类的科目 != null) {
					setOtherAccountType(origin, OriginType.Bank_Pay_OtherPayable.value);
					origin.setRelative_account_number(其他应付款类的科目);
				} else if (其他应收款的科目 != null) {
					setOtherAccountType(origin, OriginType.Bank_Pay_OtherReceivable.value);
					origin.setRelative_account_number(其他应收款的科目);
				} else if (应收账款类的科目 != null) {
					origin.setType(OriginType.Bank_Pay_Receivable.value);
					origin.setRelative_account_number(应收账款类的科目);

				} else if (isContainKeyword(origin.getBrief(), "bankSalary")) { // 发放工资
					origin.setRelative_account_number(companyProperties.getCompanies().get(companyName).get("yfgz"));
					origin.setType(OriginType.Bank_Pay_Salary.value);
				} else if (isContainKeyword(origin.getBrief(), "bankTax")) { // 缴税,不设置related account
					// origin.setRelative_account_number(companyProperties.getCompanies().get(companyName).get("bankTax"));
					origin.setType(OriginType.Bank_Pay_Tax.value);
				} else if (isContainKeyword(origin.getBrief(), "bankSecurity")) { // 社保,不设置related account
					// origin.setRelative_account_number(companyProperties.getCompanies().get(companyName).get("socailSecurity"));
					origin.setType(OriginType.Bank_Pay_SocialSecurity.value);
				} else if (isContainKeyword(origin.getBrief(), "bankFee")) { // 银行费用
					origin.setRelative_account_number(companyProperties.getCompanies().get(companyName).get("bankFee"));
					origin.setType(OriginType.Bank_Pay_BankFee.value);
				} else if (classfications.size() == 0) { // 没有对应的科目
					origin.setType(OriginType.Bank_Pay_Other.value);
					if (其他应付款_其他 != null) {
						origin.setRelative_account_number(其他应付款_其他);
					} else {
						origin.setRelative_account_number(其他应收款_其他 != null ? 其他应收款_其他 : "未找到");
					}
				}
			}
		}

		return origins;
	}

	private boolean isContainKeyword(String brief, String keyword) {
		String[] strs = companyProperties.getKeyword().get(keyword).split("\\|");
		for (String str : strs) {
			if (brief.contains(str)) {

				return true;
			}
		}
		return false;
	}

	private List<JinDieRecord> proccessBankToRecord(List<Origin> origins, LocalDate date) {

		List<JinDieRecord> jinDieRecords = new ArrayList<>();

		for (Origin origin : origins) {
			if (origin.getType().equals(OriginType.Bank_Pay_Tax.value)) { // 处理缴税
				jinDieRecords.addAll(this.hanldeBankPayTax(origin));
			} else if (origin.getType().equals(OriginType.Bank_Pay_SocialSecurity.value)) { // 处理社会保险
				jinDieRecords.addAll(this.handleBankPaySecurity(origin));
			}
			// ----> 如有特殊处理，在这里插入

			// 处理一般的bank origin
			else {
				if (origin.getBank_income() > 0.01) { // 银行借方
					JinDieRecord bank_record = creatBankRecord(origin, this.entryHelper.genEntryNum(origin),
							this.entryHelper.genItemNum(origin));
					JinDieRecord record = createRecord(origin, this.entryHelper.genEntryNum(origin),
							this.entryHelper.genItemNum(origin));
					record.set贷方金额(origin.getAmout());
					jinDieRecords.addAll(List.of(bank_record, record));
				} else if (origin.getBank_pay() > 0.01) { // 银行贷方
					JinDieRecord record = createRecord(origin, this.entryHelper.genEntryNum(origin),
							this.entryHelper.genItemNum(origin));
					record.set借方金额(origin.getAmout());
					JinDieRecord bank_record = creatBankRecord(origin, this.entryHelper.genEntryNum(origin),
							this.entryHelper.genItemNum(origin));
					jinDieRecords.addAll(List.of(record, bank_record));
				}

			}
		}

		return jinDieRecords;

	}

	private Collection<? extends JinDieRecord> handleBankPaySecurity(Origin origin) {
		// TODO Auto-generated method stub
		List<JinDieRecord> records = new ArrayList<>();
		JinDieRecord person_security = createRecord(origin, this.entryHelper.genEntryNum(origin),
				this.entryHelper.genItemNum(origin));
		person_security.set科目名称("应交社会保险费（个人）");
		person_security.set科目代码(classficationService.getByNameAndCompanyName("应交社会保险费（个人）", this.companyName).get编码());
		String personSecurityAmout = companyProperties.getCompanies().get(this.companyName).get("personSecurityAmout");
		person_security.set借方金额(Double.valueOf(personSecurityAmout));

		JinDieRecord company_security = createRecord(origin, this.entryHelper.genEntryNum(origin),
				this.entryHelper.genItemNum(origin));
		company_security.set科目名称("应交社会保险费（个人）");
		company_security.set科目代码(classficationService.getByNameAndCompanyName("应交社会保险费（个人）", this.companyName).get编码());
		company_security.set借方金额(origin.getAmout() - person_security.get借方金额());

		JinDieRecord bank_record = creatBankRecord(origin, this.entryHelper.genEntryNum(origin),
				this.entryHelper.genItemNum(origin));

		records.addAll(List.of(person_security, company_security, bank_record));

		return records;
	}

	private JinDieRecord createRecord(Origin origin, int entryNum, int itemNum) {
		JinDieRecord record = new JinDieRecord();
		record.set日期(origin.getOccur_date());
		record.set摘要(origin.getBrief());
		record.set凭证字("记");
		record.set分录序号(itemNum);
		record.set凭证号(entryNum);
		record.set科目名称(origin.getRelative_account());
		record.set科目代码(origin.getRelative_account_number() != null ? origin.getRelative_account_number() : "");
		return record;

	}

	private JinDieRecord creatBankRecord(Origin origin, int entryNum, int itemNum) {

		JinDieRecord record = createRecord(origin, entryNum, itemNum);
		if (origin.getBank_income() > 0.01) {
			record.set借方金额(origin.getAmout());
		} else if (origin.getBank_pay() > 0.01) {
			record.set贷方金额(origin.getAmout());
		}

		record.set科目代码("100201");
		record.set科目名称("银行存款（基本户）");

		return record;
	}

	private List<JinDieRecord> hanldeBankPayTax(Origin origin) {

		List<JinDieRecord> jinDieRecords = new ArrayList<>();

		// 增值税和附加税混合的缴税
		if (isContainKeyword(origin.getBrief(), "bankVATAndAdditionalTax")) {
			double rate = this.CJ_Tax_Rate + this.JY_Tax_Rate + this.DFJY_Tax_Rate;
			double vatMount = origin.getAmout() / (1 + rate);
			JinDieRecord vta_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin)); // 增值税
			vta_record.set借方金额(vatMount);
			vta_record.set科目名称("未交增值税");
			vta_record.set科目代码(classficationService.getByNameAndCompanyName("未交增值税", companyName).get编码());

			JinDieRecord cj_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			cj_record.set借方金额(vatMount * this.CJ_Tax_Rate / rate);
			cj_record.set科目名称("应交城市维护建设税");
			cj_record.set科目代码(classficationService.getByNameAndCompanyName("应交城市维护建设税", companyName).get编码());

			JinDieRecord jy_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			jy_record.set借方金额(vatMount * this.JY_Tax_Rate / rate);
			jy_record.set科目名称("教育费附加");
			jy_record.set科目代码(classficationService.getByNameAndCompanyName("教育费附加", companyName).get编码());

			JinDieRecord dfjy_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			dfjy_record.set借方金额(vatMount * this.DFJY_Tax_Rate / rate);
			dfjy_record.set科目名称("地方教育费附加");
			dfjy_record.set科目代码(classficationService.getByNameAndCompanyName("地方教育费附加", companyName).get编码());

			jinDieRecords.addAll(List.of(vta_record, cj_record, jy_record, dfjy_record));

		}
		// 单独增值税
		else if (isContainKeyword(origin.getBrief(), "bankVAT")) {
			JinDieRecord vta_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin)); // 增值税
			vta_record.set借方金额(origin.getAmout());
			vta_record.set科目名称("未交增值税");
			vta_record.set科目代码(classficationService.getByNameAndCompanyName("未交增值税", companyName).get编码());
			jinDieRecords.addAll(List.of(vta_record));
		}
		// 附加税
		else if (isContainKeyword(origin.getBrief(), "bankAdditionanlTax")) {
			double rate = this.CJ_Tax_Rate + this.JY_Tax_Rate + this.DFJY_Tax_Rate;
			double amout = origin.getAmout();
			JinDieRecord cj_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			cj_record.set借方金额(amout * this.CJ_Tax_Rate / rate);
			cj_record.set科目名称("应交城市维护建设税");
			cj_record.set科目代码(classficationService.getByNameAndCompanyName("应交城市维护建设税", companyName).get编码());

			JinDieRecord jy_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			jy_record.set借方金额(amout * this.JY_Tax_Rate / rate);
			jy_record.set科目名称("教育费附加");
			jy_record.set科目代码(classficationService.getByNameAndCompanyName("教育费附加", companyName).get编码());

			JinDieRecord dfjy_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			dfjy_record.set借方金额(amout * this.DFJY_Tax_Rate / rate);
			dfjy_record.set科目名称("地方教育费附加");
			dfjy_record.set科目代码(classficationService.getByNameAndCompanyName("地方教育费附加", companyName).get编码());

			jinDieRecords.addAll(List.of(cj_record, jy_record, dfjy_record));
		}
		// 企业所得税
		else if (isContainKeyword(origin.getBrief(), "bankIncomeTax")) {
			JinDieRecord qysd_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			qysd_record.set借方金额(origin.getAmout());
			qysd_record.set科目名称("所得税费用");
			qysd_record.set科目代码(classficationService.getByNameAndCompanyName("所得税费用", companyName).get编码());

			jinDieRecords.addAll(List.of(qysd_record));
		}
		// 个人所得税
		else if (isContainKeyword(origin.getBrief(), "bankPersonalTax")) {
			JinDieRecord grsd_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			grsd_record.set借方金额(origin.getAmout());
			grsd_record.set科目名称("应交个人所得税");
			grsd_record.set科目代码(classficationService.getByNameAndCompanyName("应交个人所得税", companyName).get编码());

			jinDieRecords.add(grsd_record);
		}
		// 其他归集到管理费用税金
		else {

			JinDieRecord iq_record = createRecord(origin, this.entryHelper.genEntryNum(origin),
					this.entryHelper.genItemNum(origin));
			iq_record.set借方金额(origin.getAmout());
			iq_record.set科目名称("归集为管理费的税费");
			iq_record.set科目代码(classficationService.getByNameAndCompanyName("归集为管理费的税费", companyName).get编码());

			jinDieRecords.add(iq_record);
		}

		// 贷方的银行记录
		JinDieRecord bank_record = creatBankRecord(origin, this.entryHelper.genEntryNum(origin),
				this.entryHelper.genItemNum(origin));
		jinDieRecords.add(bank_record);

		return jinDieRecords;
	}

	// 有关charge person 的账户，单独的归为一类 bank_defChargetperson
	private void setOtherAccountType(Origin origin, String type) {
		if (origin.getRelative_account()
				.equals(companyProperties.getCompanies().get(this.companyName).get("defChargePerson"))) {
			origin.setType(OriginType.BanK_DefChargePerson.value);
		} else {

			origin.setType(type);
		}

	}

	// 必须每个会计期间共享一个实例，不然会产生凭证号的混乱
	private class EntryHelper {

		private Map<String, List<Integer>> map = new HashMap<>();

		public void initMap() {
			int start = 200;
			for (OriginType originType : OriginType.values()) {
				map.put(originType.value, List.of(start++, 0));
			}
		}

		public int genEntryNum(Origin origin) {
			return map.get(origin.getType()).get(0);
		}

		public int genItemNum(Origin origin) {
			int num = map.get(origin.getType()).get(1);
			map.put(origin.getType(), List.of(map.get(origin.getType()).get(0), ++num)); // 更新map
			return num;
		}
	}

	
	

}
