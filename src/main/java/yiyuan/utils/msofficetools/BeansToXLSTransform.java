package yiyuan.utils.msofficetools;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
@Service
public interface BeansToXLSTransform<T> {

    public String[] createColumnTitles();
    public Map<Integer, Field> fieldColumnMap();
    public HSSFWorkbook createWorkbook(List<T> beans);
    public void writeToFile(String fileName,List<T> list);

}
