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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.open311.beans.RequestBean;
import org.open311.data.I311DAO;
import org.open311.data.Open311DAO;

/**
 * Servlet implementation class POSTServiceRequest
 */
public class PostServiceRequest extends Generic311Servlet {
	private static final long serialVersionUID = 1L;
	private I311DAO dao = new Open311DAO();
	HashMap<String, String[]> attr = new HashMap<String, String[]>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostServiceRequest() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/GetRequests");
		dispatcher.forward(request, response);
		return;
//		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		
		//get all possible values from request.
		System.out.println("@POSTServiceRequest.doPost: BEGIN");
		String lat = request.getParameter("lat");
		String lon = request.getParameter("long");
		String add = request.getParameter("address_string");
		String email = request.getParameter("email");
		String fName = request.getParameter("first_name");
		String lName = request.getParameter("last_name");
		String phone = request.getParameter("phone");
		String code = request.getParameter("service_code");
		String desc = request.getParameter("description");
		String mediaURL = request.getParameter("media_url");
		String accountID = request.getParameter("account_id");
		
		//get requested time.
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		df.setTimeZone(tz);
		Calendar date;
		date = DatatypeConverter.parseDateTime(df.format(new Date()));
		String requested = df.format(date.getTime());
		
		//create RequestBean and populate it with values from request.
		RequestBean rb = new RequestBean();
		rb.setLat(lat);
		rb.setLon(lon);
		rb.setAddress(add);
		rb.setServiceCode(code);
		rb.setDescription(desc);
		rb.setMediaURL(mediaURL);
		rb.setRequestedDatetime(requested);
		rb.setServiceNotice(I311DAO.DEFAULT_SERVICE_NOTICE);
		rb.setfName(fName);
		rb.setlName(lName);
		rb.setEmail(email);
		rb.setPhone(phone);
		rb.setAccountID(accountID);
		HashMap<String, String[]> ra = null;
		
		//a service code is required, so no further work is needed if one isn't present.  
		if (rb.getServiceCode() == null || rb.getServiceCode().equals("null")) {
			System.out.println("@PostServiceRequest.doPost: service_code is null forwarding error");
			forwardError(request, response, I311DAO.SERVICE_CODE_NOT_PRESENT, "400");
			return;
		} else {
			//if a service code was provided, use it to initiate a search of the post for any attributes.
			ra = fetchAttributes(rb.getServiceCode(), request, response);
			
			if (ra != null) {
				rb.setAttributes(ra);
			} else {
				System.out.println("@PostServiceRequest.doPost: " + I311DAO.REQUIRED_ATTRIBUTE_NOT_PRESENT);
				forwardError(request, response, I311DAO.REQUIRED_ATTRIBUTE_NOT_PRESENT, "400");
				return;
			}
		
		//once all data has been collected, it's time to try to enter the request.
		String result = dao.onPOSTServiceRequest(rb, attr);
		System.out.println("@PostServiceRequest.doPost: result of dao.onPostServiceRequest: " + result);
		
		if (!result.equals(I311DAO.SUCCESS)) {
			System.out.println("@POSTServiceRequest.doPost: post failed sending to formatter");
			System.out.println("@POSTServiceRequest.doPost: error code: " + result);
			forwardError(request, response, result, "400");
			return;
		} else {
			System.out.println("@POSTServiceRequest.doPost: post successful sending to formatter");
//			rb.setServiceCode("");
//			rb.setAddress("");
//			rb.setAttributes(null);
//			rb.setRequestedDatetime("");
//			rb.setServiceName("");
//			rb.setStatus("");
			request.setAttribute("payload", rb);
			System.out.println("@POSTServiceRequest.doPost: END");
			forwardSuccess(request, response, "POSTServiceRequest");
			return;		
			}
		}
	}
	
	/**
	 * 
	 * @param pServiceCode
	 * @param request
	 * @param response
	 * @return ArrayList<RequestAttributeBean
	 * @throws ServletException
	 * @throws IOException
	 * 
	 * This method searches the HTTP Post Request for attributes included by the client.
	 * It will use the I311DAO to contact the DB for all attributes for a given service.
	 * Then it will search the Post for any attributes matching an attribute code and 
	 * if found, create a RequestAttributeBean using the code and the information 
	 * associated with it.  After iteration has completed, a list of RequestAttributes 
	 * (if any) or null is returned. 
	 * 
	 */
	protected HashMap<String, String[]> fetchAttributes(String pServiceCode, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("@POSTServiceRequest.fetchAttributes: BEGIN");
		
		//first, get a list of ServiceAttributeBean(s) to iterate through.
		//second, search the request for the code of each attribute.
		//third, store the attribute values (if any) in a RequestAttributeBean.
		//fourth, return the list of beans (if any).
		
		ArrayList<String> codes = dao.onGetServiceAttributeCodes(pServiceCode);
		//attr = dao.onGetServiceAttributes(pServiceCode);
		HashMap<String, String[]> out = new HashMap<String, String[]>();
		System.out.println("@POSTServiceRequest.fetchAttributes: searching AttributeBeans for attribute codes");
		
		//iterate through the service attributes returned from our DB
		//get the attribute's code and search the Post for a corresponding
		//attribute.
		
		//HashMap<String, String[]> outAtr = new HashMap<String, ArrayList<String>>();
		//HashMap<String, String[]> inAttr = dao.onGetServiceAttributes(pServiceCode);
		//for (String k : inAttr.keySet())
		//	String[] values = (String[]) request.getParameterValues(k)
		//	if (values != null) rtnAtr.put(k, values);
		
		for (String code : codes) {
			//String c = ab.getCode();
			System.out.println("@POSTServiceRequest.fetchAttributes: searching Post for attribute code: " + code);
			String[] values = (String[]) request.getParameterValues(code);
			
			//if an attribute is found, create a RequestAttributeBean with the 
			//found information.  then store the bean in a list.
			if (values != null) {
				System.out.println("@PostServiceRequest.fetchAttributes: attribute found in Post.");				
				out.put(code, values);
			} else {
				System.out.println("@POSTServiceRequest.fetchAttributes: attribute not found");
			}
		}
		
		if (out.isEmpty()) {
			System.out.println("@POSTServiceRequest.fetchAttributes: END null");
			return null;
		} else {
			System.out.println("@POSTServiceRequest.fetchAttributes: END list of beans");
			return out;
		}
	}
}
