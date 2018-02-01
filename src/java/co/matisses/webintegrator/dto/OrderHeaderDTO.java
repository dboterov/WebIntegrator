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
public class OrderHeaderDTO {
    private String prestashopOrderId;
    private String sapOrderId;
    private String customerId;
    private String deliveryOption;
    private String storageEndDate;
    private String pickUpStore;
    private String comments;
    
    public OrderHeaderDTO() {
    }

    public OrderHeaderDTO(String customerId) {
        this.customerId = customerId;
    }

    public String getPrestashopOrderId() {
        return prestashopOrderId;
    }

    public void setPrestashopOrderId(String prestashopOrderId) {
        this.prestashopOrderId = prestashopOrderId;
    }

    public String getSapOrderId() {
        return sapOrderId;
    }

    public void setSapOrderId(String sapOrderId) {
        this.sapOrderId = sapOrderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getPickUpStore() {
        return pickUpStore;
    }

    public void setPickUpStore(String pickUpStore) {
        this.pickUpStore = pickUpStore;
    }

    public String getStorageEndDate() {
        return storageEndDate;
    }

    public void setStorageEndDate(String storageEndDate) {
        this.storageEndDate = storageEndDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderHeaderDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderHeaderDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderHeaderDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderHeaderDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderHeaderDTO{" + "prestashopOrderId=" + prestashopOrderId + ", sapOrderId=" + sapOrderId + ", customerId=" + customerId + '}';
    }
}
