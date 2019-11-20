package yiyuan.service;

import org.springframework.stereotype.Service;
import yiyuan.domain.Customer;

import java.io.File;
import java.util.List;
@Service
public interface CustomerService {
    List<Customer> getAllCustomers(File file);
}
