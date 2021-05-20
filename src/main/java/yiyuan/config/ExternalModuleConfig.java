package yiyuan.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jinDieEntryXLS.JinDieEntryXLS;
import jinDieEntryXLS.beans.RawInfo;
import jinDieEntryXLS.helper.TypeEnum;
import jinDieEntryXLS.service.data.DataService;
import yiyuan.JinDie.Classification.ClassficationService;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.utils.CompanyProperties;
import yiyuan.utils.Tool;

@Configuration
public class ExternalModuleConfig {
	@Autowired
	ClassficationService classificationService;
	@Autowired
	CompanyProperties companyProperties;
	@Autowired
	OriginService originService;

	@Bean
	public DataService jindieDataService() {

		DataService service = new DataService() {

			@Override
			public String getCompanyName() {
				// TODO Auto-generated method stub
				return Tool.getCurrentCompanyName();
			}

			@Override
			public String getAccountNumByMutilName(String mutilAccountName, String companyName) {
				// TODO Auto-generated method stub
				return classificationService.getNumByMutilName(mutilAccountName, companyName);
			}

			@Override
			public boolean isContainKeyWord(String text, String keyWord) {
				// TODO Auto-generated method stub
				return isContainKeyword(text, keyWord);
			}

			@Override
			public boolean needToBeMerged(RawInfo origin) {
				// TODO Auto-generated method stub

				List<String> typeNames = List.of(TypeEnum.Bank_Pay_BankFee.value, TypeEnum.Bank_Pay_Salary.value,
						TypeEnum.BanK_DefChargePerson.value, TypeEnum.Issue_Invoice.value,
						TypeEnum.Receive_Invoice.value);
				for (String typeName : typeNames) {
					if (origin.getType().equals(typeName))
						return true;
				}

				return false;
			}

			@Override
			public String mergeMark(RawInfo origin) {
				// TODO Auto-generated method stub

				return origin.getType() + origin.getRelative_account_number();
			}

			@Override
			public double findPersonSecurity(String companyName, LocalDate beginDay, LocalDate lastDay) {
				// TODO Auto-generated method stub
				return originService.findPersonSecurity(companyName, beginDay, lastDay);
			}

			@Override
			public double findSalary(String companyName, LocalDate beginDay, LocalDate lastDay) {
				// TODO Auto-generated method stub
				return originService.findSalary(companyName, beginDay, lastDay);
			}

			@Override
			public double findPersonTax(String companyName, LocalDate beginDay, LocalDate lastDay) {
				// TODO Auto-generated method stub
				return originService.findPersonTax(companyName, beginDay, lastDay);
			}

			@Override
			public double findPersonFund(String companyName, LocalDate beginDay, LocalDate lastDay) {
				// TODO Auto-generated method stub
				return originService.findPersonFund(companyName, beginDay, lastDay);
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

			@Override
			public String findRepresentiveOfCompany(String companyName) {
				// TODO Auto-generated method stub
				return companyProperties.getCompanies().get(companyName).get("defChargePerson");
			}

		};

		return service;
	}



	@Bean
	public JinDieEntryXLS jieDieEntryXLS() {

		return new JinDieEntryXLS(jindieDataService());
	}
	

}