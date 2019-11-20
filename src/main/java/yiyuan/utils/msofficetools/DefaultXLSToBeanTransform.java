package yiyuan.utils.msofficetools;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class DefaultXLSToBeanTransform<T> implements XLSToBeanTransform<T> {
    public final static int DEFAULT_BEGIN_DATA_ROW = 1;
    public final static int DEFAULT_BEGIN_COLUMN = 0;
    public final static int DEFAULT_HEAD_ROW = 0;
    public final static int DEFAULT_SHEET_INDEX = 0;
    private T t;
    private List<Field> allFields;
    private XSSFWorkbook workbook;
    private Map<Integer, Field> fieldIndexMap;

    @Override
    public void creatBeanViaRow(T t, Map<Integer, Field> ColumnIndexfieldNameMap) {

    }


    @Override
    public Map<Integer, Field> createFieldsMap(String[] sheetHeadsString) {
        Map<Integer, Field> map = new HashMap<>();
        Field[] fieldAarry = this.allFields.toArray(new Field[this.allFields.size()]);

        for (int i = 0; i < sheetHeadsString.length; i++) {
            int j = 0;
            for (; j < fieldAarry.length; j++) {
                if (sheetHeadsString[i].trim().equalsIgnoreCase(fieldAarry[j].getName().trim())) {
                    map.put(i, fieldAarry[j]);
                    break;
                }
            }
            if (j >= fieldAarry.length) {
                System.out.println("警告， 有不能匹配的列名: " + sheetHeadsString[i] + "!,跳过");
            }
        }

        return map;
    }

    @Override
    public Map<Integer, Field> createFieldsMap() {
        return createFieldsMap(createSheetHeadsNames());
    }


    @Override
    public int beginRowIndex() {
        return DEFAULT_BEGIN_DATA_ROW;
    }


    public DefaultXLSToBeanTransform(T t, XSSFWorkbook workbook) {
        this.t = t;
        this.allFields = Arrays.asList(t.getClass().getDeclaredFields());
        this.workbook = workbook;
        this.fieldIndexMap = createFieldsMap();

    }


    @Override
    public List<T> creatBeans(int sheetIndex) {
        List<T> beans = new ArrayList<>();
        XSSFSheet sheet = this.workbook.getSheetAt(sheetIndex);

        for (int i = DEFAULT_BEGIN_DATA_ROW; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null&&row.getCell(1)!=null&&row.getCell(2)!=null) {
                beans.add(creatBeanViaRow(row));
            }
        }
        return beans;
    }


    public T creatBeanViaRow(XSSFRow row) {
        try {
            T t1 = (T) this.t.getClass().newInstance();  //通过反射新建一个T类型的实例

            int cellsNumb = row.getPhysicalNumberOfCells();

            for (Entry<Integer, Field> entry : this.fieldIndexMap.entrySet()) {
                XSSFCell cell = row.getCell(entry.getKey());
                if (cell != null) {

                    cell.setCellType(CellType.STRING);
                    String cellvalue = (cell.getStringCellValue() != null ? cell.getStringCellValue() : "");
                    //System.out.println("cell value is: " + cellvalue);
                    setFieldsValue(t1, entry.getValue(), cellvalue);
                }
            }

            /*
             * for(int i=0;i<cellsNumb;i++){ XSSFCell cell=row.getCell(i); //if(cell==null)
             * continue; cell.setCellType(CellType.STRING);
             *
             * String
             * cellValue=(cell.getStringCellValue()!=null?cell.getStringCellValue():"");
             *
             * setFieldsValue(t1,this.fieldIndexMap.get(i),cellValue);
             * //把对应excel表格的列的值设置到field }
             */
            return t1;
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {

            e.printStackTrace();
            return null;
        }

    }


    private String[] createSheetHeadsNames() {
        XSSFSheet sheet = this.workbook.getSheetAt(DEFAULT_SHEET_INDEX);
        System.out.println("sheet name is" + sheet.getSheetName());
        XSSFRow headRow = sheet.getRow(DEFAULT_HEAD_ROW);
        String[] sheetHeadsNames = null;

        if (headRow != null) {

            int cellsNum = headRow.getPhysicalNumberOfCells();
            System.out.println("有" + cellsNum + "列");
            sheetHeadsNames = new String[cellsNum];
            for (int i = 0; i < cellsNum; i++) {
                XSSFCell cell = headRow.getCell(i);
                sheetHeadsNames[i] = cell.getStringCellValue();
            }

        }

        return sheetHeadsNames;
    }


    private void setFieldsValue(T t, Field field, String value) {

        field.setAccessible(true);
        try {
            if (field.getType() == String.class)
                field.set(t, value);
            else if (field.getType() == Date.class) {
                field.set(t, new SimpleDateFormat("yyyy-MM-dd").parse(value));
            } else if (field.getType() == Integer.class) {

                field.set(t, (int) Math.rint(Double.valueOf(value))); //有小数点的字符串，四舍五入
            } else if (field.getType() == Float.class) {
                field.set(t, Float.valueOf(value));
            } else if (field.getType() == Double.class) {
                field.set(t, Double.valueOf(value));
            } else if (field.getType() == Long.class) {
                field.set(t, (long) Math.rint(Double.valueOf(value)));
            }
        } catch (IllegalAccessException | ParseException e) {
            e.printStackTrace();
        }
    }


}
