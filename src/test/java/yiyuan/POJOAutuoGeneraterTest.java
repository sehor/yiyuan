package yiyuan;

import org.junit.Test;
import yiyuan.utils.AutoJOPO;

import java.io.IOException;

public class POJOAutuoGeneraterTest {


    @Test
    public void test() throws IOException {

/*
    private Integer id;
    private Integer bonus;
    private Integer lunchSalary;
    private Integer trafficSalary;
    private Integer basicSalary;
    private Integer allSalary;
    private Integer pensionBase;
    private Float pensionPer;
    private Date createDate;
    private Integer medicalBase;
    private Float medicalPer;
    private Integer accumulationFundBase;
    private Float accumulationFundPer;
    private String name;

    */

        AutoJOPO autoJOPO = new AutoJOPO("bankList", "yiyuan.bankList");
        autoJOPO
                .addField("private", "Integer", "id")
                .addField("private", "String", "brief")
                .addField("private", "String", "relateAccount")
                .addField("private", "Float", "income")
                .addField("private", "LocalDate", "date")
                .buildFiles();

        //System.out.println(beanString);

    }
}
