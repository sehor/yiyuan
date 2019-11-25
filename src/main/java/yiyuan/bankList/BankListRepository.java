package yiyuan.bankList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankListRepository extends JpaRepository<BankList, Integer>, BankListDataHelper {

}