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
public class ItemColorDTO {
    private String code;
    private String name;
    private String hexa;

    public ItemColorDTO() {
    }

    public ItemColorDTO(String code, String name, String hexa) {
        this.code = code;
        this.name = name;
        this.hexa = hexa;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexa() {
        return hexa;
    }

    public void setHexa(String hexa) {
        this.hexa = hexa;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ItemColorDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ItemColorDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ItemColorDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ItemColorDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ItemColorDTO{" + "code=" + code + ", name=" + name + ", hexa=" + hexa + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemColorDTO other = (ItemColorDTO) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }
}
