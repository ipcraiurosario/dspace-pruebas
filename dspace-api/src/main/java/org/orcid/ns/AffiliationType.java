//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.21 at 10:22:56 AM CEST 
//


package org.orcid.ns;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for affiliation-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="affiliation-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="education"/>
 *     &lt;enumeration value="employment"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "affiliation-type")
@XmlEnum
public enum AffiliationType {

    @XmlEnumValue("education")
    EDUCATION("education"),
    @XmlEnumValue("employment")
    EMPLOYMENT("employment");
    private final String value;

    AffiliationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AffiliationType fromValue(String v) {
        for (AffiliationType c: AffiliationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
