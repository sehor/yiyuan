package yiyuan.core.accountClassification;

import java.util.List;

public interface AccClaService {
    AccCla addAccCla(AccCla accCla);

    AccCla getAccCla(String id);

    AccCla updateAccCla(AccCla accCla);

    void deleteAccCla(AccCla accCla);

    void deleteAccCla(String id);

    AccClaPresentation createAccClaPresentation(AccCla accCla);

    String showFullPath(AccCla accCla);
}