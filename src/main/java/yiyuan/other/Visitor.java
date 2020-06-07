package yiyuan.other;

import java.io.File;

public interface Visitor {

	public void handleFile(File file);
	public void handleDirector(File director);
}
