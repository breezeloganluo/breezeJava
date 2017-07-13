package com.breezefw.shell;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class JsFuncallTag extends SimpleTagSupport {
	private String funexp = null;

	public String getFunexp() {
		return funexp;
	}

	public void setFunexp(String funexp) {
		this.funexp = funexp;
	}
	
	public void doTag() throws JspException, IOException {
		//合成内容
		BreezeFunctioinCallTag p = (BreezeFunctioinCallTag)this.getParent();
		String content = p.getResultData().getContextByPath(this.funexp).toString();
		this.getJspContext().getOut().write(content);
	}
	
}
