package yiyuan.JinDie.Classification;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.utils.msofficetools.BeanFromFile;

@Service
public class ClassficationServiceImpl implements ClassficationService {
	@Autowired
	ClassficationRepository repository;

	@Override
	public Classfication addClassfication(Classfication classfication) {
        classfication.setCompanyName(createMutilName(classfication));
		return repository.save(classfication);
	}

	@Override
	public Classfication getClassfication(String id) {
		return repository.findById(id).get();
	}

	@Override
	public Classfication updateClassfication(Classfication classfication) {
		return repository.save(classfication);
	}

	@Override
	public void deleteClassfication(Classfication classfication) {
		repository.delete(classfication);
	}

	@Override
	public void deleteClassfication(String id) {
		repository.deleteById(id);
	}

	@Override
	public List<Classfication> fromExcel(File file) {

		return BeanFromFile.fromFile(file, Classfication.class);
	}

	@Override
	public void SaveAll(List<Classfication> classfications) {
		// TODO Auto-generated method stub
		List<Classfication> savies=repository.saveAll(classfications);
		savies.forEach(e->e.setMutilName(createMutilName(e)));
		repository.saveAll(savies);
	}

	@Override
	@Cacheable("classfication")
	public Classfication getByName(String 名称) {
		System.out.println("调用方法:getByName(名称)");
		List<Classfication> list = repository.findBy名称(名称);
		if (list.size() <= 0)
			return null;
		return list.get(0);
	}

	public String getNumber(String name, String companyName, String rootName) {

		Classfication root = getByNameAndCompanyName(rootName, companyName);
		if (root == null)
			return "未找到";
		List<Classfication> list = repository.findBy名称AndCompanyName(name, companyName);
		for (Classfication classfication : list) {
			if (classfication.get编码().startsWith(root.get编码())) {
				return classfication.get编码();
			}
		}
		return "未找到";
	}

	@Override
	public Classfication getByNameAndCompanyName(String name, String companyName) {
		// TODO Auto-generated method stub
		List<Classfication> list = repository.findBy名称AndCompanyName(name, companyName);
		if (list.size() <= 0)
			return null;
		return list.get(0);
	}

	@Override
	public List<Classfication> getAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public List<Classfication> getAllByName(String name, String companyName) {

		return repository.findBy名称AndCompanyName(name, companyName);

	}

	@Override
	public String getNumber(List<Classfication> classfications, String companyName, String rootAccountName) {
		Classfication root = getByNameAndCompanyName(rootAccountName, companyName);
		if (root == null)
			return null;
		for (Classfication classfication : classfications) {

			if (classfication.get编码().startsWith(root.get编码())) {
				return classfication.get编码();
			}
		}
		return null;

	}

	public boolean isReceiveable(Origin origin, List<Classfication> classfications) {

		return false;
	}

	@Override
	public String createMutilName(Classfication classfication) {
		// TODO Auto-generated method stub
		String parentName = "";
		String sonNum = classfication.get编码();
		String num = sonNum.substring(0, sonNum.length() - 2); // 编码格式 xxxx.xx.xx.xx
		List<Classfication> classfications = repository.findBy编码(num);
		if (classfications.size() < 1)
			return classfication.get名称();
		parentName = classfications.get(0).get名称();
		return parentName + "-" + classfication.get名称();
	}

	

	@Override
	public String getNumByMutilName(String mutilName,String companyName){
		List<Classfication> classfications=repository.findByMutilNameAndCompanyName(mutilName,companyName);
		if(classfications.size()<=0) return "未找到";
		return classfications.get(0).get编码();
	}

	@Override
	public List<Classfication> getByCompanyname(String companyName) {
		// TODO Auto-generated method stub
		return repository.findByCompanyName(companyName);
	}

}