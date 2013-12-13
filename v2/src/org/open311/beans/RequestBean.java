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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="request")
public class RequestBean extends PostSuccessBean {

	@XmlElement(name="request_id")
	private String id;
	@XmlElement(name="service_request_id")
	private String serviceRequestID;
	private String status = "open";
	@XmlElement(name="status_notes")
	private String statusNotes;
	@XmlElement(name="service_name")
	private String serviceName;
	@XmlElement(name="service_code")
	private String serviceCode;
	private String description;
	@XmlElement(name="agency_responsible")
	private String agencyResponsible;
	@XmlElement(name="service_notice")
	private String serviceNotice;
	@XmlElement(name="requested_datetime")
	private String requestedDatetime;
	@XmlElement(name="updated_datetime")
	private String updatedDatetime;
	@XmlElement(name="expected_datetime")
	private String exptectedDatetime;
	private String address;
	@XmlElement(name="address_id")
	private String addressID;
	private String lat;
	private String lon;
	@XmlElement(name="media_url")
	private String mediaURL;
	private String email;
	@XmlElement(name="first_name")
	private String fName;
	@XmlElement(name="last_name")
	private String lName;
	@XmlElement(name="account_id")
	private String accountID;
	private String phone;
	//@XmlTransient
	//private ArrayList<RequestAttributeBean> attributes = new ArrayList<RequestAttributeBean>();
	@XmlTransient
	private HashMap<String, String[]> attributes;
	
	public String getServiceRequestID() {
		return serviceRequestID;
	}
	
	public void setServiceRequestID(String serviceRequestID) {
		this.serviceRequestID = serviceRequestID;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String pStatus) {
		if (pStatus.equals("0")) {
			this.status = "closed";
		} else {
			this.status = "open";
		}
	}
	
	public String getStatusNotes() {
		return statusNotes;
	}
	
	public void setStatusNotes(String statusNotes) {
		this.statusNotes = statusNotes;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}
	
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAgencyResponsible() {
		return agencyResponsible;
	}
	
	public void setAgencyResponsible(String agencyResponsible) {
		this.agencyResponsible = agencyResponsible;
	}
	
	public String getServiceNotice() {
		return serviceNotice;
	}
	
	public void setServiceNotice(String serviceNotice) {
		this.serviceNotice = serviceNotice;
	}
	
	public String getRequestedDatetime() {
		return requestedDatetime;
	}
	
	public void setRequestedDatetime(String requestedDatetime) {
		this.requestedDatetime = requestedDatetime;
	}
	
	public String getUpdatedDatetime() {
		return updatedDatetime;
	}
	
	public void setUpdatedDatetime(String updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}
	
	public String getExpectedDatetime() {
		return exptectedDatetime;
	}
	
	public void setExpectedDatetime(String exptectedDatetime) {
		this.exptectedDatetime = exptectedDatetime;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddressID() {
		return addressID;
	}
	
	public void setAddressID(String addressID) {
		this.addressID = addressID;
	}
	
	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getLon() {
		return lon;
	}
	
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	public String getMediaURL() {
		return mediaURL;
	}
	
	public void setMediaURL(String mediaURL) {
		this.mediaURL = mediaURL;
	}

	public HashMap<String, String[]> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String[]> attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String pID) {
		this.id = pID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		if (fName == null) {
			return;
		}
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}	
}
