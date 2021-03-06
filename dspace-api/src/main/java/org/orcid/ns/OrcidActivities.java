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
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}affiliations" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-works" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}funding-list" minOccurs="0"/>
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
    "affiliations",
    "orcidWorks",
    "fundingList"
})
@XmlRootElement(name = "orcid-activities")
public class OrcidActivities {

    protected Affiliations affiliations;
    @XmlElement(name = "orcid-works")
    protected OrcidWorks orcidWorks;
    @XmlElement(name = "funding-list")
    protected FundingList fundingList;

    /**
     * Gets the value of the affiliations property.
     * 
     * @return
     *     possible object is
     *     {@link Affiliations }
     *     
     */
    public Affiliations getAffiliations() {
        return affiliations;
    }

    /**
     * Sets the value of the affiliations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Affiliations }
     *     
     */
    public void setAffiliations(Affiliations value) {
        this.affiliations = value;
    }

    /**
     * Gets the value of the orcidWorks property.
     * 
     * @return
     *     possible object is
     *     {@link OrcidWorks }
     *     
     */
    public OrcidWorks getOrcidWorks() {
        return orcidWorks;
    }

    /**
     * Sets the value of the orcidWorks property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrcidWorks }
     *     
     */
    public void setOrcidWorks(OrcidWorks value) {
        this.orcidWorks = value;
    }

    /**
     * Gets the value of the fundingList property.
     * 
     * @return
     *     possible object is
     *     {@link FundingList }
     *     
     */
    public FundingList getFundingList() {
        return fundingList;
    }

    /**
     * Sets the value of the fundingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link FundingList }
     *     
     */
    public void setFundingList(FundingList value) {
        this.fundingList = value;
    }

}
