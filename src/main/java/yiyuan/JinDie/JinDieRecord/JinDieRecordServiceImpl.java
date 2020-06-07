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
					
					while(!currentEnd.isAfter(end)) {
						//System.out.println(currentBegin+" "+currentEnd);
						List<Origin> origins=originService.getInPeriod(companyName, currentBegin, currentEnd);
						origins=originProcess.setCompanyName(companyName).preProcessOrigin(origins);
						origins=origins.stream().filter(e->e.getType().contains(type)).collect(Collectors.toList());
						List<JinDieRecord> recordsInOnePreriod=originProcess.setCompanyName(companyName).proccessToRecord(origins, currentEnd);
						records.addAll(recordsInOnePreriod);
						
						currentBegin=currentBegin.plusMonths(1);
						currentEnd=currentBegin.with(TemporalAdjusters.lastDayOfMonth());	
						
					}
					
				return records;
	}
	
}