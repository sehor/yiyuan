package yiyuan.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.Classification.ClassficationRepository;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginRepository;

@RestController
@RequestMapping("/testDataBase")
public class TestDataBase {

	@Autowired
	OriginRepository originRepository;
	@Autowired
	ClassficationRepository claRepository;
	
	
	@GetMapping("/customers/{companyName}")
	public List<String> getCustomers(@PathVariable("companyName") String companyName){
		List<Origin> list=originRepository.findByCompanyNameAndType(companyName,OriginType.Issue_Invoice.value);
		List<String> listStr=new ArrayList<>();
		for(Origin origin:list) {
			 listStr.add(origin.getRelative_account());
		}
		Set<String> set=new HashSet<>(listStr);
		
		List<String> result=new ArrayList<>(set);
		
		return result;
	}
	
	@GetMapping("/supplier/{companyName}")
	public List<String> getSupplier(@PathVariable("companyName") String companyName){
		List<Origin> list=originRepository.findByCompanyNameAndType(companyName,OriginType.Receive_Invoice.value);
		List<String> listStr=new ArrayList<>();
		for(Origin origin:list) {
			 listStr.add(origin.getRelative_account());
		}
		Set<String> set=new HashSet<>(listStr);
		
		List<String> result=new ArrayList<>(set);
		
		return result;
	}
	
	
}
