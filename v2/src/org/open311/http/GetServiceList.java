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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.open311.beans.ServiceListBean;
import org.open311.data.I311DAO;
import org.open311.data.Open311DAO;


/**
 * Servlet implementation class GetServiceList
 */
public class GetServiceList extends Generic311Servlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetServiceList() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);  
		
		System.out.println("@GetServiceList.doGet: BEGIN");
		
		I311DAO dao = new Open311DAO();
		List<ServiceListBean> sb = dao.onGetServiceList();
		
		if (sb != null) {
			request.setAttribute("payload", sb);		
			System.out.println("@GetServiceList.doGet: END");
			forwardSuccess(request, response, "ServiceList");
		} else {
			System.out.println("@GetServiceList.doGet: END");
			forwardError(request, response, "Could not find Service List", "400");
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
}
