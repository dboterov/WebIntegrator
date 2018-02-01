package co.matisses.webintegrator.dto;

import java.io.StringReader;
import java.io.StringWriter;
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
public class GenericResponse {
    private String code;
    private String detail;

    public GenericResponse() {
    }

    public GenericResponse(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(GenericResponse.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static GenericResponse fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(GenericResponse.class);
        Unmarshaller un = context.createUnmarshaller();
        return (GenericResponse) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "GenericResponse{" + "code=" + code + ", detail=" + detail + '}';
    }
}
