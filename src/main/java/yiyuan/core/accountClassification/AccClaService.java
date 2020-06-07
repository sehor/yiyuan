package yiyuan.core.accountClassification;

import javassist.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface AccClaService {
    AccCla addAccCla(AccCla accCla);

    AccCla getAccCla(String id);

    AccCla updateAccCla(AccCla accCla);

    void updateByid(AccCla accCla,String id);

    void deleteAccCla(AccCla accCla);

    void deleteAccCla(String id) throws  NotFoundException;

    AccClaPresentation createAccClaPresentation(AccCla accCla,String type);

    String showFullPath(AccCla accCla);

    AccCla getAccClaByNameZh(String nameZh);

    List<AccCla> getAll();

    public void change();
}