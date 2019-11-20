package yiyuan.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import yiyuan.domain.Customer;
import yiyuan.domain.CustomerSaleRecord;
import yiyuan.domain.SaleRecord;

@Component
public class CustomerSaleRecordServiceImpl implements CustomerSaleRecordService {

	@Autowired
	CustomerService customerService;

	@Override
	public List<CustomerSaleRecord> getCustomerSaleRecords(List<SaleRecord> saleRecords) {
		// TODO Auto-generated method stub
		if (saleRecords.size() <= 0)
			return null;

		List<CustomerSaleRecord> customerSaleRecords = new ArrayList<>();
		List<SaleRecord> tempRecords = new ArrayList<>();

		for (int i = 0; i < saleRecords.size(); i++) {
			if (i > 0 && !saleRecords.get(i).get公司名称().equals(saleRecords.get(i - 1).get公司名称())) {
				CustomerSaleRecord csr = new CustomerSaleRecord();
				csr.setSaleRecords(tempRecords);
				csr.setCustomer(getCustomerByName(saleRecords.get(i - 1).get公司名称()));
				customerSaleRecords.add(csr);
				tempRecords.clear();
				tempRecords.add(saleRecords.get(i));

			} else {
				tempRecords.add(saleRecords.get(i));
			}
		}

		// 处理最后一条记录

		CustomerSaleRecord csr1 = new CustomerSaleRecord();
		csr1.setSaleRecords(tempRecords);
		csr1.setCustomer(getCustomerByName(saleRecords.get(saleRecords.size() - 1).get公司名称()));
		customerSaleRecords.add(csr1);

		return customerSaleRecords;

	}

	private Customer getCustomerByName(String name) {

		List<Customer> customers = customerService.getAllCustomers(new File("D:\\temp\\客户开票信息表.xlsx"));
		for (Customer customer : customers) {
			if (customer.get名称().contains(name)) {
				return customer;
			}
		}
		return null;
	}

}
