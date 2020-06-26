package yiyuan.utils.msofficetools;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

public class ExcelUtil {

	public static String getCellValueByCell(Cell cell) {
		// 判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) {
			return "";
		}
		String cellValue = "";
		CellType cellType = cell.getCellType();

		switch (cellType) {
		case NUMERIC: // 数字
			short format = cell.getCellStyle().getDataFormat();
			if (DateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat sdf = null;
				// System.out.println("cell.getCellStyle().getDataFormat()="+cell.getCellStyle().getDataFormat());
				if (format == 20 || format == 32) {
					sdf = new SimpleDateFormat("HH:mm");
				} else if (format == 14 || format == 31 || format == 57 || format == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				} else {// 日期
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				try {
					cellValue = sdf.format(cell.getDateCellValue());// 日期
				} catch (Exception e) {
					try {
						throw new Exception("exception on get date data !".concat(e.toString()));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} finally {
					sdf = null;
				}
			} else {
				BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
				cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
			}
			break;
		case STRING: // 字符串
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN: // Boolean
			cellValue = cell.getBooleanCellValue() + "";
			;
			break;
		case FORMULA: // 公式
			cellValue = cell.getCellFormula();
			break;
		case BLANK: // 空值
			cellValue = "";
			break;
		case ERROR: // 故障
			cellValue = "ERROR VALUE";
			break;
		default:
			cellValue = "UNKNOW VALUE";
			break;
		}
		return cellValue;
	}

	@SuppressWarnings("deprecation")
	public static void setFieldsValue(Object t, Field field, String value) {

		field.setAccessible(true);

		try {
			if (field.getType() == String.class)
				field.set(t, value);
			else if (field.getType() == Date.class) {

				field.set(t, new SimpleDateFormat("yyyy-MM-dd").parse(value));

			} else if (field.getType() == Integer.class) {
				if (value.isEmpty()) {
					field.set(t, 0);
				} else {
					field.set(t, (int) Math.rint(Double.valueOf(value)));
				}
				; // 有小数点的字符串，四舍五入
			} else if (field.getType() == Float.class) {
				if (value.isEmpty()) {
					field.set(t, 0.00F);
				} else
					field.set(t, new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			} else if (field.getType() == Double.class) {
				if (value.isEmpty()) {
					field.set(t, 0.00D);
				} else {
					//System.out.println(new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					field.set(t, new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				}

			} else if (field.getType() == Long.class) {
				if (value.isEmpty()) {
					field.set(t, 0);
				}
				field.set(t, (long) Math.rint(Double.valueOf(value)));
			} else if (field.getType() == LocalDate.class) {

				try {
					field.set(t, LocalDate.parse(value));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					try {
						field.set(t, LocalDate.parse(value.substring(0, "yyyyMMdd".length()),
								DateTimeFormatter.ofPattern("yyyyMMdd")));
					} catch (Exception date_e) {
						try {
							
							field.set(t, LocalDate.parse(value.substring(0, "yyyy-MM-dd".length()),
									DateTimeFormatter.ofPattern("yyyy-MM-dd")));
						}catch(Exception date_e1) {
							
							field.set(t, LocalDate.parse(value.substring(0, "yyyy/MM/dd".length()),
									DateTimeFormatter.ofPattern("yyyy/MM/dd")));
						}
						
					}
				}

			}
		} catch (IllegalAccessException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static double set2Dig(double d) {  
		   return new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	@SuppressWarnings("deprecation")
	public static float set2Dig(float f) {
		return new BigDecimal(f).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		
	}
    
	public static <T> List<T> filter(List<T> list,Predicate<T> predicate){
		if(list.size()<1) return List.of();
    	list=list.stream().filter(predicate).collect(Collectors.toList());
		return list;	
    }
}
