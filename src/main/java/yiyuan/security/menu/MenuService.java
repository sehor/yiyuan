package yiyuan.security.menu;

import java.util.List;

public interface MenuService {
Menu addMenu(Menu menu);
Menu getMenu(String id);
Menu updateMenu(Menu menu);
void deleteMenu(Menu menu);
void deleteMenu(String id);
List<Menu> findAll();
}