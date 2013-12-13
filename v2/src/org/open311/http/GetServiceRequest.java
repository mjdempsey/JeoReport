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
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.open311.beans.RequestBean;
import org.open311.data.Open311DAO;


/**
 * Servlet implementation class GetServiceRequest
 */
public class GetServiceRequest extends Generic311Servlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetServiceRequest() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		
		System.out.println("@GetServiceRequests.doGet: BEGIN");
		
	    String url = request.getRequestURL().toString();
		String code = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
		
		Open311DAO dao = new Open311DAO();
		ArrayList<RequestBean> rb =  dao.onGetServiceRequest(code);
		
		if (rb == null) {
			System.out.println("@GetServiceRequests.doGet: END error");
			forwardError(request, response, "Could not find Service Request", "400");
		} else {
			System.out.println("@GetServiceRequests.doGet: END success");
			request.setAttribute("payload", rb);		
			forwardSuccess(request, response, "GetServiceRequest");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
