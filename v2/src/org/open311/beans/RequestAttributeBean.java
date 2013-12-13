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

package org.open311.beans;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlTransient;

public class RequestAttributeBean {
	private String requestID;
	private HashMap<String, String[]> attributes = new HashMap<String, String[]>();
	//private String code;
	//private ArrayList<String> attributes = new ArrayList<String>();
	@XmlTransient
	private boolean requiresAttributes;

	public String getRequestID() {
		return requestID;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public HashMap<String, String[]> getAttributes() {
		return attributes;
	}
	
//	public String[] getAttributesAsString() {
//		String[] attrib = new String[attributes.size()];
//		return attributes.toArray(attrib);
//	}
//	
//	public void setAttributes(ArrayList<String> pAttributes) {
//		this.attributes = pAttributes;
//	}
//
//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}

	public boolean requiresAttributes() {
		return requiresAttributes;
	}

	public void requiresAttributes(boolean requiresAttributes) {
		this.requiresAttributes = requiresAttributes;
	}
}
