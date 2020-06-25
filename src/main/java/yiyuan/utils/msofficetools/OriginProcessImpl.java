package yiyuan.utils.msofficetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.CommonAccount;
import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.Classification.Classfication;
import yiyuan.JinDie.Classification.ClassficationService;
import yiyuan.JinDie.JinDieRecord.JinDieRecord;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.utils.AccountPeriod;
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

		records.addAll(proccessBankToRecord(origins, date));

		records.addAll(processIssueInvoiceToRecords(origins, date));
		records.addAll(processReceiveInvoiceToRecords1(origins, date));
		records.addAll(processHandleVTAToRecords(origins, date));

		records.addAll(processSalaryToRecords(origins, date));
		 records.addAll(processBillToRecords(origins,date));

		return records;
	}


	private List<JinDieRecord> processIssueInvoiceToRecords(List<Origin> origins, LocalDate date) {
		// TODO Auto-generated method stub
		List<JinDieRecord> records = new ArrayList<>();
		List<Origin> list = origins.stream().filter(e -> e.getType().contains(OriginType.Issue_Invoice.value))
				.collect(Collectors.toList());
		if (list.size() < 1)
			return records;
		Map<String, Double> map = new HashMap<>();// 用来计算不同收入种类的收入
		double tax_sum = 0;
		for (Origin origin : list) {
			JinDieRecord record_receivable = createRecord(origin);
			record_receivable.set摘要("开出发票，确认应收款项");
			record_receivable.set借方金额(origin.getInvoice_amount() + origin.getInvoice_tax());
			record_receivable.set科目名称(origin.getRelative_account());
			record_receivable.set科目代码(
					classficationService.getNumByMutilName("应收账款-" + origin.getRelative_account(), this.companyName));

			tax_sum += origin.getInvoice_tax();

			Double income_sum = (map.get(origin.getIncome_account()) != null ? map.get(origin.getIncome_account()) : 0);
			map.put(origin.getIncome_account(), origin.getInvoice_amount() + income_sum);
			record_receivable.set日期(date);
			records.add(record_receivable);
		}

		// 写入各种收入科目
		for (Entry<String, Double> entry : map.entrySet()) {
			JinDieRecord record_income = createRecord(list.get(0));
			record_income.set摘要("开出发票，确认收入");
			record_income.set科目名称(entry.getKey());
			record_income.set科目代码(classficationService.getNumByMutilName(entry.getKey(), this.companyName));
			record_income.set贷方金额(entry.getValue());

			record_income.set日期(date);
			records.add(record_income);
		}

		// 写入增值税-销项

		JinDieRecord record_tax = creatBankRecord(list.get(0));
		record_tax.set摘要("开出发票，计提税费");
		record_tax.set科目名称(CommonAccount.ZZSXXS.val);
		record_tax.set科目代码(classficationService.getNumByMutilName(CommonAccount.ZZSXXS.val, this.companyName));
		record_tax.set贷方金额(tax_sum);
		record_tax.set日期(date);

		records.add(record_tax);

		return records;
	}

	private List<JinDieRecord> processReceiveInvoiceToRecords1(List<Origin> origins, LocalDate date) {
		// TODO Auto-generated method stub
		List<JinDieRecord> records = new ArrayList<>();
		List<Origin> list = origins.stream().filter(e -> e.getType().contains(OriginType.Receive_Invoice.value))
				.collect(Collectors.toList());
		if (list.size() < 1)
			return records;
		Map<String, Double> map = new HashMap<>();// 用来计算不同成本费用种类
		double tax_sum = 0;
		for (Origin origin : list) {
			JinDieRecord record_receivable = createRecord(origin);
			record_receivable.set日期(date);
			record_receivable.set摘要("收到发票，确认应付款项");
			record_receivable.set贷方金额(origin.getInvoice_amount() + origin.getInvoice_tax());
			record_receivable.set科目名称(origin.getRelative_account());
			record_receivable.set科目代码(origin.getRelative_account_number());

			tax_sum += origin.getInvoice_tax();

			Double cost_sum = (map.get(origin.getPayable_account()) != null ? map.get(origin.getPayable_account()) : 0);
			map.put(origin.getPayable_account(), origin.getInvoice_amount() + cost_sum);
			records.add(record_receivable);
		}

		// 写入各种成本费用
		for (Entry<String, Double> entry : map.entrySet()) {
			JinDieRecord record_cost = createRecord(list.get(0));
			record_cost.set摘要("收到发票，确认费用");
			record_cost.set科目名称(entry.getKey());
			record_cost.set科目代码(classficationService.getNumByMutilName(entry.getKey(), this.companyName));
			record_cost.set借方金额(entry.getValue());
			record_cost.set日期(date);
			records.add(record_cost);

		}

		// 写入增值税-进项

		JinDieRecord record_tax = creatBankRecord(list.get(0));
		record_tax.set摘要("收到发票，计算税费");
		record_tax.set科目名称(CommonAccount.ZZSJXS.val);
		record_tax.set科目代码(classficationService.getNumByMutilName(CommonAccount.ZZSJXS.val, this.companyName));
		record_tax.set借方金额(tax_sum);
		record_tax.set日期(date);
		records.add(record_tax);

		return records;
	}

	@Override
	public List<Origin> preProcessOrigin(List<Origin> origins) {
		// TODO Auto-generated method stub
		origins = preHandleBankOrigin(origins); // 先处理银行的origin
		origins=  preBill(origins);
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
			
			AccountPeriod period=new AccountPeriod(origin.getOccur_date().plusMonths(1)); //取下一个月的数据
			
			//应付工资数额
			double payable_salary=originService.findSalary(this.companyName, period.getBeginDay(), period.getLastDay());
			if(payable_salary<0.001) {
				 payable_salary=origin.getBasicSalary();
			}
			double person_tax=originService.findPersonTax(this.companyName, period.getBeginDay(), period.getLastDay());
			double person_fund=originService.findPersonFund(this.companyName, period.getBeginDay(), period.getLastDay());
			
			JinDieRecord record_fee = createRecord(origin);
			record_fee.set摘要("计提工资");
			record_fee.set借方金额(payable_salary+person_tax+origin.getPayedPersonSecurity()+person_fund);  //工资费用=应付工资+个税+个人社保+个人公积金
			record_fee.set科目名称("工资费用");
			record_fee.set科目代码(getAccountNum("工资费用"));
			records.addAll(List.of(record_fee));

			// 如果有单位社保费计提社保
			if (origin.getPayedCompanySecurity() > 0.01) {
				JinDieRecord record_comSecu = createRecord(origin);
				record_comSecu.set摘要("计提社保费用");
				record_comSecu.set借方金额(origin.getPayedCompanySecurity());
				record_comSecu.set科目名称("社会保险费（单位）");
				record_comSecu.set科目代码(getAccountNum("社会保险费（单位）"));

				JinDieRecord record_comSecu1 = createRecord(origin);
				record_comSecu1.set摘要("计提社保费");
				record_comSecu1.set贷方金额(origin.getPayedCompanySecurity());
				record_comSecu1.set科目名称("应交社会保险费（单位）");
				record_comSecu1.set科目代码(getAccountNum("应交社会保险费（单位）"));

				records.addAll(List.of(record_comSecu, record_comSecu1));
			}

			// 如果有个人社保费计提社保
			if (origin.getPayedPersonSecurity() > 0.01) {
				JinDieRecord record_perSecu = createRecord(origin);
				record_perSecu.set摘要("计提社保");
				record_perSecu.set贷方金额(origin.getPayedPersonSecurity());
				record_perSecu.set科目名称("应交社会保险费（个人）");
				record_perSecu.set科目代码(getAccountNum("应交社会保险费（个人）"));
				records.addAll(List.of(record_perSecu));
			}

			// 如果有个人所得税计提个人所得税
			if (person_tax > 0.01) {
				JinDieRecord record_perTax = createRecord(origin);
				record_perTax.set摘要("计提个人所得税");
				record_perTax.set贷方金额(person_tax);
				record_perTax.set科目名称("应交个人所得税");
				record_perTax.set科目代码(getAccountNum("应交个人所得税"));
				records.addAll(List.of(record_perTax));
			}

			// 如果有公积金计提公积金
			if (person_fund > 0.01) {
				JinDieRecord record_fund = createRecord(origin);
				record_fund.set摘要("计提公积金");
				record_fund.set借方金额(person_fund);// 单位等于个人
				record_fund.set科目名称("公积金费用");
				record_fund.set科目代码(getAccountNum("公积金费用"));

				JinDieRecord record_fund1 = createRecord(origin);
				record_fund1.set摘要("计提公积金");
				record_fund1.set贷方金额(person_fund); // 单位等于个人
				record_fund1.set科目名称("应交公积金（个人）");
				record_fund1.set科目代码(getAccountNum("应交公积金（个人）"));

				JinDieRecord record_fund2 = createRecord(origin);
				record_fund2.set摘要("计提公积金");
				record_fund2.set贷方金额(person_fund); // 单位等于
				record_fund2.set科目名称("应交公积金（单位）");
				record_fund2.set科目代码(getAccountNum("应交公积金（单位）"));
				records.addAll(List.of(record_fund, record_fund1, record_fund2));
			}

			// 应付工资
			JinDieRecord record_salaryPayable = createRecord(origin);
			record_salaryPayable.set摘要("计提工资");
			record_salaryPayable.set贷方金额(payable_salary);// 单位和个人平分
			record_salaryPayable.set科目名称("应付工资");
			record_salaryPayable.set科目代码(getAccountNum("应付工资"));
			records.addAll(List.of(record_salaryPayable));
		}

		return records;

	}

	private String getAccountNum(String accountName) {
		Classfication classfication = classficationService.getByNameAndCompanyName(accountName, this.companyName);
		if (classfication == null)
			return "未找到";
		return classfication.get编码();
	}

	private List<Origin> groupByRelatedAccount(List<Origin> origins) {

		// 把发票业务的科目代码补上
		for (Origin origin : origins) {
			if (origin.getType().contains(OriginType.Issue_Invoice.value)) {
				origin.setRelative_account_number(classficationService
						.getNumByMutilName("应收账款-" + origin.getRelative_account(), this.companyName));
			} else if (origin.getType().contains(OriginType.Receive_Invoice.value)) {
				String number = classficationService.getNumByMutilName("应付账款-" + origin.getRelative_account(),
						this.companyName);

				if (number.equals("未找到")) {
					number = classficationService.getNumByMutilName("其他应付款-" + origin.getRelative_account(),
							this.companyName);
				}
				if (number.equals("未找到")) {
					number = classficationService.getNumByMutilName("其他应付款-其他", this.companyName);
				}
				origin.setRelative_account_number(number);
			}
		}

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
				}  else if (应付账款类的科目 != null) {
					origin.setType(OriginType.Bank_Income_Payable.value);
					origin.setRelative_account_number(应付账款类的科目);
				} 
				else if (其他应收款的科目 != null) {
					// origin.setType(OriginType.Bank_Income_Receivable.value);
					setOtherAccountType(origin, OriginType.Bank_Income_OtherReceivable.value);
					origin.setRelative_account_number(其他应收款的科目);
				}else if (其他应付款类的科目 != null) {
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
				} else if (isContainKeyword(origin.getBrief(), "bankSalary")) { // 发放工资
					origin.setRelative_account_number(companyProperties.getCompanies().get(companyName).get("yfgz"));
					origin.setType(OriginType.Bank_Pay_Salary.value);
				} else if (应收账款类的科目 != null) {
					origin.setType(OriginType.Bank_Pay_Receivable.value);
					origin.setRelative_account_number(应收账款类的科目);

				} else if (其他应付款类的科目 != null) {
					setOtherAccountType(origin, OriginType.Bank_Pay_OtherPayable.value);
					origin.setRelative_account_number(其他应付款类的科目);
				} else if (其他应收款的科目 != null) {
					setOtherAccountType(origin, OriginType.Bank_Pay_OtherReceivable.value);
					origin.setRelative_account_number(其他应收款的科目);
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

		ExcelUtil.filter(origins, e -> e.getType().equals(OriginType.Bank_Pay_Tax.value))
				.forEach(e -> jinDieRecords.addAll(this.hanldeBankPayTax(e)));// 处理缴税

		ExcelUtil.filter(origins, e -> e.getType().equals(OriginType.Bank_Pay_SocialSecurity.value))
				.forEach(e -> jinDieRecords.addAll(this.handleBankPaySecurity(e)));// 处理社会保险

		jinDieRecords.addAll(handleBankGoupItems(
				ExcelUtil.filter(origins, e -> e.getType().equals(OriginType.Bank_Income_Receivable.value)))); // 收货款，合并分录
		jinDieRecords.addAll(handleBankGoupItems(
				ExcelUtil.filter(origins, e -> e.getType().equals(OriginType.Bank_Pay_Payable.value)))); // 付款，合并分录
		jinDieRecords.addAll(handleBankGoupItems(
				ExcelUtil.filter(origins, e -> e.getType().equals(OriginType.Bank_Pay_Other.value)))); // 其他付款，合并分录

		String exclude = OriginType.Bank_Pay_Tax.value + OriginType.Bank_Pay_SocialSecurity.value
				+ OriginType.Bank_Income_Receivable.value + OriginType.Bank_Pay_Payable.value
				+ OriginType.Bank_Pay_Other.value;

		for (Origin origin : ExcelUtil.filter(origins, e -> !exclude.contains(e.getType()))) {

			if (origin.getBank_income() > 0.01) { // 银行借方
				JinDieRecord bank_record = creatBankRecord(origin);
				JinDieRecord record = createRecord(origin);
				record.set贷方金额(origin.getAmout());
				jinDieRecords.addAll(List.of(bank_record, record));
			} else if (origin.getBank_pay() > 0.01) { // 银行贷方
				JinDieRecord record = createRecord(origin);
				record.set借方金额(origin.getAmout());
				JinDieRecord bank_record = creatBankRecord(origin);
				jinDieRecords.addAll(List.of(record, bank_record));
			}

		}

		return jinDieRecords;

	}

	private Collection<? extends JinDieRecord> handleBankPaySecurity(Origin origin) {
		// TODO Auto-generated method stub
		List<JinDieRecord> records = new ArrayList<>();
		
		AccountPeriod period=new AccountPeriod(origin.getOccur_date());
		double personSecruty=originService.findPersonSecurity(this.companyName, period.getBeginDay(), period.getLastDay());
		
		
		JinDieRecord person_security = createRecord(origin);
		person_security.set科目名称("应交社会保险费（个人）");
		person_security.set科目代码(classficationService.getNumByMutilName("其他应付款-应交社会保险费（个人）", companyName));
		//String personSecurityAmout = companyProperties.getCompanies().get(this.companyName).get("personSecurityAmout");
		//person_security.set借方金额(origin.getBank_pay()/3); //大约总是的1/3
		person_security.set借方金额(personSecruty);

		JinDieRecord company_security = createRecord(origin, this.entryHelper.genEntryNum(origin),
				this.entryHelper.genItemNum(origin));
		company_security.set科目名称("应交社会保险费（单位）");
		company_security.set科目代码(classficationService.getNumByMutilName("其他应付款-应交社会保险费（单位）", this.companyName));
		company_security.set借方金额(origin.getAmout() - personSecruty);

		JinDieRecord bank_record = creatBankRecord(origin);

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

	private JinDieRecord createRecord(Origin origin) {
		int entryNum = this.entryHelper.genEntryNum(origin);
		int itemNum = this.entryHelper.genItemNum(origin);
		JinDieRecord record = createRecord(origin, entryNum, itemNum);
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

	private JinDieRecord creatBankRecord(Origin origin) {
		int entryNum = this.entryHelper.genEntryNum(origin);
		int itemNum = this.entryHelper.genItemNum(origin);
		JinDieRecord record = creatBankRecord(origin, entryNum, itemNum);
		return record;
	}

	private List<JinDieRecord> hanldeBankPayTax(Origin origin) {

		List<JinDieRecord> jinDieRecords = new ArrayList<>();

		// 增值税和附加税混合的缴税
		if (isContainKeyword(origin.getBrief(), "bankVATAndAdditionalTax")) {
			double rate = this.CJ_Tax_Rate + this.JY_Tax_Rate + this.DFJY_Tax_Rate;
			double vatMount = origin.getAmout() / (1 + rate);
			JinDieRecord vta_record = createRecord(origin); // 增值税
			vta_record.set借方金额(vatMount);
			vta_record.set科目名称("未交增值税");
			vta_record.set科目代码(classficationService.getNumByMutilName("应交税费-未交增值税", companyName));

			JinDieRecord cj_record = createRecord(origin);
			cj_record.set借方金额(vatMount * this.CJ_Tax_Rate );
			cj_record.set科目名称("应交城市维护建设税");
			cj_record.set科目代码(classficationService.getNumByMutilName("应交税费-应交城市维护建设税", companyName));

			JinDieRecord jy_record = createRecord(origin);
			jy_record.set借方金额(vatMount * this.JY_Tax_Rate );
			jy_record.set科目名称("教育费附加");
			jy_record.set科目代码(classficationService.getNumByMutilName("应交税费-教育费附加", companyName));

			JinDieRecord dfjy_record = createRecord(origin);
			dfjy_record.set借方金额(vatMount -cj_record.get借方金额()-jy_record.get借方金额());
			dfjy_record.set科目名称("地方教育费附加");
			dfjy_record.set科目代码(classficationService.getNumByMutilName("应交税费-地方教育费附加", companyName));

			jinDieRecords.addAll(List.of(vta_record, cj_record, jy_record, dfjy_record));

		}
		// 单独增值税
		else if (isContainKeyword(origin.getBrief(), "bankVAT")) {
			JinDieRecord vta_record = createRecord(origin); // 增值税
			vta_record.set借方金额(origin.getAmout());
			vta_record.set科目名称("未交增值税");
			vta_record.set科目代码(classficationService.getNumByMutilName("应交税费-未交增值税", companyName));
			jinDieRecords.addAll(List.of(vta_record));
		}
		// 附加税
		else if (isContainKeyword(origin.getBrief(), "bankAdditionanlTax")) {
			double rate = this.CJ_Tax_Rate + this.JY_Tax_Rate + this.DFJY_Tax_Rate;
			double amout = origin.getAmout();
			JinDieRecord cj_record = createRecord(origin);
			cj_record.set借方金额(amout * this.CJ_Tax_Rate / rate);
			cj_record.set科目名称("应交城市维护建设税");
			cj_record.set科目代码(classficationService.getNumByMutilName("应交税费-应交城市维护建设税", companyName));

			JinDieRecord jy_record = createRecord(origin);
			jy_record.set借方金额(amout * this.JY_Tax_Rate / rate);
			jy_record.set科目名称("教育费附加");
			jy_record.set科目代码(classficationService.getNumByMutilName("应交税费-教育费附加", companyName));

			JinDieRecord dfjy_record = createRecord(origin);
			dfjy_record.set借方金额(amout * this.DFJY_Tax_Rate / rate);
			dfjy_record.set科目名称("地方教育费附加");
			dfjy_record.set科目代码(classficationService.getNumByMutilName("应交税费-地方教育费附加", companyName));

			jinDieRecords.addAll(List.of(cj_record, jy_record, dfjy_record));
		}
		// 企业所得税
		else if (isContainKeyword(origin.getBrief(), "bankIncomeTax")) {
			JinDieRecord qysd_record = createRecord(origin);
			qysd_record.set借方金额(origin.getAmout());
			qysd_record.set科目名称("所得税费用");
			qysd_record.set科目代码(classficationService.getNumByMutilName("所得税费用", companyName));

			jinDieRecords.addAll(List.of(qysd_record));
		}
		// 个人所得税
		else if (isContainKeyword(origin.getBrief(), "bankPersonalTax")) {
			JinDieRecord grsd_record = createRecord(origin);
			grsd_record.set借方金额(origin.getAmout());
			grsd_record.set科目名称("应交个人所得税");
			grsd_record.set科目代码(classficationService.getNumByMutilName("应交税费-应交个人所得税", companyName));

			jinDieRecords.add(grsd_record);
		}
		// 其他归集到管理费用税金
		else {

			JinDieRecord iq_record = createRecord(origin);
			iq_record.set借方金额(origin.getAmout());
			iq_record.set科目名称("归集为管理费的税费");
			iq_record.set科目代码(classficationService.getNumByMutilName("管理费用-归集为管理费的税费", companyName));

			jinDieRecords.add(iq_record);
		}

		// 贷方的银行记录
		JinDieRecord bank_record = creatBankRecord(origin);
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

	private List<JinDieRecord> handleBankGoupItems(List<Origin> origins) {

		List<JinDieRecord> records = new ArrayList<>();
		if (origins.size() <= 0)
			return records;
		double sum = 0;
		String brief = "";
		String mutilNamePrefix = "";
		boolean isIncome = false;
		for (Origin origin : origins) {
			if (origin.getBank_income() > 0.001) {
				isIncome = true;
			}
			if (origin.getType().equals(OriginType.Bank_Income_Receivable.value)) {
				brief = "收到货款";
				mutilNamePrefix = "应收账款-";
			} else if (origin.getType().equals(OriginType.Bank_Pay_Payable.value)) {
				brief = "付货款";
				mutilNamePrefix = "应付账款-";
			} else if (origin.getType().equals(OriginType.Bank_Pay_OtherPayable.value)) {
				brief = "其他款项";
				mutilNamePrefix = "其他应付款-";
			} else if (origin.getType().equals(OriginType.Bank_Income_OtherReceivable.value)) {
				brief = "其他款项";
				mutilNamePrefix = "其他应收款-";
			}

			JinDieRecord record = createRecord(origin);
			if (isIncome) {
				record.set贷方金额(origin.getBank_income());
			} else {
				record.set借方金额(origin.getBank_pay());
			}

			record.set科目名称(origin.getRelative_account());
			record.set科目代码(classficationService.getNumByMutilName(mutilNamePrefix + origin.getRelative_account(),
					companyName));

			record.set摘要(brief);
			record.set日期(origin.getOccur_date().with(TemporalAdjusters.lastDayOfMonth()));

			sum += isIncome ? origin.getBank_income() : origin.getBank_pay();

			records.add(record);
		}

		JinDieRecord record_bank = creatBankRecord(origins.get(0));
		if (isIncome) {
			record_bank.set借方金额(sum);
		} else {
			record_bank.set贷方金额(sum);
		}
		record_bank.set日期(origins.get(0).getOccur_date().with(TemporalAdjusters.lastDayOfMonth()));
		records.add(record_bank);

		return records;
	}

	private List<Origin> preBill(List<Origin> origins) {
		// TODO Auto-generated method stub
		for(Origin origin:origins) {
			if(!origin.getType().equalsIgnoreCase(OriginType.Bill.value)) {
				continue;
			}
			if(origin.getBill_income()>0.01) {
				origin.setType(OriginType.Bill_Income.value);
				
				String accountNumber=classficationService.getNumByMutilName("应收账款-"+origin.getRelative_account(), this.companyName);
				accountNumber=accountNumber.equals("未找到")?classficationService.getNumByMutilName("应付账款-"+origin.getRelative_account(), this.companyName):accountNumber;
				origin.setRelative_account_number(accountNumber);
				
				origin.setBrief("收到汇票");
			}else {
				origin.setType(OriginType.Bill_Pay.value);
		
				String accountNumber=classficationService.getNumByMutilName("应付账款-"+origin.getRelative_account(), this.companyName);
				accountNumber=accountNumber.equals("未找到")?classficationService.getNumByMutilName("应收账款-"+origin.getRelative_account(), this.companyName):accountNumber;
				origin.setRelative_account_number(accountNumber);
				
				origin.setBrief("发出汇票");
			}
			
		}
		
		return origins;
	}
	

	private Collection<? extends JinDieRecord> processBillToRecords(List<Origin> origins, LocalDate date) {
		// TODO Auto-generated method stub
		List<JinDieRecord> records=new ArrayList<>();
		List<Origin> list=ExcelUtil.filter(origins, e->e.getType().contains(OriginType.Bill.value));
		if(list.size()<=0) return records;
		for(Origin origin:list) {
			String billAccountNum=classficationService.getNumByMutilName("应收票据", this.companyName); //全部是收到汇票，没有涉及到自身发起汇票，所以全是应收票据
			if(origin.getType().equals(OriginType.Bill_Income.value)) {
				//收到汇票
				 JinDieRecord record_billIncome=createRecord(origin);
				 record_billIncome.set借方金额(origin.getBill_income());
				 record_billIncome.set科目代码(billAccountNum);
				 
				 //应收账款
				 JinDieRecord record_receive=createRecord(origin);
				 record_receive.set贷方金额(origin.getBill_income());
				 
				 records.addAll(List.of(record_billIncome,record_receive));
			}else {
				//应付账款
				JinDieRecord record_pay=createRecord(origin);
				record_pay.set借方金额(origin.getBill_pay());
				
				//背书转发汇票
				JinDieRecord record_billPay=createRecord(origin);
				record_billPay.set贷方金额(origin.getBill_pay());
				record_billPay.set科目代码(billAccountNum);
				 records.addAll(List.of(record_pay,record_billPay));
			}
			 
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

}
