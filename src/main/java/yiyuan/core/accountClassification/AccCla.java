package yiyuan.core.accountClassification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Document
@ApiModel(value = "", description = "")
public class AccCla {
    @Id
    @ApiModelProperty(value = "")
    private String id;


    @ApiModelProperty(value = "")
    private String name;


    @ApiModelProperty(value = "")
    private String parentId;


    @ApiModelProperty(value = "")
    private List<String> children;


    @ApiModelProperty(value = "")
    private LocalDate createDate;


    @ApiModelProperty(value = "")
    private Object otherInfo;


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