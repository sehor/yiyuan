package yiyuan.JinDie.Classification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

	@PutMapping("/update")
	public Classfication update(@RequestBody Classfication classfication) {
		return service.updateClassfication(classfication);
	}

	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {

		service.deleteClassfication(id);
		return "delete classfication by id :" + id;
	}
	
	@GetMapping("/fromExcel/{companyName}")
	public List<Classfication> fromExcel(@PathVariable("companyName") String companyName){
		  File file=new File("C:\\Users\\pzr\\Desktop\\创和科目列表.xlsx");
		  List<Classfication> classfications=service.fromExcel(file);	
		  for(Classfication classfication:classfications) {
			   classfication.setCompanyName(companyName);
			   classfication.setId(classfication.get编码());
		  }
		  service.SaveAll(classfications);
		  return classfications;
	}
		
	@GetMapping("/all")
	public List<Classfication> getAll(){
		
		return service.getAll();
		
	}
	
	}