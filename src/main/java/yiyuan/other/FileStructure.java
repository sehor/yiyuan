package yiyuan.other;

import java.io.File;

public class FileStructure {

	
	private String filePath;
	public FileStructure(String filePath){
		
		this.filePath=filePath;
	}
	
	public void handle(Visitor vistor) {
		
		 File file=new File(this.filePath);
		 scan(file,vistor);
	}
	
	
	private void scan(File file ,Visitor vistor) {
		
		
		 if(file.isDirectory()) {
			 
			 vistor.handleDirector(file);
			 
			 for(File f:file.listFiles()) {
				 
				 scan(f,vistor);
			 }
		 }else if(file.isFile()) {
			 
			  vistor.handleFile(file);
		 }
		 
		
	}
}
