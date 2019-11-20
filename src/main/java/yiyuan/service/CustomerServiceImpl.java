package yiyuan.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yiyuan.domain.Customer;
import yiyuan.utils.msofficetools.DefaultXLSToBeanTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
public class CustomerServiceImpl implements CustomerService {

    @Override
    public List<Customer> getAllCustomers(File file) {
        List<Customer> customers = new ArrayList<>();
        FileInputStream fileInputStream = null;
        XSSFWorkbook workbook = null;

        try {
            fileInputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(fileInputStream);
            DefaultXLSToBeanTransform<Customer> xtob = new DefaultXLSToBeanTransform<>(new Customer(), workbook);
            customers = xtob.creatBeans(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return customers;
    }
}
