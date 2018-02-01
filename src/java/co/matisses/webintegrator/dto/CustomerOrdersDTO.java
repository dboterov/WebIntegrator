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
public class CustomerOrdersDTO{
    private List<InvoiceHeaderDTO> orders;

    public CustomerOrdersDTO() {
        orders = new ArrayList<>();
    }

    public CustomerOrdersDTO(List<InvoiceHeaderDTO> orders) {
        this.orders = orders;
    }

    public List<InvoiceHeaderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<InvoiceHeaderDTO> orders) {
        this.orders = orders;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(CustomerOrdersDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static CustomerOrdersDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CustomerOrdersDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (CustomerOrdersDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "CustomerOrdersResponse{" + "orders=" + orders + '}';
    }
}
