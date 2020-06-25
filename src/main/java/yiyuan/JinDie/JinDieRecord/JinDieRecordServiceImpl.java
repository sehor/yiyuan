package yiyuan.JinDie.JinDieRecord;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.other.annotation.TimeCost;
import yiyuan.utils.msofficetools.OriginProcess;

@Service
public class JinDieRecordServiceImpl implements JinDieRecordService {
@Autowired
JinDieRecordRepository repository;
@Autowired
OriginService originService;
@Autowired
OriginProcess originProcess;
     @Override
    public JinDieRecord addJinDieRecord(JinDieRecord jinDieRecord) {
        return repository.save(jinDieRecord);
    }
    @Override
    public JinDieRecord getJinDieRecord(String id) {
        return repository.findById(id).get();
    }
    @Override
    public JinDieRecord updateJinDieRecord(JinDieRecord jinDieRecord) {
        return repository.save(jinDieRecord);
    }
    @Override
    public void deleteJinDieRecord(JinDieRecord jinDieRecord) {
       repository.delete(jinDieRecord);
    }
    @Override
    public void deleteJinDieRecord(String id) {
        repository.deleteById(id);
    }
    
	@Override
	@TimeCost
	public List<JinDieRecord> processToRecords(LocalDate begin, LocalDate end,String companyName) {
		// TODO Auto-generated method stub
		
			List<JinDieRecord> records=new ArrayList<>();
			
			LocalDate currentEnd=begin.with(TemporalAdjusters.lastDayOfMonth()); //当前会计期间最后一天
			LocalDate currentBegin=LocalDate.of(begin.getYear(), begin.getMonth(), 1);//当前会计期间第一天
			
			while(!currentEnd.isAfter(end)) {
				System.out.println(currentBegin+" "+currentEnd);
				List<Origin> origins=originService.getInPeriod(companyName, currentBegin, currentEnd);
				origins=originProcess.setCompanyName(companyName).preProcessOrigin(origins);
				List<JinDieRecord> recordsInOnePreriod=originProcess.setCompanyName(companyName).proccessToRecord(origins, currentEnd);
				records.addAll(recordsInOnePreriod);
				
				currentBegin=currentBegin.plusMonths(1);
				currentEnd=currentBegin.with(TemporalAdjusters.lastDayOfMonth());		
			}
			
		return records;
	}
	public List<JinDieRecord> processToRecords(LocalDate begin, LocalDate end,String companyName,String type){
	
	
		
		
		// TODO Auto-generated method stub
		
					List<JinDieRecord> records=new ArrayList<>();
					
					LocalDate currentEnd=begin.with(TemporalAdjusters.lastDayOfMonth()); //当前会计期间最后一天
					LocalDate currentBegin=LocalDate.of(begin.getYear(), begin.getMonth(), 1);//当前会计期间第一天
					
					while(!currentBegin.isAfter(end)) {
						
						List<Origin> origins=originService.getInPeriod(companyName, currentBegin, currentEnd);
						
						//origins=origins.stream().filter(e->e.getRelative_account().equals("深圳市宜源科技有限公司的保证金")).collect(Collectors.toList()); //type过滤

						origins=originProcess.setCompanyName(companyName).preProcessOrigin(origins);
								
						List<JinDieRecord> recordsInOnePreriod=originProcess.setCompanyName(companyName).proccessToRecord(origins, currentEnd);
						sortRecords(recordsInOnePreriod);
						records.addAll(recordsInOnePreriod);
					
						currentBegin=currentBegin.plusMonths(1);
						currentEnd=currentBegin.with(TemporalAdjusters.lastDayOfMonth());	
						
					}
					
				return records;
	}
	
	private void sortRecords(List<JinDieRecord> records) {
		records.sort((o1,o2)->{
			int diff1 = o1.get凭证号() - o2.get凭证号(), diff2 = o1.get分录序号() - o2.get分录序号();

			if (diff1 != 0) {
				return diff1;
			} else {
				return diff2;
			}	
		});

	}
	
}