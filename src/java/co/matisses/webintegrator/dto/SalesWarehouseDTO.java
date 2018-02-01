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
public class SalesWarehouseDTO {
    private String prestashopId;
    private String destinationCityCode;
    private List<SalesWarehouseItemDTO> items;

    public SalesWarehouseDTO() {
        items = new ArrayList<>();
    }

    public SalesWarehouseDTO(String prestashopId, List<SalesWarehouseItemDTO> items) {
        this.prestashopId = prestashopId;
        this.items = items;
    }

    public String getPrestashopId() {
        return prestashopId;
    }

    public void setPrestashopId(String prestashopId) {
        this.prestashopId = prestashopId;
    }

    public List<SalesWarehouseItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SalesWarehouseItemDTO> items) {
        this.items = items;
    }

    public String getDestinationCityCode() {
        return destinationCityCode;
    }

    public void setDestinationCityCode(String destinationCityCode) {
        this.destinationCityCode = destinationCityCode;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(SalesWarehouseDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static SalesWarehouseDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(SalesWarehouseDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (SalesWarehouseDTO) un.unmarshal(new StringReader(xml));
    }
}
