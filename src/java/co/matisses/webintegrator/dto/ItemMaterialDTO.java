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
public class ItemMaterialDTO {
    private String code;
    private String name;
    private String cares;

    public ItemMaterialDTO() {
    }

    public ItemMaterialDTO(String code, String name, String cares) {
        this.code = code;
        this.name = name;
        this.cares = cares;
    }

    public String getCares() {
        return cares;
    }

    public void setCares(String cares) {
        this.cares = cares;
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

    @Override
    public String toString() {
        return "ItemMaterialDTO{" + "code=" + code + ", name=" + name + ", cares=" + cares + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.code != null ? this.code.hashCode() : 0);
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
        final ItemMaterialDTO other = (ItemMaterialDTO) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ItemMaterialDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ItemMaterialDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ItemMaterialDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ItemMaterialDTO) un.unmarshal(new StringReader(xml));
    }
}
