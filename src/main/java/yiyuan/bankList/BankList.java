package yiyuan.bankList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bankList")
@Component
@ApiModel(value = "", description = "")
public class BankList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "")
    private Integer id;

    @Column()
    @ApiModelProperty(value = "")
    private String brief;

    @Column()
    @ApiModelProperty(value = "")
    private String relateAccount;

    @Column()
    @ApiModelProperty(value = "")
    private Float income;

    @Column()
    @ApiModelProperty(value = "")
    private LocalDate date;


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getBrief() {
        return this.brief;
    }

    public void setRelateAccount(String relateAccount) {
        this.relateAccount = relateAccount;
    }

    public String getRelateAccount() {
        return this.relateAccount;
    }

    public void setIncome(Float income) {
        this.income = income;
    }

    public Float getIncome() {
        return this.income;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }
}