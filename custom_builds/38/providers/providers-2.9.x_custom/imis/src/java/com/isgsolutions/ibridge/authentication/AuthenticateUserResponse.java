package com.isgsolutions.ibridge.authentication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuthenticateUserResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "authenticateUserResult" })
@XmlRootElement(name = "AuthenticateUserResponse")
public class AuthenticateUserResponse {

	@XmlElement(name = "AuthenticateUserResult")
	protected String authenticateUserResult;

	/**
	 * Gets the value of the authenticateUserResult property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAuthenticateUserResult() {
		return authenticateUserResult;
	}

	/**
	 * Sets the value of the authenticateUserResult property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAuthenticateUserResult(String value) {
		this.authenticateUserResult = value;
	}

}
