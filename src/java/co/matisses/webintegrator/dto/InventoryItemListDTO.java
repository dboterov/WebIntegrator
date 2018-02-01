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
public class InventoryItemListDTO {

    private List<InventoryItemDTO> items;

    public InventoryItemListDTO() {
        items = new ArrayList<>();
    }

    public List<InventoryItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InventoryItemDTO> items) {
        this.items = items;
    }

    public boolean contains(InventoryItemDTO item) {
        return items.contains(item);
    }

    public void add(InventoryItemDTO item) {
        items.add(item);
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(InventoryItemListDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static InventoryItemListDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InventoryItemListDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (InventoryItemListDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "InventoryItemListDTO{" + "items=" + items + '}';
    }
}
