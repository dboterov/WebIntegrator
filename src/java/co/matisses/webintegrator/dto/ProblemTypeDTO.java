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
public class ProblemTypeDTO {
    private String name;

    public ProblemTypeDTO() {
    }

    public ProblemTypeDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ProblemTypeDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ProblemTypeDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ProblemTypeDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ProblemTypeDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ProblemTypeDTO{" + "name=" + name + '}';
    }
}
