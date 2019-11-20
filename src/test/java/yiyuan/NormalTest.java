package yiyuan;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import yiyuan.domain.SaleRecord;
import yiyuan.utils.msofficetools.DefaultXLSToBeanTransform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class NormalTest {



    //@Test
    public void test() throws IOException {

    }

    @Test
    public void dateTest(){
        LocalDate d=LocalDate.parse("2019-01-01");
        LocalTime t= LocalTime.now();
        LocalDateTime dt= LocalDateTime.now();
        ZonedDateTime zdt =dt.atZone(ZoneId.of("America/New_York"));
        System.out.println(d);
        System.out.println(t);
        System.out.println(dt);
        System.out.println(zdt);

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dt1=LocalDateTime.parse("2019/12/31 10:50:32",dtf);
        System.out.println(dt1);
        System.out.println(d.plusMonths(1));
        System.out.println(d.withYear(2020));

    }
}
