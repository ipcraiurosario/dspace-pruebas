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
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}relevancy-score"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-profile"/>
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
    "relevancyScore",
    "orcidProfile"
})
@XmlRootElement(name = "orcid-search-result")
public class OrcidSearchResult {

    @XmlElement(name = "relevancy-score", required = true)
    protected RelevancyScore relevancyScore;
    @XmlElement(name = "orcid-profile", required = true)
    protected OrcidProfile orcidProfile;

    /**
     * Gets the value of the relevancyScore property.
     * 
     * @return
     *     possible object is
     *     {@link RelevancyScore }
     *     
     */
    public RelevancyScore getRelevancyScore() {
        return relevancyScore;
    }

    /**
     * Sets the value of the relevancyScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelevancyScore }
     *     
     */
    public void setRelevancyScore(RelevancyScore value) {
        this.relevancyScore = value;
    }

    /**
     * Gets the value of the orcidProfile property.
     * 
     * @return
     *     possible object is
     *     {@link OrcidProfile }
     *     
     */
    public OrcidProfile getOrcidProfile() {
        return orcidProfile;
    }

    /**
     * Sets the value of the orcidProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrcidProfile }
     *     
     */
    public void setOrcidProfile(OrcidProfile value) {
        this.orcidProfile = value;
    }

}
