//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: PM.10.07 a las 01:34:06 PM CEST 
//


package icaro.aplicaciones.descripcionorganizaciones;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DescRecursosAplicacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DescRecursosAplicacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DescRecursoAplicacion" type="{urn:icaro:aplicaciones:descripcionOrganizaciones}DescRecursoAplicacion" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescRecursosAplicacion", propOrder = {
    "descRecursoAplicacion"
})
public class DescRecursosAplicacion {

    @XmlElement(name = "DescRecursoAplicacion")
    protected List<DescRecursoAplicacion> descRecursoAplicacion;

    /**
     * Gets the value of the descRecursoAplicacion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descRecursoAplicacion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescRecursoAplicacion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescRecursoAplicacion }
     * 
     * 
     */
    public List<DescRecursoAplicacion> getDescRecursoAplicacion() {
        if (descRecursoAplicacion == null) {
            descRecursoAplicacion = new ArrayList<DescRecursoAplicacion>();
        }
        return this.descRecursoAplicacion;
    }

}
