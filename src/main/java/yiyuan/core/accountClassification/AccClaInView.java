package yiyuan.core.accountClassification;

import java.util.ArrayList;
import java.util.List;

public class AccClaInView {
    private String name;
    private String nameZh;
    private String id;
    private String number;
    private AccCla accCla;
    private List<AccCla> children=new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccCla getAccCla() {
        return accCla;
    }

    public void setAccCla(AccCla accCla) {
        this.accCla = accCla;
    }

    public List<AccCla> getChildren() {
        return children;
    }

    public void setChildren(List<AccCla> children) {
        this.children = children;
    }
}
