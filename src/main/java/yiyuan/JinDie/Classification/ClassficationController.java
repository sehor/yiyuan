package yiyuan.JinDie.Classification;

import io.swagger.annotations.Api;
import yiyuan.utils.Tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import excelTool.ExcelTool;

@Api(tags = "Classfication 接口测试")
@RestController
@RequestMapping("/Classfication")
public class ClassficationController {
	@Autowired
	ClassficationService service;

	@PostMapping("/add")
	public Classfication add(@RequestBody Classfication classfication) {

		return service.addClassfication(classfication);
	}

	@GetMapping("/get/{id}")
	public Classfication getClassfication(@PathVariable(value = "id") String id) {
		return service.getClassfication(id);
	}
	
	@GetMapping("/getByName/{name}")
	public Classfication getClassficationByName(@PathVariable(value = "name") String name) {
		return service.getByName(name);
	}

	@PutMapping("/update")
	public Classfication update(@RequestBody Classfication classfication) {
		return service.updateClassfication(classfication);
	}

	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {

		service.deleteClassfication(id);
		return "delete classfication by id :" + id;
	}
	
	@GetMapping("/fromExcel")
	public List<Classfication> fromExcel(HttpServletResponse response){
		String filePath="D:/work/finace/"+Tool.getCurrentCompanyName()+"/"+Tool.getCurrentCompanyName()+"_科目列表.xls";
		File file=new File(filePath);
		if(!file.getName().contains(Tool.getCurrentCompanyName())) {
			PrintWriter pw;
			try {
				pw = response.getWriter();
				pw.write("error! the file:"+file.getName()+" seems not like the current username:"+Tool.getCurrentCompanyName());
				pw.flush();
				pw.close();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		  ExcelTool<Classfication> excelTool=new ExcelTool<>();

		  List<Classfication> classfications=excelTool.readFromFileName(Classfication.class, filePath);
		  
		  for(Classfication classfication:classfications) {
			   classfication.setCompanyName(Tool.getCurrentCompanyName());
			   classfication.setId(classfication.get编码());
		  }
		  service.SaveAll(classfications);
		  return classfications;
	}
	

	@GetMapping("/all")
	public List<Classfication> getAll(){
		
		return service.getAll();
		
	}
	@GetMapping("/all/{companyName}")
	public List<Classfication> getAll(@PathVariable("companyName") String companyName){
		
		return service.getAll().stream().filter(e->e.getCompanyName().equalsIgnoreCase(companyName)).collect(Collectors.toList());
		//return service.getAll();
		
	}
	
	}