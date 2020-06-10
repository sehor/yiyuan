package yiyuan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yiyuan.other.chache.TestCache;
import yiyuan.other.chache.TestElement;


@RestController
@RequestMapping("/testCache")
public class TestCacheController {

	@Autowired
	TestCache service;
	
	@GetMapping("/all")
	public List<TestElement> all(){
		
		return service.all();
	}
	
	@PostMapping("/save")
	public TestElement save(@RequestBody TestElement te) {
		
		 return service.save(te);
	}
	
	@GetMapping("/oneById/{id}")
	public TestElement getOneByid(@PathVariable("id") int id){
		return service.one(id);
	}
		 
	@GetMapping("/oneByName/{name}")
	public TestElement getOneByName(@PathVariable("name") String name){
		return service.one(name);
	}
		 
	
	@DeleteMapping("/delete/{id}")
	public boolean delete(@PathVariable("id") int id) {
		return service.delete(id);
	}
	
	
	@GetMapping("/like/{likeName}")
		
	public List<TestElement> getLike(@PathVariable("likeName") String likeName){
		
		return service.findLike(likeName);
	}
	
	@DeleteMapping("/delete/all")
	public void deletAll() {
		service.clearAll();
	}
	
	@GetMapping("/initTestData")
	public List<TestElement> intiTestDate(){
		return service.initData();
	}
}
