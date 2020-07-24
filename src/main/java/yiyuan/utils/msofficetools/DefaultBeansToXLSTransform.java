package yiyuan.utils.msofficetools;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import yiyuan.utils.Tool;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultBeansToXLSTransform<T> implements BeansToXLSTransform<T> {
	public final int DEFAULT_HEAD_ROW_INDEX = 0;
	public final int DEFAULT_DATA_BEGIN_ROW = 1;
	public final int DEFAULT_SHEET_INDEX = 0;
	public final int DEFAULT_COLUMN_WIDTH = 8 * 256;

	private Field[] fields;

	public DefaultBeansToXLSTransform(Class<T> cls) {
		this.fields = cls.getDeclaredFields();
	}

	@Override
	public String[] createColumnTitles() {
		String[] columnTitles = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			columnTitles[i] = fields[i].getName();
		}
		return columnTitles;
	}

	@Override
	public Map<Integer, Field> fieldColumnMap() {
		Map<Integer, Field> fieldColumnMap = new HashMap<>();
		for (int i = 0; i < fields.length; i++) {
			fieldColumnMap.put(i, fields[i]);
		}
		return fieldColumnMap;
	}

	@Override
	public HSSFWorkbook createWorkbook(List<T> beans) {
		HSSFWorkbook workbook = new HSSFWorkbook();

		initWorkbook(workbook);

		HSSFSheet sheet = workbook.getSheetAt(DEFAULT_SHEET_INDEX);

		// 创建日期显示格式
		HSSFCellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		
		int rowIndex = DEFAULT_DATA_BEGIN_ROW;

		// 取得列数
		int columnNum = createColumnTitles().length;

		// 取得列和field映射
		Map<Integer, Field> fieldColumnMap = fieldColumnMap();
		for (Object o : beans) {
			T t = (T) o;// 转型

			HSSFRow row = sheet.createRow(rowIndex);
			for (int i = 0; i < columnNum; i++) {
				HSSFCell cell = row.createCell(i);
				
				Field field = fieldColumnMap.get(i);

				field.setAccessible(true);
				try {
					Object value = field.get(t);
					if (value == null)
						continue;

					Object type = field.getType();

					if (type == String.class) {
						cell.setCellValue((String) value);
					} else if (type == Integer.class) {
						cell.setCellValue((Integer) value);
					} else if (type == Long.class) {
						cell.setCellValue((Long) value);
					} else if (type == Double.class) {

						cell.setCellValue((Double) value);
					} else if (type == Float.class) {
						cell.setCellValue((Float) value);
					} else if (type == Date.class) {
						cell.setCellStyle(dateCellStyle);
						cell.setCellValue((Date) value);
					} else {
						cell.setCellValue(value.toString());
					}

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}

			rowIndex++;

		}
		
		

		return workbook;
	}

	private void initWorkbook(HSSFWorkbook workbook) {
//2.创建文档摘要
		workbook.createInformationProperties();
		// 3.获取文档信息，并配置
		DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
		// 3.1文档类别
		dsi.setCategory("xx信息");
		// 3.2设置文档管理员
		dsi.setManager("pzr");
		// 3.3设置组织机构
		dsi.setCompany("XXX集团");
		// 4.获取摘要信息并配置
		SummaryInformation si = workbook.getSummaryInformation();
		// 4.1设置文档主题
		si.setSubject("xxx信息表");
		// 4.2.设置文档标题
		si.setTitle("xx信息");
		// 4.3 设置文档作者
		si.setAuthor("XXX集团");
		// 4.4设置文档备注
		si.setComments("备注信息暂无");
		// 创建Excel表单
		HSSFSheet sheet = workbook.createSheet("XXX集团xx信息表");

		// 创建标题的显示样式
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// 定义列宽
		String[] columnTitles = createColumnTitles();

		for (int i = 0; i < columnTitles.length; i++) {
			sheet.setColumnWidth(i, DEFAULT_COLUMN_WIDTH);
		}

		// 设置标题行
		HSSFRow headRow = sheet.createRow(DEFAULT_HEAD_ROW_INDEX);
		for (int i = 0; i < columnTitles.length; i++) {
			HSSFCell cell = headRow.createCell(i);
			cell.setCellValue(columnTitles[i]);
			cell.setCellStyle(headerStyle);
		}

	}



	@Override
	public void writeToFile(String fileName, List<T> list) {
		// TODO Auto-generated method stub
		File file = new File(fileName);
		OutputStream output = null;
		HSSFWorkbook workbook = createWorkbook(list);
		stripe(workbook);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		try {
			output = new FileOutputStream(file);
			workbook.write(output);

			workbook.close();
			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stripe(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheetAt(DEFAULT_SHEET_INDEX);
		int flag = -1;
		HSSFCellStyle style = (HSSFCellStyle) workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
		for (int rowIndex = DEFAULT_DATA_BEGIN_ROW; rowIndex <= sheet.getPhysicalNumberOfRows(); rowIndex++) {
			HSSFRow row = sheet.getRow(rowIndex);
			if (row == null)
				continue;
			
			
			if (rowIndex>DEFAULT_DATA_BEGIN_ROW) {    //遇到不同类的，变换flag;
				String preStr=ExcelUtil.getCellValueByCell(sheet.getRow(rowIndex-1).getCell(2))+
						ExcelUtil.getCellValueByCell(sheet.getRow(rowIndex-1).getCell(0));
				
				String currentStr=ExcelUtil.getCellValueByCell(row.getCell(2))+
						ExcelUtil.getCellValueByCell(row.getCell(0));
				
				if(!preStr.equals(currentStr)) {
					flag = flag * -1;
				}
				
			}
			if (flag == 1) {
				
				for (int columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					HSSFCell cell = row.getCell(columnIndex);
					if (cell == null)
						continue;
					cell.setCellStyle(style);
					
				}

			}
		}
	}

}
