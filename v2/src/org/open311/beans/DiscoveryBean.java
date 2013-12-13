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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="discovery")
@XmlType (propOrder={"changeset", "contact", "keyService", "endpoints"})
public class DiscoveryBean {

	@XmlElement(name="changeset")
	private String changeset;
	@XmlElement(name="contact")
	private String contact;	
	@XmlElement(name="key_service")
	private String keyService;
	@XmlElementWrapper(name="endpoints")
	@XmlElement(name="endpoint")
	private List<EndpointBean> endpoints;
	
	public String getChangeset() {
		return changeset;
	}
	
	public void setChangeset(String changeset) {
		this.changeset = changeset;
	}
	
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public String getkeyService() {
		return keyService;
	}
	
	public void setkeyService(String keyService) {
		this.keyService = keyService;
	}
	
	public List<EndpointBean> getEndpoints() {
		return endpoints;
	}
	
	public void setEndpoints(List<EndpointBean> endpoints) {
		this.endpoints = endpoints;
	}
	
}
