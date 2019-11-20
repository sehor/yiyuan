package yiyuan.domain;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import yiyuan.service.CustomerService;

@Component
public class CustomerSaleRecord {

	private Customer customer;
	private LocalDate date;
	private List<SaleRecord> saleRecords;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<SaleRecord> getSaleRecords() {
		return saleRecords;
	}

	public void setSaleRecords(List<SaleRecord> saleRecords) {
		this.saleRecords = saleRecords;
	}

}
