package yiyuan.JinDie.Classification;

import java.io.File;
import java.util.List;



public interface ClassficationService {
	Classfication addClassfication(Classfication classfication);

	Classfication getClassfication(String id);

	Classfication updateClassfication(Classfication classfication);

	void deleteClassfication(Classfication classfication);
 
	
	void deleteClassfication(String id);
	
	List<Classfication> fromExcel(File file);
	
	void SaveAll(List<Classfication> classfications);
	
	Classfication getByName(String name);
	Classfication getByNameAndCompanyName(String name,String companyName);
	
	List<Classfication> getAll();
	
	public String getNumber(String name,String companyName,String rootName);
	
	public List<Classfication> getAllByName(String name,String companyName);
	public String getNumber(List<Classfication> classfications,String companyName,String rootAccountName);
}