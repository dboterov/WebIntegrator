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
public class InventoryChangesDTO {

    private List<InventoryItemDTO> changes;

    public InventoryChangesDTO() {
        changes = new ArrayList<InventoryItemDTO>();
    }

    public InventoryChangesDTO(List<InventoryItemDTO> changes) {
        this.changes = changes;
    }

    public List<InventoryItemDTO> getChanges() {
        return changes;
    }

    public void setChanges(List<InventoryItemDTO> changes) {
        this.changes = changes;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(InventoryChangesDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static InventoryChangesDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InventoryChangesDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (InventoryChangesDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "InventoryChangesDTO{" + "changes=" + changes + '}';
    }
}
