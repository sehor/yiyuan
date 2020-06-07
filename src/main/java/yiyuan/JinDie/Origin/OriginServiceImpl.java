package yiyuan.JinDie.Origin;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.Classification.Classfication;

@Service
public class OriginServiceImpl implements OriginService {
@Autowired
OriginRepository repository;
     @Override
    public Origin addOrigin(Origin origin) {
        return repository.save(origin);
    }
    @Override
    public Origin getOrigin(String id) {
        return repository.findById(id).get();
    }
    @Override
    public Origin updateOrigin(Origin origin) {
        return repository.save(origin);
    }
    @Override
    public void deleteOrigin(Origin origin) {
       repository.delete(origin);
    }
    @Override
    public void deleteOrigin(String id) {
        repository.deleteById(id);
    }
	@Override
	public List<Origin> getAll() {
		// TODO Auto-generated method stub
		
		
		return repository.findAll();
	}
	
	
	@Override
	public List<Origin> saveAll(List<Origin> origins) {
		// TODO Auto-generated method stub
		return repository.saveAll(origins);
	}
	@Override
	public List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return repository.findInPeriod(companyName, begin, end);
	}
	@Override
	public List<Origin> deleteByCompanyName(String companyName) {
		// TODO Auto-generated method stub
		return repository.deleteByCompanyName(companyName);
	}
	

	
}