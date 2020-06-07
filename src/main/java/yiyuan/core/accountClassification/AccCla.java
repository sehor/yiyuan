package yiyuan.core.accountClassification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@ApiModel(value = "", description = "")
public class AccCla {
    @Id
    @ApiModelProperty(value = "")
    private String id;

    private String number;

    @ApiModelProperty(value = "")
    private String name;

    private String nameZh;

    private String forIndustry;

    private String level;

    private Map<String,String> categories=new HashMap<>();




    @ApiModelProperty(value = "")
    private String parentId;


    @ApiModelProperty(value = "")
    private List<String> children=new ArrayList<>();


    @ApiModelProperty(value = "")
    private LocalDate createDate;


    @ApiModelProperty(value = "")
    private Object otherInfo;





    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getForIndustry() {
        return forIndustry;
    }


    public void setForIndustry(String forIndustry) {
        this.forIndustry = forIndustry;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, String> categories) {
        this.categories = categories;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public List<String> getChildren() {
        return this.children;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public void setOtherInfo(Object otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Object getOtherInfo() {
        return this.otherInfo;
    }

}