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
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-identifier"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}last-modified-date"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}credit-name" minOccurs="0"/>
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
    "orcidIdentifier",
    "lastModifiedDate",
    "creditName"
})
@XmlRootElement(name = "delegate-summary")
public class DelegateSummary {

    @XmlElement(name = "orcid-identifier", required = true)
    protected OrcidId orcidIdentifier;
    @XmlElement(name = "last-modified-date", required = true)
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "credit-name")
    protected CreditName creditName;

    /**
     * Gets the value of the orcidIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link OrcidId }
     *     
     */
    public OrcidId getOrcidIdentifier() {
        return orcidIdentifier;
    }

    /**
     * Sets the value of the orcidIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrcidId }
     *     
     */
    public void setOrcidIdentifier(OrcidId value) {
        this.orcidIdentifier = value;
    }

    /**
     * Gets the value of the lastModifiedDate property.
     * 
     * @return
     *     possible object is
     *     {@link LastModifiedDate }
     *     
     */
    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Sets the value of the lastModifiedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link LastModifiedDate }
     *     
     */
    public void setLastModifiedDate(LastModifiedDate value) {
        this.lastModifiedDate = value;
    }

    /**
     * Gets the value of the creditName property.
     * 
     * @return
     *     possible object is
     *     {@link CreditName }
     *     
     */
    public CreditName getCreditName() {
        return creditName;
    }

    /**
     * Sets the value of the creditName property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditName }
     *     
     */
    public void setCreditName(CreditName value) {
        this.creditName = value;
    }

}
