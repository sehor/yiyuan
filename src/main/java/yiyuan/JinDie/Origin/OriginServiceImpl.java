package yiyuan.JinDie.Origin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import excelTool.reader.Reader;
import jinDieEntryXLS.beans.RawInfo;
import jinDieEntryXLS.helper.TypeEnum;
import yiyuan.utils.Tool;

@Service
public class OriginServiceImpl implements OriginService {
	@Autowired
	OriginRepository repository;

	@Override
	public Origin addOrigin(Origin origin) {
		return repository.save(origin);
	}

	@Override
	public Origin getOrigin(String id) {
		return repository.findById(id).get();
	}

	@Override
	public Origin updateOrigin(Origin origin) {
		return repository.save(origin);
	}

	@Override
	public void deleteOrigin(Origin origin) {
		repository.delete(origin);
	}

	@Override
	public void deleteOrigin(String id) {
		repository.deleteById(id);
	}

	@Override
	public List<Origin> getAll() {
		// TODO Auto-generated method stub

		return repository.findAll();
	}

	@Override
	public List<Origin> saveAll(List<Origin> origins) {
		// TODO Auto-generated method stub
		return repository.saveAll(origins);
	}

	@Override
	public List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub

		return repository.findInPeriod(companyName, begin, end);
	}

	@Override
	public List<Origin> deleteByCompanyName(String companyName) {
		// TODO Auto-generated method stub
		return repository.deleteByCompanyName(companyName);
	}

	@Override
	public double findSalary(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return repository.findSalary(companyName, begin, end);
	}

	@Override
	public double findPersonSecurity(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return repository.findPersonSecurity(companyName, begin, end);
	}

	@Override
	public double findPersonTax(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return repository.findPersonTax(companyName, begin, end);
	}

	@Override
	public double findPersonFund(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return repository.findPersonFund(companyName, begin, end);
	}

	@Override
	public List<Origin> getFromFile(File file, String companyName) {

		List<Origin> origins = new ArrayList<>();

		try {
			InputStream input = new FileInputStream(file);
			Workbook workbook = file.getName().toLowerCase().endsWith("xlsx") ? new XSSFWorkbook(input)
					: new HSSFWorkbook(input);

			Reader<Origin> reader = new OriginReader(Origin.class, workbook, companyName);

			origins = reader.creatBeans();
			workbook.close();
			input.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return origins;
	}

	@Override
	public List<Origin> getFromFilePath(String filePath, String companyName) {
		return getFromFile(new File(filePath), companyName);
	}

	@Override
	public List<RawInfo> originToSupClass(List<Origin> origins) {
		// TODO Auto-generated method stub
		List<jinDieEntryXLS.beans.RawInfo> origins2 = new ArrayList<>();
		for (Origin origin : origins) {
			origins2.add((RawInfo) origin);
		}
		return origins2;
	}

	@Override
	public List<RawInfo> getExpenseOriginFromFile(File file, String companyName) {
		// TODO Auto-generated method stub

		StringBuilder builder = new StringBuilder();
		try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
			char[] buffer=new char[1000];
			
			while (reader.read(buffer) != -1) {
				builder.append(buffer);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<jinDieEntryXLS.beans.RawInfo> origins=new ArrayList<>();
		String[] lines = builder.toString().split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].trim().length() < 1)
				continue;
			RawInfo origin = new RawInfo();
			double amount_sum = 0;
			for (String str : Tool.getMatchers("(\\d+|\\d+\\.\\d+)元", lines[i])) {
				amount_sum += Double.valueOf(str);
			}
			origin.setInvoice_amount(amount_sum);

			double tax_sum = 0;
			for (String str : Tool.getMatchers("税(\\d+元|\\d+\\.\\d+)", lines[i])) {
				tax_sum += Double.valueOf(str);
			}
			origin.setInvoice_tax(tax_sum);

			
			origin.setBrief(Tool.getMatcher("摘要([\u4E00-\u9FA5\\w]*)", lines[i]));
			
			
			String dateStr = Tool.getMatcher("日期(\\d+)", lines[i]);
			LocalDate date = LocalDate.parse(dateStr.isBlank()?"19000101":dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")); //没找到日期，默认1900-01-01
			origin.setOccur_date(date);

			origin.setRelative_account(Tool.getMatcher("科目([\u4E00-\u9FA5\\w\\-]*)", lines[i]));
			
			origin.setType(TypeEnum.Expense.value);
			origin.setCompanyName(companyName);
			
			
			origins.add(origin);
		}

		return origins;
	}

	@Override
	public List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end, String typeString) {
		// TODO Auto-generated method stub
		return repository.findInPeriod(companyName, begin, end, typeString);
	}

}