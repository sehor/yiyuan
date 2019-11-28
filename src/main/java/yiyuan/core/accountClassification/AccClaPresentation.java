package yiyuan.core.accountClassification;

import java.util.ArrayList;
import java.util.List;

public class AccClaPresentation {
    private String id;
    private String name;
    private String parentId;
    private String type;
    private List<AccClaPresentation> childObjects=new ArrayList<>();


    public List<AccClaPresentation> getChildObjects() {
        return childObjects;
    }

    public void setChildObjects(List<AccClaPresentation> childObjects) {
        this.childObjects = childObjects;
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

    public AccClaPresentation(){

    }


    public AccClaPresentation(AccCla accCla){
        this.setId(accCla.getId());
        this.setName(accCla.getName());
        this.setParentId(accCla.getParentId());
    }
}
