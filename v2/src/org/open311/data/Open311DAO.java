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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.open311.beans.DefinitionBean;
import org.open311.beans.RequestAttributeBean;
import org.open311.beans.RequestBean;
import org.open311.beans.ServiceAttributeBean;
import org.open311.beans.ServiceAttributeValueBean;
import org.open311.beans.ServiceListBean;


/**
 * The Open311DAO DOES:
 * 		Encapsulate all MySQL to one location in the source.
 * 		Accept String varargs and returns a single MySQL safe, single-quote wrapped string.
 * 		Handle all read/write operations necessary to implement GeoReport v2.
 * 		Use PreparedStatements to negate the threat of SQL Injection attacks.
 * 
 * The Open311DAO does NOT:
 * 		Provide any data validation for your database.  
 * 		Ensure thread safety. 
 * 		Pool connections.  
 */

public class Open311DAO implements I311DAO {
	
	/**
	 * Returns all service attribute codes for a given service code.
	 */
	@Override
	public ArrayList<String> onGetServiceAttributeCodes(String pServiceCode) {
		Connection c = onGetDBConnection();
		ArrayList<String> codes = new ArrayList<String>();
		
		try {
			String query = I311DAO.GET_SERVICE_ATTRIBUTE_CODES + pServiceCode;		
			PreparedStatement stmt = c.prepareStatement(query);
			System.out.println("@Open311DAO.onGetServiceAttributeCodes: executing query: " + query);
			ResultSet rs = stmt.executeQuery(query) ;
			
			while (rs.next()) {					
				String code = rs.getString("service_attribute_id");
				codes.add(code);
				System.out.println("@Open311DAO.onGetServiceAttributeCodes: code found: " + code);
			}
			
			c.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (codes.isEmpty()) {
			return null;
		} else {
			return codes;
		}
	}
	
	/**
	 * SEE: Open311DAO.onPostServiceRequest(RequestBean pRB, HashMap<String, String[] pAttrs)
	*/
	public String onPOSTServiceRequest(RequestBean pRB) {
		return onPOSTServiceRequest(pRB, null);
	}
	
	/**
	 * Queries the database for required attribute codes when given a service code.  The 
	 * result is true if a result is found.
	 */
	@Override
	public boolean onServiceRequiresAttributes(String pServiceCode) {
		if (onGetRequiredAttributeCodes(pServiceCode) == null) {
			System.out.println("@Open311DAO.onServiceRequiresAttributes: " + pServiceCode + " requires attributes");
			return false;
		} else {
			System.out.println("@Open311DAO.onServiceRequiresAttributes: " + pServiceCode + " doesn't require attributes");
			return true;
		}
	}
		
	/**
	 * Accepts {@link RequestBean} and extracts the necessary information to perform an INSERT into the database.  
	 * if attributes are included, the {@link RequestBean} is then passed to the Open311DAO.onPOSTRequest() method.
	 */
	public String onPOSTServiceRequest(RequestBean pRB, HashMap<String, String[]> pAttrs) {
		System.out.println("@Open311DAO.onPOSTServiceRequest BEGIN");
		//step 1: verify a service code.
		if (pRB.getServiceCode() == null) {
			return SERVICE_CODE_NOT_PRESENT;
		}

		//step 2: verify attributes are present, if required.  we verify this by seeing if any attributes are listed for the service code.
		if (pRB == null || pRB.getAttributes() == null || pRB.getAttributes().isEmpty() && onServiceRequiresAttributes(pRB.getServiceCode())) {
			System.out.println("@Open311DAO.onPOSTServiceRequest " + REQUIRED_ATTRIBUTE_NOT_PRESENT);
			return REQUIRED_ATTRIBUTE_NOT_PRESENT;
		}
				
		//step 3: verify a location parameter is present.  
		if (pRB.getLat() == null && pRB.getLon() == null && pRB.getAddress() == null) {
			return LOCATION_PARAMETER_NOT_PRESENT;
		}
		
		Connection c = onGetDBConnection();		
		if (c != null) {
			try {
				//generate a query string.  start with default post string.
				//then add each field of the request other than the id of 
				//the request, as it doesn't exist yet.
				String query = I311DAO.POST_SERVICE_REQUEST; 
				String values = "( "; 
				values += Open311DAO.getSQLSafeCSV(
					pRB.getAddress(), 
					pRB.getServiceCode(), 
					pRB.getDescription(), 
					pRB.getLat(), 
					pRB.getLon(), 
					pRB.getMediaURL(),
					pRB.getEmail(),
					pRB.getfName(), 
					pRB.getlName(),
					pRB.getAccountID(), 
					pRB.getPhone(),
					pRB.getServiceName(),
					pRB.getServiceNotice(),
					pRB.getRequestedDatetime());
				values += ")";
				
				//add query to values and execute.  
				query += values;				
				PreparedStatement stmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				System.out.println("@Open311DAO.onPOSTServiceRequest: executing query: " + query);
				int rows = stmt.executeUpdate();
				ResultSet keys = stmt.getGeneratedKeys();
				
				//if insert is successful, there will be an ID of the row created.  this is 
				//needed to insert any attributes associated with the service request.
				if (rows > 0) {
					System.out.println("@Open311DAO.onPOSTServiceRequest: Data successfully inserted");
					while (keys.next()) {
						//set id of request.  this will then cascade down to any attributes 
						//associated with the request.
						System.out.println("@Open311DAO.onPOSTServiceRequest: Request Code is: " + keys.getInt(1));
						pRB.setId(Integer.toString(keys.getInt(1)));
					}
				} else {
					System.out.println("@Open311DAO.onPOSTServiceRequest: " + FAILED_TO_POST_REQUEST);
					return FAILED_TO_POST_REQUEST;
				}
				c.close();
				
				//if attributes isn't empty, post attribute value
				if (!pRB.getAttributes().isEmpty()) {
					String val = onPOSTRequestAttribute(pRB.getServiceCode(), pRB.getId(), pRB.getAttributes());
					System.out.println("@Open311DAO.onPOSTServiceRequest: " + val);
					return val;
				} else {
					return SUCCESS;
				}
										
			} catch (SQLException e) {
				e.printStackTrace();
				return FAILED_TO_POST_REQUEST;
			}
		}
		System.out.println("@Open311DAO.onPOSTServiceRequest: END");
		return FAILED_TO_POST_REQUEST;
	}
	
	/**
	 * @param String pServiceCode 
	 * 
	 * Given a service code, this method will select all rows listing the code and 
	 * use the ResultSet to instantiate an ArrayList of ServiceAtttributeBean(s) 
	 * which it then returns.  
	 */
	@Override
	public ArrayList<ServiceAttributeBean> onGetServiceAttributes(String pServiceCode) {
		System.out.println("@Open311DAO.onGetServiceAttributes: BEGIN");
		System.out.println("@Open311DAO.onGetServiceAttributes: Searching for attributes for service: " + pServiceCode);
		String sc = pServiceCode;
		Connection c = onGetDBConnection();
		ArrayList<ServiceAttributeBean> ab = new ArrayList<ServiceAttributeBean>();
		
		if (c != null) {
			try {
				//the only information added to the query should be the id of the service attribute.
				String query = I311DAO.GET_SERVICE_ATTRIBUTES + sc;
				PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = stmt.executeQuery(query);
				
				//for every row found, extract the information from the ResultSet and use it to 
				//instantiate a ServiceAttributeBean.
				while (rs.next()) {
					System.out.println("@Open311DAO.onGetServiceAttributes: attributes found for service: " + pServiceCode);
					ServiceAttributeBean a = new ServiceAttributeBean();
					a.setCode(rs.getString("service_attribute_id"));
					a.setDatatype(rs.getString("attribute_datatype_content"));
					a.setDatatypedescription(rs.getString("service_attribute_datatype_description"));
					a.setDescription(rs.getString("service_attribute_description"));
					a.setOrder(rs.getString("service_attribute_order"));
					a.setRequired(rs.getString("service_attribute_required"));					
					a.setVariable(rs.getString("service_attribute_variable"));
					a.setValues(onGetServiceAttributeValues(a.getCode()));
					ab.add(a);					
				}
				
				c.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("@Open311DAO.onGetServiceAttributes: END SQL problem");
				return null;
			}
		} else {
			System.out.println("@Open311DAO.onGetServiceAttributes: END connection null");
			return null;
		}
		
		if (!ab.isEmpty()) {
			System.out.println("@Open311DAO.onGetServiceAttributes: END returning attributes");
			return ab;
		} else {
			System.out.println("@Open311DAO.onGetServiceAttributes: END returning null");
			return null;
		}
	}
	
	/**
	 * @param String pAttributeID
	 * 
	 * Returns an ArrayList of ServiceAttributeValue(s) representing values associated with a given 
	 * service attribute (if any).   
	 */
	public ArrayList<ServiceAttributeValueBean> onGetServiceAttributeValues(String pAttributeID) {
		System.out.println("@Open311DAO.onGetServiceAttributeValues: BEGIN");
		String id = pAttributeID;
		Connection c = onGetDBConnection();
		ArrayList<ServiceAttributeValueBean> rslt = new ArrayList<ServiceAttributeValueBean>();
		
		if (c  != null) {
			try {
				String query = I311DAO.GET_SERVICE_ATTRIBUTE_VALUES + id;		
				PreparedStatement stmt = c.prepareStatement(query);
				System.out.println("@Open311DAO.onGetServiceAttributeValues: executing query: " + query);
				ResultSet rs = stmt.executeQuery(query) ;
				
				while (rs.next()) {	
					ServiceAttributeValueBean sav = new ServiceAttributeValueBean();					 
				    String name = rs.getString("service_attribute_value_name");
				    String key = rs.getString("service_attribute_value_key");
					sav.setName(name);
					sav.setKey(key);
					rslt.add(sav);
					System.out.println("@Open311DAO.onGetServiceAttributeValues: attribute value found key: " + key + " name: " + name);
				}
				
				c.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("@Open311DAO.onGetServiceAttributeValues: END = " + rslt);
		return rslt;
	}

	/**
	 * @param String pServiceCode
	 * 
	 * Returns an instantiated {@link DefinitionBean} when given a service code.
	 * It should also be noted that any attributes belonging to the service will 
	 * be instantiated and included as well.
	 */
	@Override
	public DefinitionBean onGetServiceDefinition(String pServiceCode) {
		DefinitionBean db = new DefinitionBean();
		
		ArrayList<ServiceAttributeBean> sa = onGetServiceAttributes(pServiceCode);
		if (!(sa == null)) {
			db.setServiceCode(pServiceCode);
			db.setAttributes(sa);
			return db;
		} else {
			return null;
		}
	}

	/**
	 * For every service listed in the database, this method will 
	 * gather the required information to instantiate a {@link ServicesBean}
	 * that will contain all required information about each service
	 * in the form of a {@link DefinitionBean}.  
	 */
	@Override
	public ArrayList<ServiceListBean> onGetServiceList() {
		System.out.println("@Open311DAO.onGetServiceList: BEGIN");
		Connection c = onGetDBConnection();
		ArrayList<ServiceListBean> beans = new ArrayList<ServiceListBean>();
		
		if (c != null) {
			try {
				String query = I311DAO.GET_SERVICE_LIST;		
				PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = stmt.executeQuery(query);
				
				while (rs.next()) {
					System.out.println("@Open311DAO.onGetServiceList: Creating ServiceListBean");
					ServiceListBean slb = new ServiceListBean();
					slb.setServiceCode(rs.getString("service_id"));
					slb.setServiceName(rs.getString("service_name"));
					slb.setServiceDesc(rs.getString("service_description"));
					slb.setMetadata(rs.getString("service_metadata"));
					slb.setType(rs.getString("service_type_content"));
					slb.setGroup(rs.getString("service_group_content"));
					slb.setKeywords(onGetServiceKeywords(slb.getServiceCode()));
					beans.add(slb);
				}
				
				c.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (!beans.isEmpty()) {
			System.out.println("@Open311DAO.onGetServiceList: END returning service definition");
			return beans;
		} else {
			System.out.println("@Open311DAO.onGetServiceList: END returning null");
			return null;
		}
	}

	/**
	 * When given a request id, this method will find the record in the 
	 * database and leverage the information returned to instantiate 
	 * a {@link RequestBean} to return.
	 */
	@Override
	public ArrayList<RequestBean> onGetServiceRequest(String pServiceRequestID) {
		Connection c = onGetDBConnection();
		ArrayList<RequestBean> beans = new ArrayList<RequestBean>();
		
		if (c != null) {
			try {
				String query = I311DAO.GET_SERVICE_REQUEST	+ pServiceRequestID;	
				PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = stmt.executeQuery(query) ;
				
				while (rs.next()) {
					RequestBean b = new RequestBean();
					b.setAddress(rs.getString("service_request_address"));
					b.setAgencyResponsible(rs.getString("service_request_agency"));
					b.setDescription(rs.getString("service_request_description"));
					b.setExpectedDatetime(rs.getString("service_request_expected"));
					b.setLat(rs.getString("service_request_lat"));
					b.setLon(rs.getString("service_request_long"));
					b.setMediaURL(rs.getString("service_request_media_url"));
					b.setRequestedDatetime(rs.getString("service_request_requested"));
					b.setServiceCode(rs.getString("service_request_code"));					
					b.setServiceRequestID(rs.getString("service_request_id"));
					b.setStatus(rs.getString("service_request_status"));
					//TODO: add status_notes and service_name 
					//b.setStatusNotes(rs.getString(""));
					//b.setServiceName(rs.getString("service_name"));
					b.setServiceNotice(rs.getString("service_request_service_notice"));
					b.setUpdatedDatetime(rs.getString("service_request_updated"));
					beans.add(b);
				}
				
				c.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (beans.isEmpty()) {
			return null;
		} else {			
			return beans;
		}
	}

	/**
	 * With some added functionality, this method will prioritize which 
	 * parameters provided will be used to gather the requested information.
	 */
	public ArrayList<RequestBean> onGetServiceRequests(String pStartDate, String pEndDate, String pStatus, String[] pServiceCodes, String[] ids){
		Connection c = onGetDBConnection();
		if (c != null) {
			try {				
				//if there ID attributes present no other attributes need to be evaluated
				//gather them into one string, and execute the query.
				if (ids != null) {
					String query = I311DAO.GET_SERVICE_REQUESTS_BY_ID;
					String IDs = "(";					
					IDs += getSQLSafeCSV(ids);
					query += IDs + ")";
					System.out.println("@Open311DAO.onGetServiceRequests: executing query" + query);
					PreparedStatement stmt = c.prepareStatement(query);
					ResultSet rs = stmt.executeQuery(query);
					return RStoRB(rs);																 
				}
				
				System.out.println("@Open311DAO.onGetServiceRequests: building default query");
				//if there are no ID attributes, a conventional query needs to be built.  step one is setting the dates
				String query = I311DAO.GET_SERVICE_REQUESTS_ROOT + "'" + pStartDate + "' AND '" + pEndDate + "'";
				
				//step two is adding a status, if included.
				if (pStatus != null) {
					System.out.println("@Open311DAO.onGetServiceRequests: requested status detected");
					if (pStatus.equals("open")) {
						System.out.println("@Open311DAO.onGetServiceRequests: requested status open");
						query += I311DAO.GET_SERVICE_REQUESTS_STATUS + "1";
					} else if (pStatus.equals("closed")) {
						System.out.println("@Open311DAO.onGetServiceRequests: requested status closed");
						query += I311DAO.GET_SERVICE_REQUESTS_STATUS + "0";
					}
				}
				
				//step three is to add any service codes to the query
				if (pServiceCodes != null) {
					System.out.println("@Open311DAO.onGetServiceRequests: service codes detected");
					query += GET_SERVICE_REQUESTS_CODE + "(";	
					for (int i = 0; i<pServiceCodes.length; i++) {	
						query += pServiceCodes[i];
						if (i < pServiceCodes.length - 1) {
							query += ", ";
						}
					}
					query += ")";
				}
								
				System.out.println("@Open311DAO.onGetServiceRequests: executing query: " + query);				
				PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = stmt.executeQuery(query) ;
				
				if (rs.next()) {
					System.out.println("@Open311DAO.onGetServiceRequests: requests found");
					ArrayList<RequestBean> list = RStoRB(rs);
					c.close();
					return list;
				} else {
					System.out.println("@Open311DAO.onGetServiceRequests: no requests found");
					c.close();
					return null;
				}				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("@Open311DAO.onGetServiceRequests returning null because of MySQL connection issue.");
		return null;
	}
	
	/**
	 * Returns any keywords associated with a particular service code.
	 */
	@Override
	public String onGetServiceKeywords(String pServiceCode) {
		String rslt = null;
		
		Connection c = onGetDBConnection();
		if (c != null) {
			try {
				String query = I311DAO.GET_SERVICE_KEYWORDS + pServiceCode;
				PreparedStatement ps = c.prepareStatement(query);
				ResultSet rs = ps.executeQuery(query);
				
				while (rs.next()) {
					rslt += ", " + rs.getString("service_keyword_content");
				}
				
				c.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return rslt.substring(6, rslt.length());
	}

	protected Connection onGetDBConnection() {
		System.out.println("@Open311DAO.onGetConnection: BEGIN");
		Connection c = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
		}
	 
		try {
			c = DriverManager
			.getConnection(I311DAO.DB_URL, I311DAO.DB_UN, I311DAO.DB_PW);
			
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		System.out.println("@Open311DAO.onGetConnection: END");
		return c;
	}

	/**
	 * Convenience method to instantiate RequestBean(s) with a ResultSet.
	 * @throws SQLException 
	 */
	protected ArrayList<RequestBean> RStoRB(ResultSet rs) throws SQLException {
		System.out.println("@Open311DAO.RStoRB: BEGIN");
			
		ArrayList<RequestBean> rb = new ArrayList<RequestBean>();
			
		while (rs.next()) {
			System.out.println("@Open311DAO.RStoRB: building RequestBean and adding to ArrayList");
			RequestBean b = new RequestBean();						
			b.setServiceRequestID(rs.getString("service_request_id"));
			System.out.println(rs.getString("service_request_status"));
			b.setStatus(rs.getString("service_request_status"));			
			b.setServiceNotice(rs.getString("service_request_service_notice"));
			b.setEmail(rs.getString("service_request_email"));
			b.setfName(rs.getString("service_request_first_name"));
			b.setlName(rs.getString("service_request_last_name"));
			b.setAccountID(rs.getString("service_request_account_id"));
			b.setPhone(rs.getString("service_request_phone"));
			b.setServiceName(rs.getString("service_request_service_name"));
			b.setServiceCode(rs.getString("service_request_code"));
			b.setDescription(rs.getString("service_request_description"));
			b.setRequestedDatetime(rs.getString("service_request_requested"));
			b.setUpdatedDatetime(rs.getString("service_request_updated"));						
			b.setExpectedDatetime(rs.getString("service_request_expected"));
			b.setAddress(rs.getString("service_request_address"));
			b.setLat(rs.getString("service_request_lat"));
			b.setLon(rs.getString("service_request_long"));
			b.setMediaURL(rs.getString("service_request_media_url"));	
			rb.add(b);
		}		
		
		if (rb.isEmpty()) {
			System.out.println("@Open311DAO.RStoRB: END returning null");
			return null;
		} else {
			System.out.println("@Open311DAO.RStoRB: END returning beans");
			return rb;
		}		
	}
	
	/**
	 * 
	 * @param pRAB ArrayList of {@link RequestAttributeBean}
	 * @return status String
	 * 
	 * Accepts an ArrayList of {@link RequestAttributeBean}(s) and inserts them into the database.  If successful,
	 * this method will return the {@link I311DAO.SUCCESS} String which will be returned by the calling method (onPostServiceRequest()
	 */
	protected String onPOSTRequestAttribute(String pServiceCode, String pRequestID, HashMap<String, String[]> pRAB) {
		System.out.println("@Open311DAO.onPOSTServiceRequestAttribute BEGIN");
		Connection c = onGetDBConnection();
		String val = "";
		if (c  != null) {
			try {
				
				//insert each request attribute in the database
				//the query will be the default plus the service code of the parent
				//request, the actual value submitted, and the id fo the parent request.
				//for clarification: this is a HashMap containing the String key for each attribute code and
				//an array of String(s) for each attribute code
				for (String code : pRAB.keySet()) {
					for (int i = 0; i < pRAB.get(code).length; i++) {						
						String value = pRAB.get(code)[i];						
						String query = I311DAO.POST_REQUEST_ATTRIBUTE;
						String[] values = new String[] {pServiceCode, value, pRequestID};
						query += " (";
						query += Open311DAO.getSQLSafeCSV(values);
						query += ")";
						
						System.out.println("@Open311DAO.onPOSTRequestAttribute: query = " + query);
						PreparedStatement stmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
						stmt.executeUpdate();
						ResultSet keys = stmt.getGeneratedKeys();
												
						//if a single attribute fails to insert, the entire process has failed.
						//this check will stop iteration and allow the method to return the 
						//failure message.
						if (!keys.next()) {
							System.out.println("@Open311DAO.onPOSTServiceRequestAttribute " + FAILED_TO_POST_REQUEST_ATTRIBUTE);
							val = FAILED_TO_POST_REQUEST_ATTRIBUTE;
							break;
						} 						
					}					
				}
									
				val = SUCCESS;
				c.close();					
				System.out.println("@Open311DAO.onPOSTServiceRequestAttribute: Insert successful.");				
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("@Open311DAO.onPOSTServiceRequestAttribute: END.");
		return val;		
	}

	/**
	 * @param pServiceCode
	 * @return
	 * 
	 * Returns all service codes for a given service code. 
	 */
	protected ArrayList<String> onGetRequiredAttributeCodes(String pServiceCode) {		
		Connection c = onGetDBConnection();
		ArrayList<String> codes = new ArrayList<String>();
		if (c != null) {
			String query = I311DAO.GET_REQUIRED_ATTRIBUTE_CODES + pServiceCode;			
			PreparedStatement stmt;			
			try {
				stmt = c.prepareStatement(query);
				ResultSet rs = stmt.executeQuery(query);
				
				while (rs.next()) {
					String code = rs.getString("service.attribute.id");
					if (!code.equals("null")) {
						codes.add(code);
					}
				}
				
				c.close();
				
			} catch (SQLException e) {			
				e.printStackTrace();
			}	
		}
		
		if (!codes.isEmpty()) {
			return codes;
		} else {
			return null;
		}
	}
	
	/**
	 * Convenience method accepting vararg String parameters and returning a single
	 * String where all parameters have been wrapped in single-quotes and separated by commas.
	 */
	public static String getSQLSafeCSV(String...pVal) {
		String rtn = "";
		for (int i = 0; i < pVal.length; i++) {
			rtn += "'" + pVal[i] + "'";
			if (!(i >= pVal.length - 1)) {
				rtn += ", ";
			}
		}
		return rtn;
	}
}
