package yiyuan.core.accEntryItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDate;

@Document
@Table(name = "accEntryItem")
@ApiModel(value = "", description = "")
public class AccEntryItem {
    @Id
    @ApiModelProperty(value = "")
    private String id;


    @ApiModelProperty(value = "")
    private String EntryId;


    @ApiModelProperty(value = "")
    private int queueNum;


    @ApiModelProperty(value = "")
    private LocalDate date;


    @ApiModelProperty(value = "")
    private String brief;


    @ApiModelProperty(value = "")
    private String AccClaId;


    private String AccClaNum;
    
    
    @ApiModelProperty(value = "")
    private Float debit;


    @ApiModelProperty(value = "")
    private Float credit;


    @ApiModelProperty(value = "")
    private String creatorId;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setEntryId(String EntryId) {
        this.EntryId = EntryId;
    }

    public String getEntryId() {
        return this.EntryId;
    }

    public void setQueueNum(int queueNum) {
        this.queueNum = queueNum;
    }

    public int getQueueNum() {
        return this.queueNum;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getBrief() {
        return this.brief;
    }

    public void setAccClaId(String AccClaId) {
        this.AccClaId = AccClaId;
    }

    public String getAccClaId() {
        return this.AccClaId;
    }

    public void setDebit(Float debit) {
        this.debit = debit;
    }

    public Float getDebit() {
        return this.debit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public Float getCredit() {
        return this.credit;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

	public String getAccClaNum() {
		return AccClaNum;
	}

	public void setAccClaNum(String accClaNum) {
		AccClaNum = accClaNum;
	}
}