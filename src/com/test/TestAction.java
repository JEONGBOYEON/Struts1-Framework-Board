package com.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

//ActionForm을 extends한 TestFrom의 데이터를 다 가져다 쓴다
//코딩이 줄었다

public class TestAction extends Action{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String uri = request.getRequestURI();
		
		if(uri.indexOf("/test_ok.do")!=-1){
			
			TestForm f = (TestForm)form; //매개변수로 받은 form을 다운캐스팅 한다.
			
			request.setAttribute("vo", f);
			
			return mapping.findForward("ok");
			
		}
		
		return mapping.findForward("error");
	}
	
	
}
