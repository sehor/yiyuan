package yiyuan.domain;

import org.springframework.stereotype.Component;

@Component
public class Customer {
    private String 编号;
    private String 名称;
    private  String 纳税编号;
   private String 地址电话;
    private String 银行账号;

    public String get编号() {
        return 编号;
    }

    public void set编号(String 编号) {
        this.编号 = 编号;
    }

    public String get纳税编号() {
        return 纳税编号;
    }

    public void set纳税编号(String 纳税编号) {
        this.纳税编号 = 纳税编号;
    }

    public String get地址电话() {
        return 地址电话;
    }

    public void set地址电话(String 地址电话) {
        this.地址电话 = 地址电话;
    }

    public String get银行账号() {
        return 银行账号;
    }

    public void set银行账号(String 银行账号) {
        this.银行账号 = 银行账号;
    }

    public String get名称() {
        return 名称;
    }

    public void set名称(String 名称) {
        this.名称 = 名称;
    }
}
