//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.14 at 10:10:17 PM EDT 
//


package edu.cwru.SimpleRTS.environment.state.persistence.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResourcePickupLogList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourcePickupLogList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="roundNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="resourcePickupLog" type="{}ResourcePickupLog"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcePickupLogList", propOrder = {
    "roundNumber",
    "resourcePickupLog"
})
public class XmlResourcePickupLogList {

    protected int roundNumber;
    protected List<XmlResourcePickupLog> resourcePickupLog;

    /**
     * Gets the value of the roundNumber property.
     * 
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Sets the value of the roundNumber property.
     * 
     */
    public void setRoundNumber(int value) {
        this.roundNumber = value;
    }

    /**
     * Gets the value of the resourcePickupLog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourcePickupLog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourcePickupLog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlResourcePickupLog }
     * 
     * 
     */
    public List<XmlResourcePickupLog> getResourcePickupLog() {
        if (resourcePickupLog == null) {
            resourcePickupLog = new ArrayList<XmlResourcePickupLog>();
        }
        return this.resourcePickupLog;
    }

}