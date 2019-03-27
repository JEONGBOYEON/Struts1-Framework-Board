package com.fileTest;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.FileManager;
import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class FileTestAction extends DispatchAction{

	
	
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		
		return mapping.findForward("write");
	}
	
	
	
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		HttpSession session = request.getSession();
		
		//저장할 폴더의 경로 생성
		String root = session.getServletContext().getRealPath("/");
		String savepath = root + File.separator + "pds" + File.separator + "saveFile";
		
		//이미 f에는 write.jsp에서 받아온 FileForm과 subject가 들어있다
		FileTestForm f = (FileTestForm)form;
		
		//파일 업로드
		String newFileName = FileManager.doFileUpload(f.getUpload(), savepath);
		
		//업로드한 파일의 정보를 DB에 입력
		if(newFileName!=null){
			
			int maxNum = dao.getIntValue("fileTest.maxNum");
			
			f.setNum(maxNum+1);
			f.setSaveFileName(newFileName);
			f.setOriginalFileName(f.getUpload().getFileName());
			//f.getUpload().getFileSize();
			dao.insertData("fileTest.insertData", f);
			
		}
		
		
		
		return mapping.findForward("write_ok");
	}
	
	
	
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		MyUtil myUtil = new MyUtil();
		String cp = request.getContextPath();
		
		int numPerPage = 5;
		int totalPage = 0;
		int totalDataCount =0;
		
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null&&!pageNum.equals(""))
			currentPage = Integer.parseInt(pageNum);
		
		totalDataCount = dao.getIntValue("fileTest.dataCount");
		
		if(totalDataCount!=0)
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = (List<Object>)dao.getListData("fileTest.listData",hMap);
		
		//번호 재정렬
		Iterator<Object> it = lists.iterator();
		int listNum,n = 0;
		String str;
		
		while(it.hasNext()){
			
			FileTestForm dto = (FileTestForm)it.next();
			listNum = totalDataCount - (start+n-1);
			dto.setListNum(listNum);
			n++;
			
			//파일 다운로드 경로
			str = cp + "/fileTest.do?method=download&num="+dto.getNum();
			dto.setUrlFile(str);
		}
		
		String urlList = cp + "/fileTest.do?method=list";
		
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("totalDataCount", totalDataCount);
		request.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		request.setAttribute("lists", lists);
		
		return mapping.findForward("list");
	}
	
	
	
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();

		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savepath = root + File.separator + "pds" + File.separator + "saveFile";
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		//saveFile을 보내서 삭제해줘야 되니까 일단 num을 가지고 해당 파일의 정보를 모두 읽어온다
		FileTestForm dto = (FileTestForm)dao.getReadData("fileTest.readData",num);
		
		String saveFileName = dto.getSaveFileName();
		
		FileManager.doFileDelete(saveFileName, savepath);
		
		dao.delteData("fileTest.deleteData",num);
		
		return mapping.findForward("delete");
	}
	
	
	
	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();

		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savepath = root + File.separator + "pds" + File.separator + "saveFile";
		
		int num = Integer.parseInt(request.getParameter("num"));

		//saveFile을 보내서 삭제해줘야 되니까 일단 num을 가지고 해당 파일의 정보를 모두 읽어온다
		FileTestForm dto = (FileTestForm)dao.getReadData("fileTest.readData",num);
		
		if(dto==null)
			return mapping.findForward("list");
		
		boolean flag = FileManager.doFileDownload(dto.getSaveFileName(), dto.getOriginalFileName(), savepath, response);
		
		if(!flag){
			response.setContentType("text/html;charset=UTF-8");
			
			PrintWriter out = response.getWriter();
			
			out.print("<script type='text/javascript'>");
			out.print("alert('다운로드에러!!');");
			out.print("history.back()");
			out.print("</script>");
		}
		
		
		return mapping.findForward("null");
	}
	
}
