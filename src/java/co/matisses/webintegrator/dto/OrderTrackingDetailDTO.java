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
public class OrderTrackingDetailDTO {
    private String itemCode;
    private String date;
    private String status;

    public OrderTrackingDetailDTO() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderTrackingDetailDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderTrackingDetailDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderTrackingDetailDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderTrackingDetailDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderTrackingDetailDTO{" + "itemCode=" + itemCode + ", date=" + date + ", status=" + status + '}';
    }
}
