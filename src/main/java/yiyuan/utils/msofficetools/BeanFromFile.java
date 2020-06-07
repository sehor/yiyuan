package yiyuan.utils.msofficetools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BeanFromFile {

	@SuppressWarnings("deprecation")
	public static<K> List<K> fromFile(File file, Class<K> clz) {

		FileInputStream fileInputStream = null;
		XSSFWorkbook workbook = null;

		try {
			fileInputStream = new FileInputStream(file);
			workbook = new XSSFWorkbook(fileInputStream);

			return new DefaultXLSToBeanTransform<>((K) clz.newInstance(), workbook).creatBeans(0);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null)
					workbook.close();
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}
}
