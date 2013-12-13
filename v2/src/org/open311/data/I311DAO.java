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

package org.open311.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.open311.beans.DefinitionBean;
import org.open311.beans.RequestBean;
import org.open311.beans.ServiceAttributeBean;
import org.open311.beans.ServiceListBean;


public interface I311DAO {
	
	public static final String DB_URL = "jdbc:mysql://pgh311.bitnamiapp.com:3306/pgh311";
	public static final String DB_UN = "city_0f_pittsbrg";
	public static final String DB_PW = "0penEII";
	
	public static final String GET_SERVICE_REQUESTS_ROOT = "SELECT * from service_request AS sr WHERE sr.service_request_requested between ";
	public static final String GET_SERVICE_REQUESTS_STATUS = " AND sr.service_request_status = ";
	public static final String GET_SERVICE_REQUESTS_CODE = " AND sr.service_request_code IN ";
	public static final String GET_SERVICE_REQUESTS_BY_ID = "SELECT * from service_request AS sr WHERE sr.service_request_id IN ";
	
	public static final String DEFAULT_SERVICE_NOTICE = "Your request has been processed";
	
	//int values representing the datatype of a ServiceAttribute
	public static final int ATTRIBUTE_DATATYPE_STRING = 0;
	public static final int ATTRIBUTE_DATATYPE_NUMBER = 1;
	public static final int ATTRIBUTE_DATATYPE_DATETIME = 2;
	public static final int ATTRIBUTE_DATATYPE_TEXT = 3;
	public static final int ATTRIBUTE_DATATYPE_SINGLEVALUELIST = 4;
	public static final int ATTRIBUTE_DATATYPE_MULTIVALUELIST = 5;
	
	//various error codes when trying to perform CRUD operations
	public static final String ER_CD = "ERROR_CODE";
	public static final String SUCCESS = "SUCCESS";
	public static final String SERVICE_CODE_NOT_PRESENT = "SERVICE_CODE_NOT_PRESENT";
	public static final String REQUIRED_ATTRIBUTE_NOT_PRESENT = "REQUIRED_ATTRIBUTE_NOT_PRESENT";
	public static final String LOCATION_PARAMETER_NOT_PRESENT = "LOCATION_PARAMETER_NOT_PRESENT";
	public static final String FAILED_TO_POST_REQUEST  = "FAILED_TO_POST_REQUEST";
	public static final String FAILED_TO_POST_REQUEST_ATTRIBUTE = "FAILED_TO_POST_REQUEST_ATTRIBUTE";
	public static final String FAILED_TO_POST_REQUEST_ATTRIBUTE_VALUE = "FAILED_TO_POST_REQUEST_ATTRIBUTE_VALUE";
	
	//-RETURNS: nothing
	public static final String POST_REQUEST_ATTRIBUTE = "INSERT INTO request_attribute " +
			"(" +
			"request_attribute_code, " +
			"request_attribute_key, " +
			"request_attribute_request_id)" +
			"VALUES";
	
	//RETURNS: nothing
	public static final String POST_REQUEST_ATTRIBUTE_VALUE = "INSERT INTO request_attribute_value " +
			"(" +
			"request_attribute_value_key, " + 
			"request_attribute_value_attribute_id) " +
			"VALUES";
	
	//-RETURNS: service_request_id and DEFAULT_SERVICE_NOTICE
	public static final String POST_SERVICE_REQUEST = "INSERT INTO service_request " +
			"(" +
			"service_request_address, " +
			"service_request_code, " +
			"service_request_description, " +
			"service_request_lat, " +
			"service_request_long, " +
			"service_request_media_url, " +
			"service_request_email, " +
			"service_request_first_name, " +
			"service_request_last_name, " +
			"service_request_account_id, " +
			"service_request_phone, " +
			"service_request_service_name, " +
			"service_request_service_notice, " +
			"service_request_requested" + ")" + 
			"VALUES";
	
	public static final String GET_SERVICE_ATTRIBUTE_CODES = 
			"SELECT sa.service_attribute_id " +
			"FROM service_attribute AS sa " +
			"WHERE sa.service_attribute_service_id = ";
	
	public static final String GET_REQUIRED_ATTRIBUTE_CODES = 
			"SELECT sa.service_attribute_id " +
			"FROM service_attribute AS sa " +
			"WHERE sa.service_attribute_required = 1 AND sa.service_attribute_service_id = ";
	
	//- RETURNS: service_id, service_name, service_description, service_metadata, service_type_content, service_group_content.	
	public static final String GET_SERVICE_LIST = 
		"SELECT s.service_id, s.service_name, s.service_description, s.service_metadata, st.service_type_content, sg.service_group_content " +
		"FROM service AS s " +
		"INNER JOIN service_type st " +
			"ON s.service_type = st.service_type_id " +
		"INNER JOIN service_group sg " +
			"ON s.service_group = sg.service_group_id";
	
	//- RETURNS: service_keyword(s) for a given service.
	public static final String GET_SERVICE_KEYWORDS = 
		"SELECT skw.service_keyword_content " +
		"FROM rel_service_keyword AS rkw " +
		"INNER JOIN service_keyword skw " +
			"ON rkw.rel_service_keyword_keyword_id = skw.service_keyword_id " +
		"WHERE rkw.rel_service_keyword_service_id = "; 	
	
	//-RETURNS: variable, code, datatype, required, datatype_description, order, required, and description for a given service code.
	public static final String GET_SERVICE_ATTRIBUTES = 
		"SELECT " +
			"sa.service_attribute_variable, " +
			"sa.service_attribute_id, " +
			"ad.attribute_datatype_content, " +
			"sa.service_attribute_required, " +
			"sa.service_attribute_datatype_description,  " +
			"sa.service_attribute_order, " +
			"sa.service_attribute_description " + 
		"FROM service_attribute AS sa " +
		"INNER JOIN attribute_datatype ad " +
			"ON sa.service_attribute_datatype = ad.attribute_datatype_id " +
		"WHERE sa.service_attribute_id =";
	
	//-RETURNS: values for a given service attribute
	public static final String GET_SERVICE_ATTRIBUTE_VALUES = 
		"SELECT sav.service_attribute_value_key, sav.service_attribute_value_name " +
		"FROM service_attribute_value AS sav " +
		"WHERE sav.service_attribute_value_attribute_id = ";
	
	//-RETURNS: values for a given request attribute
	public static final String GET_REQUTEST_ATTRIBUTE_VALUES = 
		"SELECT rav.attribute_value_key, rav.attribute_value_name " +
		"FROM request_attribute_value AS rav " +
		"WHERE rav.request_attribute_value_attribute_id = ";
	
	//- RETURNS: all not null fields for a given Service Request
	public static final String GET_SERVICE_REQUEST = 
		"SELECT * " +
		"FROM service_request AS sr " +
		"WHERE sr.service_request_id = ";

	String onPOSTServiceRequest(RequestBean pRB,  HashMap<String, String[]> pAttrs);
	ArrayList<String> onGetServiceAttributeCodes(String pServiceCode);
	boolean onServiceRequiresAttributes(String pServiceCode);
	//HashMap<String, String[]> onGetRequestAttributes(String pServiceCode);
	ArrayList<ServiceAttributeBean> onGetServiceAttributes(String pServiceCode);
	//HashMap<String, String> onGetServiceAttributeValues(String pAttributeID);
	DefinitionBean onGetServiceDefinition(String pServiceCode);
	ArrayList<ServiceListBean> onGetServiceList();
	ArrayList<RequestBean> onGetServiceRequest(String pServiceRequestID);
	ArrayList<RequestBean> onGetServiceRequests(String pStartDate, String pEndDate, String status, String[] pServiceCode, String[] pRequestIDs);
	String onGetServiceKeywords(String pServiceCode);
	
}
