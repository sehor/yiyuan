package yiyuan.utils.msofficetools;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public interface XLSToBeanTransform<T> {

    public void creatBeanViaRow(T t,Map<Integer, Field> ColumnIndexfieldNameMap);
    public Map<Integer,Field> createFieldsMap(String[] sheetHeadsString);
    public Map<Integer,Field> createFieldsMap();
    public List<T> creatBeans(int sheetIndex);

}
