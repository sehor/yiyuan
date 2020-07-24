package yiyuan.externalModule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jinDieEntryXLS.beans.Origin;
import jinDieEntryXLS.helper.TypeEnum;
import jinDieEntryXLS.service.data.DataService;
import yiyuan.JinDie.Classification.ClassficationService;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.utils.CompanyProperties;
import yiyuan.utils.Tool;

@Service
public class JinDieEntryDataService implements DataService {
	@Autowired
	ClassficationService classificationService;
	@Autowired
	CompanyProperties companyProperties;
	@Autowired
	OriginService originService;

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
	public boolean needToBeMerged(Origin origin) {
		// TODO Auto-generated method stub
		
		List<String> typeNames = List.of(TypeEnum.Bank_Pay_BankFee.value, TypeEnum.Bank_Pay_Salary.value,
				TypeEnum.BanK_DefChargePerson.value,TypeEnum.Issue_Invoice.value,TypeEnum.Receive_Invoice.value);
        for(String typeName:typeNames) {
        	if(origin.getType().equals(typeName)) return true;
        }
		
		return false;
	}

	@Override
	public String mergeMark(Origin origin) {
		// TODO Auto-generated method stub
		
		return origin.getType()+origin.getRelative_account_number();
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
		return  originService.findPersonTax(companyName, beginDay, lastDay);
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

}
