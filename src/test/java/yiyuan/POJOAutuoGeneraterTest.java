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

        AutoJOPO autoJOPO = new AutoJOPO("JinDieRecord", "yiyuan.JinDie.JinDieRecord","mongo");
        autoJOPO
                .addField("private", "String", "id")
                .addField("private", "LocalDate", "日期")
                .addField("private", "String", "凭证字")
                .addField("private", "Integer", "凭证号")
                .addField("private", "Integer", "分录序号")
                .addField("private", "String", "摘要")
                .addField("private", "String", "科目代码")
                .addField("private", "Float", "借方金额")
                .addField("private", "Float", "贷方金额")
                
				/*
				 * .addField("private", "String", "parentId") .addField("private", "LocalDate",
				 * "date") .addField("private", "String", "brief")
				 * .addField("private","String","AccClaId") .addField("private","Float","debit")
				 * .addField("private","Float","credit")
				 * .addField("private","String","creatorId")
				 */

                .buildFiles();

        //System.out.println(beanString);

    }
}
