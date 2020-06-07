package yiyuan.utils.msofficetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
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
	ClassficationService classfictionService;
	@Autowired
	OriginService originService;

	private final float VTARate = 0.13f;
	private final float CJ_Tax_Rate = 0.07f;
	private final float JY_Tax_Rate = 0.03f;
	private final float DFJY_Tax_Rate = 0.02f;
	private String companyName = "未指点公司名称";

	@Override
	public OriginProcess setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	@Override
	public List<JinDieRecord> proccessToRecord(List<Origin> origins, LocalDate date) {
		// TODO Auto-generated method stub

		this.map.clear();// 每个会计期间初始化map

		List<JinDieRecord> records = new ArrayList<>();
		records.addAll(proccessBankToRecord(origins, date));
		/*
		 * records.addAll(processIssueInvoiceToRecords(origins, date));
		 * records.addAll(processReceiveInvoiceToRecords(origins, date));
		 * records.addAll(processHandleVTAToRecords(origins, date));
		 * records.addAll(processSalaryToRecords(origins, date));
		 */
		return records;
	}

	@Override
	public List<Origin> preProcessOrigin(List<Origin> origins) {
		// TODO Auto-generated method stub
		origins = processTypeOfBank(origins); // 先处理银行的origin
		origins = groupByRelatedAccount(origins); // 先合并同一Type并且relative_account相同的origins, 比如开出或收到的发票，银行费用等
		return origins;
	}

	private Map<String, List<Integer>> map = new HashMap<>();
	@Autowired
	ClassficationService classficationService;

	private List<Origin> processTypeOfBank(List<Origin> orings) {

		for (Origin origin : orings) {

			if (!origin.getType().contains("Bank"))
				continue;
			origin.setAmout(origin.getBank_income() + origin.getBank_pay());
			origin.setBrief(linkBrief(origin.getBrief(), origin.getBank_brief1(), origin.getBank_brief2()));
			Classfication classfication = classficationService.getByName(origin.getRelative_account());
			String rootNum = "";
			if (classfication != null) {
				origin.setRelative_account_number(classfication.get编码());
				rootNum = classfication.get编码().substring(0, 4); // 一级编码
			}
			// 银行收款事项
			if (origin.getBank_income() != 0) {

				if (rootNum.equals("1122")) { // 应收账款
					origin.setType(OriginType.Bank_Income_Receivable.value);

				} else if (rootNum.equals("1221")) { // 其他应收款
					origin.setType(OriginType.Bank_Income_OtherReceivable.value);
				} else if (rootNum.equals("2241")) { // 其他应付款
					origin.setType(OriginType.Bank_Income_OtherPayable.value);
				} else if (origin.getBrief().contains("结息")) {
					origin.setType(OriginType.Bank_Income_Interest.value);
				} else {

					origin.setType(OriginType.Bank_Income_Other.value);
				}

			}

			// 银行付款事项
			else if (origin.getBank_pay() != 0) {

				if (rootNum.equals("2202")) { // 应付账款
					origin.setType(OriginType.Bank_Pay_Payable.value);
				} else if (origin.getBrief().contains("工资")) { // 工资

					origin.setType(OriginType.Bank_Pay_Salary.value);
				} else if (origin.getBrief().contains("报销")) { // 报销，默认其他应付账款-法人
					origin.setType(OriginType.Bank_Pay_Expensive.value);
					
				}

				else if (origin.getBrief().contains("缴税") || origin.getBrief().contains("协议扣税")) { // 缴税
					origin.setType(OriginType.Bank_Pay_Tax.value);
				} else if (origin.getBrief().contains("社保") || origin.getBrief().contains("社会保险")) { // 缴税
					origin.setType(OriginType.Bank_Pay_SocialSecurity.value);
				}

				else if (origin.getBrief().contains("收费项目") || origin.getBrief().contains("短信服务费")
						|| origin.getBrief().contains("手续费")) { // 银行费用

					origin.setType(OriginType.Bank_Pay_BankFee.value);
					origin.setRelative_account("手续费"); // 手续费
				} else if (rootNum.endsWith("2241")) { // 其他应付账款
					origin.setType(OriginType.Bank_Pay_OtherPayable.value);
				} else {

					origin.setType(OriginType.Bank_Pay_OtherPayable.value);
				}
			}

		}

		return orings;

	}

	private List<JinDieRecord> proccessBankToRecord(List<Origin> origins, LocalDate date) {

		List<JinDieRecord> jinDieRecords = new ArrayList<>();

		for (Origin origin : origins) {
			if (!origin.getType().contains("Bank"))
				continue;
			List<Integer> list = getEntryNumAndItemNum(origin.getType());
			int entryNum = list.get(0);
			int itemNum = list.get(1);

			JinDieRecord record = new JinDieRecord();
			JinDieRecord record2 = new JinDieRecord();

			record.set凭证字("记");
			record.set凭证号(entryNum);
			record.set日期(date);
			record.set分录序号(itemNum);
			list.set(1, ++itemNum); // 保存回map

			record2.set凭证字("记");
			record2.set凭证号(entryNum);
			record2.set日期(date);
			record2.set分录序号(itemNum);
			record2.set科目名称(origin.getRelative_account() != null ? origin.getRelative_account() : "未找到");
			list.set(1, ++itemNum); //

			switch (origin.getType()) {
			case "Bank_Income_Receivable":
				record.set借方金额(origin.getAmout());
				record.set摘要("收款-货款");
				record.set科目代码("100201"); // 默认的基本户

				record2.set贷方金额(origin.getAmout());
				record2.set摘要("收款-货款");
				record2.set科目代码(origin.getRelative_account_number());
				break;

			case "Bank_Income_OtherReceivable":
				record.set借方金额(origin.getAmout());
				record.set摘要("收款-其他");
				record.set科目代码("100201"); // 默认的基本户

				record2.set贷方金额(origin.getAmout());
				record2.set摘要("收款-其他");
				record2.set科目代码(
						origin.getRelative_account_number() != null ? origin.getRelative_account_number() : "未处理");
				break;
			case "Bank_Income_OtherPayable":
				record.set借方金额(origin.getAmout());
				record.set摘要("收款-其他");
				record.set科目代码("100201"); // 默认的基本户

				record2.set贷方金额(origin.getAmout());
				record2.set摘要("收款-其他");
				record2.set科目代码(
						origin.getRelative_account_number() != null ? origin.getRelative_account_number() : "未处理");
				break;
			case "Bank_Income_Interest":
				record.set借方金额(origin.getAmout());
				record.set摘要("存款结息");
				record.set科目代码("100201"); // 默认的基本户

				record2.set贷方金额(origin.getAmout());
				record2.set摘要("存款结息");
				record2.set科目代码("560302");
				break;
			case "Bank_Pay_Payable":
				record.set贷方金额(origin.getAmout());
				record.set摘要("付款-货款");
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("付款-货款");
				record2.set科目代码(origin.getRelative_account_number());
				break;
			case "Bank_Pay_Salary":
				record.set贷方金额(origin.getAmout());
				record.set摘要("发放工资");
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("发放工资");
				record2.set科目代码("221101");
				break;
			case "Bank_Pay_BankFee":
				record.set贷方金额(origin.getAmout());
				record.set摘要("银行费用");
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("银行费用");
				record2.set科目代码("560303");
				break;
			case "Bank_Pay_OtherPayable":
				record.set贷方金额(origin.getAmout());
				record.set摘要("其他支出：" + origin.getBrief());
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("其他支出：" + origin.getBrief());
				record2.set科目代码(
						origin.getRelative_account_number() != null ? origin.getRelative_account_number() : "未找到");
				break;
			case "Bank_Pay_Expensive":
				record.set贷方金额(origin.getAmout());
				record.set摘要("报销费用");
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("报销费用");
				record2.set科目代码("224101"); // 其他应付款-法人
				break;

			case "Bank_Pay_SocialSecurity":
				record.set贷方金额(origin.getAmout());
				record.set摘要("缴纳社保");
				record.set科目代码("100201"); // 默认的基本户

				record2.set借方金额(origin.getAmout());
				record2.set摘要("缴纳社保");
				record2.set科目代码("560216");
				break;

			case "Bank_Pay_Tax":
				record.set贷方金额(origin.getAmout());
				record.set摘要("缴纳税费");
				record.set科目代码("100201"); // 默认的基本户

				if (origin.getBrief().contains("增值税及附加税")) {
					double vta = origin.getAmout() / 1.12; // 增值税

					record2.set借方金额(vta);
					record2.set科目代码("222102"); // 未交增值税
					record2.set摘要("缴纳税费");

					JinDieRecord record4 = new JinDieRecord();
					record4.set摘要("缴纳税费");
					record4.set凭证字("记");
					record4.set日期(date);
					record4.set借方金额((vta * 0.07));
					record4.set科目代码("222108"); // 城建税
					record4.set凭证号(entryNum);
					record4.set分录序号(itemNum);
					list.set(1, ++itemNum); // 保存回map

					JinDieRecord record5 = new JinDieRecord();
					record5.set摘要("缴纳税费");
					record5.set凭证字("记");
					record5.set日期(date);
					record5.set借方金额(vta * 0.03);
					record5.set科目代码("222113"); // 教育费
					record5.set凭证号(entryNum);
					record5.set分录序号(itemNum);
					list.set(1, ++itemNum); // 保存回map

					JinDieRecord record6 = new JinDieRecord();
					record6.set摘要("缴纳税费");
					record6.set凭证字("记");
					record6.set日期(date);
					record6.set借方金额(vta * 0.02);
					record6.set科目代码("222114"); // 教育费附加
					record6.set凭证号(entryNum);
					record6.set分录序号(itemNum);
					list.set(1, ++itemNum); // 保存回map

					jinDieRecords.addAll(List.of(record4, record5, record6));
				} else if (origin.getBrief().contains("增值税")) {

					record2.set借方金额(origin.getAmout());
					record2.set科目代码("222102"); // 增值税
					record2.set摘要("缴纳税费");
				} else if (origin.getBrief().contains("附加税")) {

					double amout = origin.getAmout();

					record2.set摘要("缴纳税费");
					record2.set借方金额((amout * 7 / 12));
					record2.set科目代码("222108"); // 城建税

					JinDieRecord record3 = new JinDieRecord();
					record3.set摘要("缴纳税费");
					record3.set凭证字("记");
					record3.set日期(date);
					record3.set借方金额(amout * 3 / 12);
					record3.set科目代码("222113"); // 教育费
					record3.set凭证号(entryNum);
					record3.set分录序号(itemNum);
					list.set(1, ++itemNum); // 保存回map

					JinDieRecord record4 = new JinDieRecord();
					record4.set摘要("缴纳税费");
					record4.set凭证字("记");
					record4.set日期(date);
					record4.set借方金额(amout * 2 / 12);
					record4.set科目代码("222114"); // 教育费附加
					record4.set凭证号(entryNum);
					record4.set分录序号(itemNum);
					list.set(1, ++itemNum); // 保存回map

					jinDieRecords.addAll(List.of(record3, record4));
				} else if (origin.getBrief().contains("企业所得税")) {

					record2.set借方金额(origin.getAmout());
					record2.set科目代码("5801"); // 企业所得税
					record2.set摘要("缴纳税费");
				} else if (origin.getBrief().contains("个人所得税")) {

					record2.set借方金额(origin.getAmout());
					record2.set科目代码("222112"); // 企业所得税
					record2.set摘要("缴纳税费");
				} else {

					record2.set借方金额(origin.getAmout());
					record2.set科目名称("归集管理费用的税金"); // 归集管理费用的税金
					record2.set摘要("缴纳税费");
				}

				break;

			case "No_Handled":
				record.set贷方金额(origin.getAmout());
				record.set摘要("未处理: " + origin.getBrief());

				record2.set借方金额(origin.getAmout());
				record2.set摘要("未处理: " + origin.getBrief());

				break;
			}
			map.put(origin.getType(), list); // 更新map ，itemNum变了

			jinDieRecords.addAll(List.of(record, record2));

		}

		return jinDieRecords;

	}

	private List<JinDieRecord> processIssueInvoiceToRecords(List<Origin> origins, LocalDate date) {

		List<Origin> list = origins.stream().filter(e -> e.getType().equals("Issue_Invoice"))
				.collect(Collectors.toList());
		List<JinDieRecord> records = new ArrayList<>();
		if (list.size() <= 0)
			return records;

		List<Integer> state = getEntryNumAndItemNum("Issue_Invoice");
		double tax = 0, amount = 0;

		for (Origin origin : list) {

			// System.out.println("issue: " + origin.getRelative_account());
			JinDieRecord record = new JinDieRecord();
			tax += origin.getInvoice_tax();
			amount += origin.getInvoice_amount();

			record.set日期(date);
			record.set凭证字("记");
			record.set凭证号(state.get(0));
			record.set摘要("开出发票，确认收入");

			record.set分录序号(state.get(1));
			state.set(1, state.get(1) + 1);

			record.set科目名称(origin.getRelative_account());
			record.set科目代码(getAccountNum(origin.getRelative_account())); //
			record.set借方金额(origin.getInvoice_tax() + origin.getInvoice_amount());

			records.add(record);
		}

		JinDieRecord record1 = new JinDieRecord();
		record1.set日期(date);
		record1.set凭证字("记");
		record1.set凭证号(state.get(0));
		record1.set分录序号(state.get(1));
		state.set(1, state.get(1) + 1);
		record1.set科目代码("5001"); // 主营业务收入
		record1.set贷方金额(amount);
		record1.set摘要("开出发票，确认收入");
		record1.set科目名称("主营业务收入");

		JinDieRecord record2 = new JinDieRecord();
		record2.set日期(date);
		record2.set凭证字("记");
		record2.set凭证号(state.get(0));
		record2.set分录序号(state.get(1));
		state.set(1, state.get(1) + 1);
		record2.set科目代码("22210106"); // 销项税额
		record2.set贷方金额(tax);
		record2.set摘要("开出发票，确认收入");
		record2.set科目名称("销项税额");

		records.addAll(List.of(record1, record2));

		this.map.put("Issue_Invoice", state); // 更新map

		return records;

	}

	@SuppressWarnings("deprecation")
	private List<JinDieRecord> processReceiveInvoiceToRecordsByType(List<Origin> origins, LocalDate date, String type) {

		List<JinDieRecord> records = new ArrayList<>();
		if (origins.size() < 1)
			return records;
		List<Integer> state = getEntryNumAndItemNum(type);
		double tax = 0, amount = 0;
		for (Origin origin : origins) {
			// System.out.println("receive: " + origin.getRelative_account());
			JinDieRecord record = new JinDieRecord();
			tax += origin.getInvoice_tax();
			amount += origin.getInvoice_amount();

			record.set日期(date);
			record.set凭证字("记");
			record.set凭证号(state.get(0));

			record.set分录序号(state.get(1));
			state.set(1, state.get(1) + 1);

			record.set摘要("收到发票，确认费用");

			String accountName = getAccountNum(origin.getRelative_account());
			// System.out.println("accountNum: " + accountName);
			record.set科目代码(accountName); //
			record.set科目名称(origin.getRelative_account());
			record.set贷方金额(origin.getInvoice_tax() + origin.getInvoice_amount());

			records.add(record);
		}

		JinDieRecord record1 = new JinDieRecord();
		record1.set日期(date);
		record1.set凭证字("记");
		record1.set凭证号(state.get(0));
		record1.set分录序号(state.get(1));
		state.set(1, state.get(1) + 1);
		record1.set科目代码(companyProperties.getCompanies().get(this.companyName).get("cost")); // 成本科目

		record1.set借方金额(amount);
		record1.set摘要("收到发票，确认费用");

		JinDieRecord record2 = new JinDieRecord();
		record2.set日期(date);
		record2.set凭证字("记");
		record2.set凭证号(state.get(0));
		record2.set分录序号(state.get(1));
		state.set(1, state.get(1) + 1);
		record2.set科目代码("22210101"); // 进项税额
		record2.set借方金额(tax);
		record2.set摘要("收到发票，确认费用");

		records.addAll(List.of(record1, record2));

		this.map.put(type, state); // 更新map

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
		List<Integer> state = getEntryNumAndItemNum("Handle_VTA");
		for (Origin origin : list) {
			System.out.println("handle VTA: " + origin.getRelative_account());
			for (int i = 0; i <= 5; i++) {
				JinDieRecord record = new JinDieRecord();
				record.set日期(date);
				record.set凭证字("记");
				record.set凭证号(state.get(0));
				record.set摘要("月末增值税处理");

				record.set分录序号(state.get(1));
				state.set(1, state.get(1) + 1);
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

		this.map.put("Handle_VTA", state); // 更新map
		return records;

	}

	private List<JinDieRecord> processSalaryToRecords(List<Origin> origins, LocalDate date) {

		List<Origin> list = origins.stream().filter(e -> e.getType().equals("Accrued_Salary"))
				.collect(Collectors.toList());
		List<JinDieRecord> records = new ArrayList<>();
		if (list.size() <= 0)
			return records;
		List<Integer> state = getEntryNumAndItemNum("Accrued_Salary");
		for (Origin origin : list) {
			// System.out.println("salary: " + origin.getSalary_payable());
			for (int i = 0; i >= 4; i++) {
				JinDieRecord record = new JinDieRecord();
				record.set日期(date);
				record.set凭证字("记");
				record.set凭证号(state.get(0));
				record.set摘要("计提工资");

				record.set分录序号(state.get(1));
				state.set(1, state.get(1) + 1);
				records.add(record);
			}
			records.get(0).set借方金额(origin.getBasicSalary());// 未交增值税
			records.get(0).set科目代码(getAccountNum("工资费用"));
			// records.get(0).set科目代码(getAccountNum("研发费用"));

			records.get(1).set贷方金额(origin.getSalary_security());// 转出未交增值税
			records.get(1).set科目代码(getAccountNum("应交社保"));

			records.get(2).set借方金额(origin.getSalary_funds()); // 税金及附加
			records.get(2).set科目代码(getAccountNum("应交公积金"));

			records.get(3).set贷方金额(origin.getSalary_personTax());// 城建税
			records.get(3).set科目代码(getAccountNum("应交个人所得税"));

			records.get(4).set贷方金额(origin.getSalary_payable());// 教育费
			records.get(4).set科目代码(getAccountNum("应付工资"));
		}

		this.map.put("Accrued_Salary", state); // 更新map
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
	private List<Integer> getEntryNumAndItemNum(String type) {

		List<Integer> states = this.map.get(type);

		if (states == null) {
			List<Integer> list = new ArrayList<>();
			list.add(100 + map.size()); // 凭证号从100开始+map长度
			list.add(1);// 记录号从1开始
			this.map.put(type, list);
			return list;
		}

		return states;
	}

	private String linkBrief(String... briefs) {

		List<String> list = new ArrayList<>();
		for (String brief : briefs) {

			if (brief != null)
				list.add(brief);
		}

		return String.join(";", list);
	}

	private String getAccountNum(String accountName) {
		Classfication classfication = classficationService.getByNameAndCompanyName(accountName, this.companyName);
		if (classfication == null)
			return "未找到";
		return classfication.get编码();
	}

	private List<Origin> groupByRelatedAccount(List<Origin> origins) {

		for (OriginType type : OriginType.values()) {

			// 相同type-->
			List<Origin> sameTypeList = new ArrayList<>();
			sameTypeList = origins.stream().filter(e -> e.getType().contains(type.value)&&!e.getType().contains("Tax")).collect(Collectors.toList());  //税款不汇总
			if (sameTypeList.size() > 0) {

				Map<String, Origin> map = new HashMap<>(); // 存储相同relative_account的origin

				// 有相同relative_account的origin汇总到map
				for (int i = 0; i < sameTypeList.size(); i++) {
					Origin originL = sameTypeList.get(i); // 将要汇总到originM的
					Origin originM = map.get(originL.getRelative_account()); // 在map的汇总origin
					if (originM != null) {
						originM.setAmout(originM.getAmout() + originL.getAmout());
						originM.setInvoice_amount(originL.getInvoice_amount() + originM.getInvoice_amount());
						originM.setInvoice_tax(originM.getInvoice_tax() + originL.getInvoice_tax());
					} else if (originM == null) {
						map.put(originL.getRelative_account(), originL); // 如果没有，把第一个遇到的加到到map
					}

					// 从origins清空sameTypeList, 把map数据加上
					origins.removeAll(sameTypeList);
					origins.addAll(map.values());
				}
			}
			// <----相同type
		}

		return origins;
	}

}
