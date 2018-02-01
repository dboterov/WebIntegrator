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
public class OrderDetailDTO {
    private String itemCode;
    private Integer quantity;

    public OrderDetailDTO() {
    }

    public OrderDetailDTO(String itemCode, Integer quantity) {
        this.itemCode = itemCode;
        this.quantity = quantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderDetailDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderDetailDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderDetailDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderDetailDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" + "itemCode=" + itemCode + ", quantity=" + quantity + '}';
    }
}
