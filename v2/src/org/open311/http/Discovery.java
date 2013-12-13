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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.open311.beans.DiscoveryBean;

/**
 * Servlet implementation class Test
 */
public class Discovery extends Generic311Servlet {
       
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public Discovery() {
        super();
    }

	/**
	 * @throws JAXBException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		super.doGet(request, response);
 
        DiscoveryBean db = null;         
	
		try {
			String path = getServletContext().getRealPath("/WEB-INF/Discovery.xml");
	        JAXBContext context = (JAXBContext) JAXBContext.newInstance(DiscoveryBean.class);
	        JAXBUnmarshaller unmarshaller = context.createUnmarshaller();
	        File xml = new File(path);
	        db = (DiscoveryBean) unmarshaller.unmarshal(xml);
		} catch (JAXBException e) {
			e.printStackTrace();
		}			

        if (db != null) {
        	request.setAttribute("payload", db);
        	forwardSuccess(request, response, "Discovery");
        	return;
        } else {
        	forwardError(request, response, "Could not find Discovery", "400");
        	return;
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
