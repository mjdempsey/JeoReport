/*
 * Java-Open311 - A java implementation of the Open311 GeoReportv2 Specification.
    Copyright (C) 2013, 2014  Matthew Dempsey

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.open311.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.open311.beans.DefinitionBean;
import org.open311.beans.DiscoveryBean;
import org.open311.beans.ErrorBean;
import org.open311.beans.RequestBean;
import org.open311.beans.ServiceListBean;
import org.open311.beans.WrapperBean;
import org.open311.data.I311DAO;

/**
 * Servlet implementation class Formatter
 */
public class Formatter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String type = "";
	JAXBContext context = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Formatter() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("@Formatter.doGet: BEGIN");
		response.setCharacterEncoding("UTF8");
		type = (String) request.getAttribute("format");
		String requestor = (String) request.getAttribute("requestor");
		PrintWriter pw = response.getWriter();
		
		switch(requestor) {		
			case "Discovery":
				System.out.println("@Formatter.doGet: case=DISCOVERY");
				DiscoveryBean d = (DiscoveryBean) request.getAttribute("payload");
				marshal(response, null, null, pw, d);
				break;

			case "ServiceDefinition":
				System.out.println("@Formatter.doGet: case=SERVICE_DEFINITION");
				DefinitionBean db = (DefinitionBean)request.getAttribute("payload");
				ArrayList<DefinitionBean> dbs = new ArrayList<DefinitionBean>();
				dbs.add(db);
				marshal(response, null, null, pw, db);
				break;			
			
			case "ERROR":
				System.out.println("@Formatter.doGet: case=ERROR");
				String desc = (String) request.getAttribute(I311DAO.ER_CD);
				String code = (String) request.getAttribute("40X");
				ErrorBean eb = new ErrorBean();			
				eb.setCode(code);
				eb.setDescription(desc);
				ArrayList<ErrorBean> ebs = new ArrayList<ErrorBean>();
				ebs.add(eb);
				marshal(response, ebs, "errors", pw, eb);
				break;
	
			case "ServiceList":
				System.out.println("@Formatter.doGet: case=SERVICE_LIST");
				List<ServiceListBean> list = (List<ServiceListBean>) request.getAttribute("payload");
				marshal(response, list, "services", pw, list.get(0));
				break;
				
			case "GetServiceRequest":
				System.out.println("@Formatter.doGet: case=GET_SERVICE_REQUEST");
				List<RequestBean> rb = (List<RequestBean>) request.getAttribute("payload");
				marshal(response, rb, "services", pw, rb.get(0));
				break;
						
			case "GetServiceRequests":
				System.out.println("@Formatter.doGet: case=GET_SERVICE_REQUESTS");
				List<RequestBean> rbns = (List<RequestBean>) request.getAttribute("payload");
				marshal(response, rbns, "requests", pw, rbns.get(0));
				
				break;
				
			case "POSTServiceRequest":
				System.out.println("@Formatter.doGet: case=POST_SERVICE_REQUEST");
				RequestBean reqB = (RequestBean) request.getAttribute("payload");
				marshal(response, null, "requests", pw, reqB);
				break;
				
			default:
				System.out.println("@Formatter.doGet: case=UNKNOWN");
				break;
		}
	    System.out.println("@Formatter.doGet: END");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
    /**
     * Wrap List in Wrapper, then leverage JAXBElement to supply root element 
     * information.
     * 
     * supply name with no list
     * supply name with a list
     * supply element with no list or name
     * 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void marshal(HttpServletResponse response, List<?> list, String name, PrintWriter pw, Object pElement) {
    	System.out.println("@Formatter.marshal: BEGIN");
        JAXBContext context = null;
        JAXBMarshaller marshaller;
        JAXBElement<WrapperBean> jaxbElement = null;
        WrapperBean wrapper = null;
		try {
			//IF: name != null:
			////a JAXBElement is needed using that name			
			////the JAXBElement will need to wrap either the list using object supplied as a template or the object supplied 
			if (name != null && !(name.equals("null"))) {
				System.out.println("@Formatter.marshal: name provided: " + name);
				QName qName = new QName(name);	
				
				if (list != null) {
					System.out.println("@Formatter.marshal: list provided");
					wrapper = new WrapperBean(list);
				} else if (pElement != null){
					System.out.println("@Formatter.marshal: element provided");
					wrapper = new WrapperBean();
					wrapper.getItems().add(pElement);
				}
				
				jaxbElement = new JAXBElement<WrapperBean>(qName, WrapperBean.class, wrapper);
				context = (JAXBContext) JAXBContext.newInstance(WrapperBean.class, pElement.getClass());				
				
			} else {
				System.out.println("@Formatter.marshal: no name provided");
				context = (JAXBContext) JAXBContext.newInstance(pElement.getClass());				
			}			
			
			marshaller = context.createMarshaller();
						
		    switch(type){
	        case ".xml" :
	        	System.out.println("@Formatter.doGet: case=XML");
	        	response.setContentType("text/xml; charset=utf-8");	        	
	        	break;
	        case ".json" :
	        	System.out.println("@Formatter.doGet: case=JSON");
		        marshaller.setProperty("eclipselink.media-type", "application/json");
		        marshaller.setProperty("eclipselink.json.include-root", false);
		        response.setContentType("application/json; charset=utf-8");	 		        
	        	break;
	        case ".html" :
	        	System.out.println("@Formatter.doGet: case=HTML");
	        	response.setContentType("text/html; charset=utf-8");
	        	pw.println("html response");
	        	break;   
	        case ".htm" :
	        	System.out.println("@Formatter.doGet: case=HTM");
	        	response.setContentType("text/html; charset=utf-8");
	        	pw.println("HTML Response");
	        	break; 		    	
	    }
			
		    if (jaxbElement == null) {
		    	System.out.println("@Formatter.marshal: marshalling bean");
		    	marshaller.marshal(pElement, pw);
		    } else {
		    	System.out.println("@Formatter.marshal: marshalling JAXBElement");
		    	marshaller.marshal(jaxbElement, pw);
		    }
			
		} catch (JAXBException e) {			
			e.printStackTrace();
		}

        System.out.println("@Formatter.marshal: END");
    }
	
//	private void marshal(HttpServletResponse response, Object pObject, PrintWriter pw, Object pElementObject) {
//		try {
//		
//		  JAXBMarshaller marshaller = null;
//	      
//		  if (pElementObject != null) {
//          context = (JAXBContext) JAXBContext.newInstance(pElementObject.getClass());
//          marshaller = context.createMarshaller();
//		  } else {
//			  context = (JAXBContext) JAXBContext.newInstance(pObject.getClass());
//			  marshaller = context.createMarshaller();
//		  }			
//      
//		  marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);
//			
//	      QName qName = new QName("services");
	      //WrapperBean wrapper = new WrapperBean(pObject.);
	      //JAXBElement<WrapperBean> jaxbElement = new JAXBElement<WrapperBean>(qName, WrapperBean.class, wrapper);
	      //marshaller.marshal(jaxbElement, pw);
			
			
//			JAXBMarshaller marshaller = null;
//			
//	        if (pElementObject != null) {
//                context = (JAXBContext) JAXBContext.newInstance(pElementObject.getClass());
//                marshaller = context.createMarshaller();
//	        } else {
//                context = (JAXBContext) JAXBContext.newInstance(pObject.getClass());
//                marshaller = context.createMarshaller();
//	        }			
//	        
//	        marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);
//	        
//		    switch(type){
//		        case ".xml" :
//		        	System.out.println("@Formatter.doGet: case=XML");
//		        	response.setContentType("text/xml; charset=utf-8");	        	
//		        	break;
//		        case ".json" :
//		        	System.out.println("@Formatter.doGet: case=JSON");
//			        marshaller.setProperty("eclipselink.media-type", "application/json");
//			        marshaller.setProperty("eclipselink.json.include-root", false);
//			        response.setContentType("application/json; charset=utf-8");	 		        
//		        	break;
//		        case ".html" :
//		        	System.out.println("@Formatter.doGet: case=HTML");
//		        	response.setContentType("text/html; charset=utf-8");
//		        	pw.println("html response");
//		        	break;   
//		        case ".htm" :
//		        	System.out.println("@Formatter.doGet: case=HTM");
//		        	response.setContentType("text/html; charset=utf-8");
//		        	pw.println("HTML Response");
//		        	break; 		    	
//		    }
//		    
//		    marshaller.marshal(pObject, pw);				
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}	
}
