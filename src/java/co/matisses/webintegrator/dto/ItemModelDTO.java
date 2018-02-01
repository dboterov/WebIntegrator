package co.matisses.webintegrator.dto;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
public class ItemModelDTO {

    private String code;
    private List<String> references;

    public ItemModelDTO() {
        references = new ArrayList<String>();
    }

    public ItemModelDTO(String code, String reference) {
        references = new ArrayList<String>();
        this.code = code;
        references.add(reference);
    }

    public ItemModelDTO(String code, List<String> references) {
        this.code = code;
        this.references = references;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public void addReference(String reference) {
        if (!references.contains(reference)) {
            references.add(reference);
        }
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ItemModelDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ItemModelDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ItemModelDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ItemModelDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ItemModelDTO{" + "code=" + code + ", references=" + references + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.code != null ? this.code.hashCode() : 0);
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
        final ItemModelDTO other = (ItemModelDTO) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }
}
