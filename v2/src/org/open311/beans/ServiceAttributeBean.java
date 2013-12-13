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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType (propOrder={"variable", "code", "datatype", "required", "datatypedescription", "order", "description", "values"})
public class ServiceAttributeBean {

	private boolean variable;
	private String code;
	private String datatype;
	private boolean required;
	@XmlElement(name="datatype_description")
	private String datatypedescription;
	private String order;
	private String description;
	@XmlElementWrapper(name="values")
	@XmlElement(name="value")
	private ArrayList<ServiceAttributeValueBean> values;
	
	public boolean isVariable() {
		return variable;
	}
	
	public void setVariable(boolean variable) {
		this.variable = variable;
	}
	
	public void setVariable(String pVariable) {
		if (pVariable == "0") {
			variable = false;
		} else {
			variable = true;
		}
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDatatype() {
		return datatype;
	}
	
	public void setDatatype(String pDataType) {
		this.datatype = pDataType;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public void setRequired(String pRequired) {
		if (pRequired == "0") {
			required = false;
		} else {
			required = true;
		}
	}
	
	public String getDatatypedescription() {
		return datatypedescription;
	}
	
	public void setDatatypedescription(String datatypedescription) {
		this.datatypedescription = datatypedescription;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<ServiceAttributeValueBean> getValues() {
		return values;
	}
	
	public void setValues(ArrayList<ServiceAttributeValueBean> values) {
		this.values = values;
	}	
}
