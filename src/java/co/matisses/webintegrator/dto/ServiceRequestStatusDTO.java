package co.matisses.webintegrator.dto;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dbotero
 */
@XmlRootElement
public class ServiceRequestStatusDTO implements Comparable<ServiceRequestStatusDTO>{

    private Short code;
    private String name;
    private Date fecha;

    public ServiceRequestStatusDTO() {
    }

    public ServiceRequestStatusDTO(Short code, String name, Date fecha) {
        this.code = code;
        this.name = name;
        this.fecha = fecha;
    }

    public Short getCode() {
        return code;
    }

    public void setCode(Short code) {
        this.code = code;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ServiceRequestStatusDTO{" + "code=" + code + ", name=" + name + ", fecha=" + fecha + '}';
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ServiceRequestStatusDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ServiceRequestStatusDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ServiceRequestStatusDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ServiceRequestStatusDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public int compareTo(ServiceRequestStatusDTO o) {
        return this.getFecha().compareTo(o.getFecha());
    }
}
