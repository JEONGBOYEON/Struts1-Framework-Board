package com.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.upload.FormFile;

public class FileManager {

	//1. upload
	
	//path:파일을 저장할 경로
	//return:서버에 저장할 새로운 파일이름
	//파일 업로드 후에 리턴값 = FormFile uploadFile에서 A.txt(originalFileName)를 주면 새로운 이름(saveFileName)이 리턴값으로 반환된다
	public static String doFileUpload(FormFile uploadFile,String path) throws Exception{
		
		String newFileName = null;
		
		if(uploadFile==null)
			return null;
		
		//클라이언트가 업로드한 파일이름
		String originalFileName = uploadFile.getFileName();
		
		if(originalFileName.equals(""))
			return null;
		
		//originalFileName에서 확장자 분리해서 확장자를 저장
		String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		if(fileExt==null||fileExt.equals(""))
			return null;
		
		//서버에 저장할 파일명 생성 = 중복되지 않도록 이름 생성
		//년도,월,날짜,시간,분,초
		newFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", Calendar.getInstance());
		//10의 -9승의 값
		newFileName += System.nanoTime();
		newFileName += fileExt;
		
		File f = new File(path);
		
		if(!f.exists())
			f.mkdirs();
		
		String fullFilePath = path + File.separator + newFileName;
		
		//실제로 파일 업로드(지금까지는 준비작업)
		byte[] fileData = uploadFile.getFileData();
		
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		fos.write(fileData);
		fos.close();
		
		//db에 저장할 saveFileName을 리턴해주기
		return newFileName;
				
	}
	
	
	
	//2.파일 삭제
	
	public static void doFileDelete(String fileName,String path){
		
		String fullFilePath = path + File.separator + fileName;
		
		File f = new File(fullFilePath);
		
		if(f.exists())
			f.delete();

	}
	
	
	//3.파일 다운로드
	//saveFileName : 서버에 저장된 파일명
	//originalFileName : 클라이언트가 업로드한 파일명
	//path : 파일이 서버에 저장된 경로
	
	public static boolean doFileDownload(String saveFileName,String originalFileName,
			String path, HttpServletResponse response){
		
		String fullFilePath = path + File.separator + saveFileName;
		
		//한글이름 깨지지 않도록 설정
		try {
			
			if(originalFileName==null||originalFileName.equals("")){
				originalFileName = saveFileName;
			}
			
			originalFileName = new String(originalFileName.getBytes("euc-kr"),"8859_1");
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		//다운로드
		try {
			
			File f = new File(fullFilePath);
			
			if(f.exists()){
				
				byte readByte[] = new byte[4096];
				
				response.setContentType("application/octet-stream");
				
				response.setHeader("Content-disposition", "attachment;fileName=" + originalFileName);
				
				//읽어드리기
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
				//내보내기
				OutputStream os = response.getOutputStream();
				
				int read;
				while((read=fis.read(readByte,0,4096))!=-1){
					os.write(readByte,0,read);
				}
				
				os.flush();
				os.close();
				fis.close();
				
				return true;
			}
			
		} catch (Exception e) {
		}
		
		return false;
		
	}
	
	
	
	
	
	
	
	
	
	
}
