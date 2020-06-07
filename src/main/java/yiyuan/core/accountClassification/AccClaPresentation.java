package yiyuan.core.accountClassification;

import java.util.ArrayList;
import java.util.List;

public class AccClaPresentation {
    private String value;
    private String label;
    private String id;
    private String nameZh;
    private String name;
    private String parentId;
    private String type;
    private AccCla originAccCla;
    private String number;
    private List<AccClaPresentation> children=new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public AccCla getOriginAccCla() {
        return originAccCla;
    }

    public void setOriginAccCla(AccCla originAccCla) {
        this.originAccCla = originAccCla;
    }

    public List<AccClaPresentation> getChildren() {
        return children;
    }

    public void setChildren(List<AccClaPresentation> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public AccClaPresentation(){

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public AccClaPresentation(AccCla accCla){
        this.setOriginAccCla(accCla);
        this.setId(accCla.getId());
        this.setName(accCla.getName());
        this.setNameZh(accCla.getNameZh());
        this.setType(accCla.getCategories().get("categoryInBalance"));
        this.setParentId(accCla.getParentId());
        this.setLabel(accCla.getNameZh());
        this.setNumber(accCla.getNumber());
        this.setValue(accCla.getId());
    }
}
