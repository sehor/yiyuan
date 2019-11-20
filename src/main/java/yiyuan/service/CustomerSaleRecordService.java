package yiyuan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import yiyuan.domain.CustomerSaleRecord;
import yiyuan.domain.SaleRecord;

@Service
public interface CustomerSaleRecordService {
	public  List<CustomerSaleRecord> getCustomerSaleRecords(List<SaleRecord> saleRecords);
}
