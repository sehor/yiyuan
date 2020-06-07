package yiyuan.utils.msofficetools;

import java.io.File;
import java.util.List;

import yiyuan.JinDie.Origin.Origin;

public interface ReadDataFromExcel {
	public List<Origin> readWorkbook(String companyName, File file);
}
