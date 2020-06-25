package yiyuan.JinDie.Classification;

import java.io.File;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;



public interface ClassficationService {
	@Cacheable("classfication")
	Classfication addClassfication(Classfication classfication);
	
	//@Cacheable("classfication")
	Classfication getClassfication(String id);

	@CachePut(value = "classfication",key="#result.id")
	Classfication updateClassfication(Classfication classfication);

	@CacheEvict("classfication")
	void deleteClassfication(Classfication classfication);
 
	@CacheEvict("classfication")
	void deleteClassfication(String id);
	
	List<Classfication> fromExcel(File file);
	
	void SaveAll(List<Classfication> classfications);
	
	Classfication getByName(String name);
	Classfication getByNameAndCompanyName(String name,String companyName);
	
	List<Classfication> getAll();
	
	//@Cacheable("classfication")
	public String getNumber(String name,String companyName,String rootName);
	
	//@Cacheable("classfication")
	public List<Classfication> getAllByName(String name,String companyName);
	
	//@Cacheable("classfication")
	public String getNumber(List<Classfication> classfications,String companyName,String rootAccountName);
	
	public String createMutilName(Classfication classfication);
	
	public void initMutilName(List<Classfication> classfications);
	
	public String getNumByMutilName(String mutilName,String companyName);
}