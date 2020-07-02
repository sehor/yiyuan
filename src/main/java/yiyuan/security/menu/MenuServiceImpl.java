package yiyuan.security.menu;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl implements MenuService {
@Autowired
MenuRepository repository;
     @Override
    public Menu addMenu(Menu menu) {
        return repository.save(menu);
    }
    @Override
    public Menu getMenu(String id) {
        return repository.findById(id).get();
    }
    @Override
    public Menu updateMenu(Menu menu) {
        return repository.save(menu);
    }
    @Override
    public void deleteMenu(Menu menu) {
       repository.delete(menu);
    }
    @Override
    public void deleteMenu(String id) {
        repository.deleteById(id);
    }
	@Override
	public List<Menu> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}
}