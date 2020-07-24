package yiyuan.JinDie.Origin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import excelTool.ExcelTool;
import excelTool.reader.Reader;

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
	public List<Origin> getFromFile(File file,String companyName) {
	 
		List<Origin> origins=new ArrayList<>();

		try {
			InputStream input = new FileInputStream(file);
			Workbook workbook = file.getName().toLowerCase().endsWith("xlsx") ? new XSSFWorkbook(input)
					: new HSSFWorkbook(input);
			
			Reader<Origin> reader=new OriginReader(Origin.class,workbook, companyName);
			
			origins=reader.creatBeans();
			workbook.close();
			input.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return origins;
	}

	@Override
	public List<Origin> getFromFilePath(String filePath,String companyName) {
		return getFromFile(new File(filePath),companyName);
	}

	@Override
	public List<jinDieEntryXLS.beans.Origin> originToSubcalss(List<Origin> origins) {
		// TODO Auto-generated method stub
		List<jinDieEntryXLS.beans.Origin> origins2=new ArrayList<>();
		for(Origin origin:origins) {
			origins2.add((jinDieEntryXLS.beans.Origin) origin);
		}
		return origins2;
	}

}