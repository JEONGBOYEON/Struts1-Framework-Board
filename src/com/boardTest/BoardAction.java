package com.boardTest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.URLDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oracle.net.aso.b;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class BoardAction extends DispatchAction{
	
	//DB연결
	//CommonDAO dao = CommonDAOImpl.getInstance(); 를 밖에서 해주면 안되고 각각의 매소드 안에 하나하나 작성해주어야 함 
	//저렇게 호출하면 getInstance()함수가 호출되서 ibatis 환경설정 해줌
	
	public ActionForward created(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		String mode = request.getParameter("mode");
		
		if(mode==null){ //insert
			
			request.setAttribute("mode", "save");
			
		}else{ //update
			
			CommonDAO dao = CommonDAOImpl.getInstance(); 

			int num = Integer.parseInt(request.getParameter("num"));
			String pageNum = request.getParameter("pageNum");
			String searchKey = request.getParameter("searchKey");
			String searchValue = request.getParameter("searchValue");

			if(searchValue==null){
				searchKey="subject";
				searchValue="";
			}
			
			if(request.getMethod().equalsIgnoreCase("GET"))
				searchValue = URLDecoder.decode(searchValue,"UTF-8");
			
			BoardForm dto = (BoardForm)dao.getReadData("boardTest.readData",num);
			
			if(dto==null){
				return mapping.findForward("list");
			}
			
			String url = "&pageNum="+pageNum+"&searchKey="+searchKey+"&searchValue="+searchValue;

			request.setAttribute("dto", dto);
			request.setAttribute("mode", "updatedok");
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("searchKey", searchKey);
			request.setAttribute("searchValue", searchValue);
			request.setAttribute("url", url);
		}
		
		
		return mapping.findForward("created");
	}
	
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance(); 
		BoardForm f = (BoardForm)form;
		
		String mode = request.getParameter("mode"); //save or updatedok
		
		if(mode.equals("save")){//입력
			
			int maxNum=dao.getIntValue("boardTest.maxNum");	//boardTest_sqlMap.xml에 있는 것을 넣어준다
			
			f.setNum(maxNum +1);
			f.setIpAddr(request.getRemoteAddr());
			
			dao.insertData("boardTest.insertData", f);//f는 boardForm을 넘겨준다 parameterClass여기안에있는 경로
			
		}else{//수정
			
			String pageNum = request.getParameter("pageNum");
			String searchKey = request.getParameter("searchKey");
			String searchValue = request.getParameter("searchValue");

			if(searchValue==null){
				searchKey="subject";
				searchValue="";
			}
			
			if(request.getMethod().equalsIgnoreCase("GET"))
				searchValue = URLDecoder.decode(searchValue,"UTF-8");
			
			
			dao.updatetData("boardTest.updateData", f);
			
			//pageNum넘기기 : list로 redirect하는데 그때는 pageNum을 넘길 방법이 없기 때문에 session으로 넘기기
			HttpSession session = request.getSession();
			
			session.setAttribute("pageNum", pageNum);
			session.setAttribute("searchKey", searchKey);
			session.setAttribute("searchValue", searchValue);
			
			
		}
		
		
		dao=null;
		
		return mapping.findForward("created_ok");
		
	}
	
	public ActionForward deleted(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance(); 

		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		//
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue==null){
			searchKey="subject";
			searchValue="";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET"))
			searchValue = URLDecoder.decode(searchValue,"UTF-8");

		//
		
		
		dao.delteData("boardTest.deleteData",num);
		
		HttpSession session = request.getSession();
		session.setAttribute("pageNum", pageNum);
		//
		session.setAttribute("searchKey", searchKey);
		session.setAttribute("searchValue", searchValue);
				
		return mapping.findForward("deleted_ok");
		
	}
	
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// 업캐스팅 해준거 : 인터페이스(CommonDAO)는 implement한 클래스(CommonDAOImpl)의 ~가 될수있다.  
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		String cp = request.getContextPath();
		MyUtil myUtil = new MyUtil();
		
		int numPerPage = 10;
		int totalPage = 0;
		int totaldataCount = 0;
		
		int currentPage = 1;
		
		
		//pageNum 받기 !!!!!!!!!!!!
		//첫번째시도 : parameter로 받기
		String pageNum = request.getParameter("pageNum");
		//두번째시도 : session로 받기
		HttpSession session = request.getSession();
		if(pageNum==null){
			pageNum = (String)session.getAttribute("pageNum");
		}
		session.removeAttribute("pageNum");
		
		//위의 두가지 방법에도 pageNum이 없을때
		if(pageNum!=null)
			currentPage = Integer.parseInt(pageNum);
		
		
		//검색값 받기 !!!!!!!!!!!!
		//첫번째시도 : parameter로 받기
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		//두번째시도 : session로 받기
		if(searchValue==null){
			searchKey = (String)session.getAttribute("searchKey");
			searchValue = (String)session.getAttribute("searchValue");
		}
		session.removeAttribute("searchKey");
		session.removeAttribute("searchValue");
		
		//위의 두가지 방법에도 pageNum이 없을때
		if(searchValue==null){
			searchKey="subject";
			searchValue="";
		}
		if(request.getMethod().equalsIgnoreCase("GET"))
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		
		
		
		
		
		Map<String, Object> hMap = new HashMap<String, Object>();

		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		
		totaldataCount = dao.getIntValue("boardTest.dataCount",hMap);
		
		if(totaldataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totaldataCount);
		}
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage + 1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		//dao.getListData(id,map)
		//에서 id는 boardTest_sqlMap.xml의 namespace.각각의 id
		List<Object> lists = dao.getListData("boardTest.listData", hMap);
		
		String param = "";
		String urlArticle = "";
		String urlList = "";
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			param = "&searchKey="+ searchKey;
			param += "&searchValue=" + searchValue;
		}
		
		urlList = cp +"/boardTest.do?method=list"+param;
		urlArticle = cp + "/boardTest.do?method=article&pageNum="+currentPage;
		urlArticle += param;
		
		request.setAttribute("lists", lists);
		request.setAttribute("urlArticle", urlArticle);
		request.setAttribute("pageNum", currentPage);
		request.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		request.setAttribute("totaldataCount", totaldataCount);
		
		
		
		return mapping.findForward("list");
		
	}

	
	public ActionForward article(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = request.getContextPath();
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue==null){
			searchKey="subject";
			searchValue="";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET"))
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		
		dao.updatetData("boardTest.hitCountUpdate", num);
		
		BoardForm dto = (BoardForm)dao.getReadData("boardTest.readData",num);
		
		if(dto==null){
			return mapping.findForward("list");
		}
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replace("\n", "<br/>"));
		
		//이전글 다음글 처리
		String preUrl = "";
		String nextUrl = "";
		
		//sqlMpa.xml에 searchKey,searchValue,num 이 3가지를 넘겨줘야 한다
		Map<String,Object> bMap = new HashMap<String, Object>();
		
		bMap.put("searchKey",searchKey);
		bMap.put("searchValue",searchValue);
		bMap.put("num", num);
		
		String preSubject="";
		BoardForm preDTO = (BoardForm)dao.getReadData("boardTest.preReadData",bMap);
		
		//다음글이나 이전글이 있는 경우에만 = 본인이 마지막 이나 처음글인 아닌 경우
		if(preDTO!=null){
			preUrl = cp + "/boardTest.do?method=article&pageNum="+pageNum;
			preUrl += "&num=" + preDTO.getNum();
			
			preSubject = preDTO.getSubject();
		}
		
		String nextSubject="";
		BoardForm nextDTO = (BoardForm)dao.getReadData("boardTest.nextReadData",bMap);
		
		//다음글이나 이전글이 있는 경우에만 = 본인이 마지막 이나 처음글인 아닌 경우
		if(nextDTO!=null){
			nextUrl = cp + "/boardTest.do?method=article&pageNum="+pageNum;
			nextUrl += "&num=" + nextDTO.getNum();
			
			nextSubject = nextDTO.getSubject();
		}
		
		String urlList = cp + "/boardTest.do?method=list&pageNum="+pageNum;
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			urlList += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			
			if(!preUrl.equals("")){
				preUrl += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			}
			
			if(!nextUrl.equals("")){
				nextUrl += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			}
		}
		
		//수정과 삭제에서 사용할 인수
		String paramArticle = "num=" + num + "&pageNum=" + pageNum;
		if(!searchValue.equals("")){
			paramArticle += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
		}

		request.setAttribute("dto", dto);
		request.setAttribute("preSubject", preSubject);
		request.setAttribute("preUrl", preUrl);
		request.setAttribute("nextSubject", nextSubject);
		request.setAttribute("nextUrl", nextUrl);
		request.setAttribute("lineSu", lineSu);
		request.setAttribute("paramArticle", paramArticle);
		request.setAttribute("urlList", urlList);
		
		return mapping.findForward("article");
	}
	
}
