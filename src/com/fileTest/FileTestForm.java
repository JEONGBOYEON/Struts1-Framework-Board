package com.fileTest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class FileTestForm extends ActionForm{
	
	private static final long serialVersionUID = 1L;
	
	//db에 넣는
	private int num;
	private String subject;
	private String saveFileName;
	private String originalFileName;
	
	//db에 안넣는
	//struts1에서 업로드 시키는 얘(변수이름은 write.jsp의 input(type=file)의 이름과 반드시 같아야 한다)
	private FormFile upload;
	
	private int listNum;//일렬번호 재정렬
	private String urlFile;//파일의 다운로드 경로
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSaveFileName() {
		return saveFileName;
	}
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public FormFile getUpload() {
		return upload;
	}
	public void setUpload(FormFile upload) {
		this.upload = upload;
	}
	public int getListNum() {
		return listNum;
	}
	public void setListNum(int listNum) {
		this.listNum = listNum;
	}
	public String getUrlFile() {
		return urlFile;
	}
	public void setUrlFile(String urlFile) {
		this.urlFile = urlFile;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
