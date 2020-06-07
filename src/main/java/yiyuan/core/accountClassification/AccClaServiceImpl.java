package yiyuan.core.accountClassification;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;

@Service
public class AccClaServiceImpl implements AccClaService {
    @Autowired
    AccClaRepository repository;

    @Override
    public AccCla addAccCla(AccCla accCla) {
        AccCla accCla1=new AccCla();
        accCla1.setCreateDate(LocalDate.now()); //设置创建时间

        //更新parent的children列表
        AccCla parent=repository.findById(accCla.getParentId()).orElse(null);

        if(parent!=null){
            accCla.setCategories(Map.copyOf(parent.getCategories())); //copy parent's categories

            accCla1=repository.save(accCla);
            parent.getChildren().add(accCla1.getId()); //add new child id
            repository.save(parent);
        }

        return accCla1;
    }

    @Override
    public AccCla getAccCla(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public AccCla updateAccCla(AccCla accCla) {
        return repository.save(accCla);
    }

    @Override
    public void updateByid(AccCla accCla, String id) {
         repository.updateById(accCla,id);
    }

    @Override
    public void deleteAccCla(AccCla accCla) {

        repository.delete(accCla);
        AccCla parent=repository.findById(accCla.getParentId()).orElse(null);
        if(parent==null) return;
         parent.getChildren().remove(accCla.getId());
         repository.save(parent);
    }

    @Override
    public void deleteAccCla(String id) throws NotFoundException {
         AccCla accCla=repository.findById(id).orElse(null);
         if(accCla==null)
             throw new NotFoundException("no such AccCla");
        deleteAccCla(accCla);
    }

    @Override
    public AccClaPresentation createAccClaPresentation(AccCla accCla,String type) {
        AccClaPresentation accClaPresentation = new AccClaPresentation(accCla);
        addChildren(accClaPresentation,type);
        return accClaPresentation;
    }

    @Override
    public String showFullPath(AccCla accCla) {

        Deque<String> deque = new LinkedList<>();
        while (accCla != null && !accCla.getId().equals("root")) {
            deque.push(accCla.getName());
            accCla = repository.findById(accCla.getParentId()).orElse(null);
        }

        return String.join(" - ", deque);

    }

    @Override
    public AccCla getAccClaByNameZh(String nameZh) {
        return repository.findByNameZh(nameZh);
    }

    @Override
    public List<AccCla> getAll() {
        List<AccCla> accClas = repository.findAll();
        accClas.sort(new Comparator<AccCla>() {  //按 number 排序 （升序）
            @Override
            public int compare(AccCla o1, AccCla o2) {
                int minLength= Math.min(o1.getNumber().length(), o2.getNumber().length());
                return Integer.parseInt(o1.getNumber().substring(0,minLength))
                        -Integer.parseInt(o2.getNumber().substring(0,minLength));
            }
        });

        return accClas;
    }

    private void addChildren(AccClaPresentation accClaPresentation,String type) {
        List<AccCla> children = repository.findByParentId(accClaPresentation.getId());
        children.forEach(child -> {
            if(type.equalsIgnoreCase("all")||type.equalsIgnoreCase(child.getCategories().get("categoryInBalance"))){
                AccClaPresentation accClaPresentationChild = new AccClaPresentation(child);
                accClaPresentation.getChildren().add(accClaPresentationChild);
                addChildren(accClaPresentationChild,type);
            }
        });
    }

 @Override
    public void change(){
      repository.deleteByNumber(null);
    }

}
