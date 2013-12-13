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
import javax.xml.bind.annotation.XmlType;

@XmlType (propOrder={"specification", "url", "changeset", "type", "formats"})
public class EndpointBean {

	private String specification;
	private String url;
	private String changeset;
	private String type;
	@XmlElementWrapper(name="formats")
	@XmlElement(name ="format")
	private List<String> formats;
	
	public String getSpecification() {
		return specification;
	}
	
	public void setSpecification(String spefification) {
		this.specification = spefification;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getChangeset() {
		return changeset;
	}
	
	public void setChangeset(String changeset) {
		this.changeset = changeset;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getFormats() {
		return formats;
	}
	
	public void setFormats(List<String> format) {
		this.formats = format;
	}	
	
}
