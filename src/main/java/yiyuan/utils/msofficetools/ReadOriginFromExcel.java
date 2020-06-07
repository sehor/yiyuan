package yiyuan.utils.msofficetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;

@Service
public class ReadOriginFromExcel implements ReadDataFromExcel {
	@Autowired
	OriginService originService;
	private final int Head_Column_Index = 0;
	private final int Data_Column_First_Index = 1;

	/*
	 * workbook should have one column named 'serial_numuber' at first place or may
	 * cause some problems
	 */
	public List<Origin> readWorkbook(String companyName, File file) {
		List<Origin> origins = new ArrayList<>();
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(file);

			// 读sheet---->
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				XSSFSheet sheet = workbook.getSheetAt(sheetNum);
				String sheetName = sheet.getSheetName();
				Map<Integer, Field> map = getIndexToFieldMap(sheet, Origin.class);

				// 读行---->
				for (int rowIx = this.Data_Column_First_Index; rowIx <= sheet.getLastRowNum(); rowIx++) {
					XSSFRow row = sheet.getRow(rowIx);
					if (row == null)
						continue;
					Origin origin = new Origin();
					origin.setCompanyName(companyName);

					for (Entry<Integer, Field> entry : map.entrySet()) {
						XSSFCell cell = row.getCell(entry.getKey());
						if (cell != null) {

							String cellvalue = ExcelUtil.getCellValueByCell(cell);

							//System.out.println(sheetName + origin.getSerial_number() + ": " + cellvalue);
							ExcelUtil.setFieldsValue(origin, entry.getValue(), cellvalue);
						}
					}
					if (origin.getSerial_number() == null)
						continue;
					origin.setType(sheetName); // type暂时先设为sheet名
					origin.setId(companyName + sheetName + origin.getSerial_number()); // 设置id 公司名+sheet名+序号
					origins.add(origin);
				}

				// <----读行
			}

			// <-----读sheet

		} catch (InvalidFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {

			try {
				if (workbook != null) {

					workbook.close();
					System.out.println("workbook:" + workbook.toString() + " close!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return origins;

	}

	

	private Map<Integer, Field> getIndexToFieldMap(XSSFSheet sheet, Class<Origin> clz) {
		Map<Integer, Field> map = new HashMap<>();
		XSSFRow headRow = sheet.getRow(this.Head_Column_Index);
		Field[] fields = clz.getDeclaredFields();

		for (int i = headRow.getFirstCellNum(); i < headRow.getLastCellNum(); i++) {
			XSSFCell cell = headRow.getCell(i);
			cell.setCellType(CellType.STRING);
			int j = 0;
			for (; j < fields.length; j++) {
				// System.out.println(cell.getStringCellValue()+" "+fields[j].getName());
				if (fields[j].getName().trim().equalsIgnoreCase(cell.getStringCellValue().trim())) {
					map.put(i, fields[j]);
					break;
				}
			}
			if (j >= fields.length) {
				System.out.println("警告， 有不能匹配的列名: " + cell.getStringCellValue() + "!,跳过");
			}
		}

		return map;

	}

	

}
