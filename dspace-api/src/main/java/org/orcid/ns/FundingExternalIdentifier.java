//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.21 at 10:22:56 AM CEST 
//


package org.orcid.ns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}funding-external-identifier-type"/>
 *         &lt;element name="funding-external-identifier-value" type="{http://www.orcid.org/ns/orcid}string2084" minOccurs="0"/>
 *         &lt;element name="funding-external-identifier-url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fundingExternalIdentifierType",
    "fundingExternalIdentifierValue",
    "fundingExternalIdentifierUrl"
})
@XmlRootElement(name = "funding-external-identifier")
public class FundingExternalIdentifier {

    @XmlElement(name = "funding-external-identifier-type", required = true)
    protected String fundingExternalIdentifierType;
    @XmlElement(name = "funding-external-identifier-value")
    protected String fundingExternalIdentifierValue;
    @XmlElement(name = "funding-external-identifier-url")
    @XmlSchemaType(name = "anyURI")
    protected String fundingExternalIdentifierUrl;

    /**
     * Gets the value of the fundingExternalIdentifierType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFundingExternalIdentifierType() {
        return fundingExternalIdentifierType;
    }

    /**
     * Sets the value of the fundingExternalIdentifierType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingExternalIdentifierType(String value) {
        this.fundingExternalIdentifierType = value;
    }

    /**
     * Gets the value of the fundingExternalIdentifierValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFundingExternalIdentifierValue() {
        return fundingExternalIdentifierValue;
    }

    /**
     * Sets the value of the fundingExternalIdentifierValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingExternalIdentifierValue(String value) {
        this.fundingExternalIdentifierValue = value;
    }

    /**
     * Gets the value of the fundingExternalIdentifierUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFundingExternalIdentifierUrl() {
        return fundingExternalIdentifierUrl;
    }

    /**
     * Sets the value of the fundingExternalIdentifierUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingExternalIdentifierUrl(String value) {
        this.fundingExternalIdentifierUrl = value;
    }

}
