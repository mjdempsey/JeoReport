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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.open311.data.I311DAO;


/**
 * Servlet implementation class Generic311Servlet
 */
public abstract class Generic311Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Generic311Servlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		if (!url.contains(".")) {			
			forwardError(request, response, "There was an error attempting to fulfill your request.  Possibly due to a badly formed URL.", "400");
		}		
		String format = url.substring(url.lastIndexOf("."), url.length());
		request.setAttribute("format", format);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		if (!url.contains(".")) {
			forwardError(request, response, "There was an error attempting to fulfill your request.  Possibly due to a badly formed URL.", "400");
			return;
		}
		
		String format = url.substring(url.lastIndexOf("."), url.length());
		request.setAttribute("format", format);
	}

	protected void forwardError(HttpServletRequest request, HttpServletResponse response, String pErrorText, String pErrorCode) throws ServletException, IOException {
		System.out.println("@Generic311Servlet.forwardError: BEGIN");
		request.setAttribute("requestor", "ERROR");
		request.setAttribute("40X", pErrorCode);
		request.setAttribute(I311DAO.ER_CD, pErrorText);
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/FORMATTER");
		System.out.println("@Generic311Servlet.forwardError: END");
		dispatcher.forward(request, response);
		return;
	}
	
	protected void forwardSuccess(HttpServletRequest request, HttpServletResponse response, String pRequestor) throws ServletException, IOException {
		System.out.println("@Generic311Servlet.forwardSuccess: BEGIN");
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/FORMATTER");
		request.setAttribute("requestor", pRequestor);
		System.out.println("@Generic311Servlet.forwardSuccess: END");
		dispatcher.forward(request, response);
		return;
	}
	
}
