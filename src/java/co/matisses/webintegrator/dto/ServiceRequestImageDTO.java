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
public class ServiceRequestImageDTO {
    private String imageName;

    public ServiceRequestImageDTO() {
    }

    public ServiceRequestImageDTO(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ServiceRequestImageDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ServiceRequestImageDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ServiceRequestImageDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ServiceRequestImageDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ServiceRequestImageDTO{" + "imageName=" + imageName + '}';
    }
    
}
