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
 * Servlet implementation class GetServiceRequests
 */
public class GetServiceRequests extends Generic311Servlet {
	private static final long serialVersionUID = 1L;
	private I311DAO dao = new Open311DAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetServiceRequests() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		
		String[] ids = request.getParameterValues("service_request_id");
		String start = request.getParameter("start_date");
		String end = request.getParameter("end_date");
		String[] codes = request.getParameterValues("service_code");
		String status = request.getParameter("status");
	
		ArrayList<String> dates = setDates(start, end);
		ArrayList<RequestBean> b = dao.onGetServiceRequests(dates.get(0), dates.get(1), status, codes, ids);
		
		if (b == null) {
			forwardError(request, response, "Could not find Service Request", "400");
		} else {		
			request.setAttribute("payload", b);		
			forwardSuccess(request, response, "GetServiceRequests");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
				
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/Post");
		dispatcher.forward(request, response);
	}
		
	protected String getDate(String pDate, int pIncrement) {
		String rtn = "";
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		df.setTimeZone(tz);
		Calendar date;
		
		if (pDate == null) {
			date = DatatypeConverter.parseDateTime(df.format(new Date()));
		} else {
			date = DatatypeConverter.parseDateTime(pDate);
		}
		
		date.add(Calendar.DAY_OF_YEAR, pIncrement);
		Date stop = date.getTime();
		rtn = df.format(stop);
		return rtn;
	}
	
	protected ArrayList<String> setDates(String pStart, String pEnd) {
		String start = pStart;
		String end = pEnd;
		
		if (start == null && end == null) {
			end = getDate(null, 0);
			start = getDate(end, -90);		
			//System.out.println("@GetServiceRequests.setDates() -> no dates supplied");
			//System.out.println("@GetServiceRequests.setDates() -> start set to " + start + " end set to " + end);
		} else if (start == null && end != null) {
			start = getDate(end, -90);
			
		} else if (end == null && start != null){
			end = getDate(start, 90);		
		}
		
		ArrayList<String> dates = new ArrayList<String>();
		dates.add(start);
		dates.add(end);
		return dates;
	}
}
