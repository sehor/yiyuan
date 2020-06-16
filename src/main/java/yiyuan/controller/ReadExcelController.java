package yiyuan.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.other.FileStructure;
import yiyuan.other.SocialSecurityCllector;
import yiyuan.other.Visitor;
import yiyuan.utils.msofficetools.DefaultBeansToXLSTransform;

@RestController
@RequestMapping("/readxlsfile")
public class ReadExcelController {

	
	@GetMapping("/socialSecurity")
	public List<Origin> readSecurityFiles(@RequestParam("companyName") String companyName){
		FileStructure fileStructure=new FileStructure("D:\\work\\finace\\创和\\社保");
		SocialSecurityCllector visitor=new SocialSecurityCllector();
		fileStructure.handle(visitor);
		List<Origin> origins=visitor.getRecords();
		origins.forEach(e->e.setCompanyName(companyName));
		
		new DefaultBeansToXLSTransform<Origin>(Origin.class).writeToFile("C:\\Users\\pzr\\Desktop\\security-"+companyName+".xls", origins);
		
		return origins;
	}
}
