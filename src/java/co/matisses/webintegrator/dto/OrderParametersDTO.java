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
public class OrderParametersDTO {
    private String cityCode;
    private String orderTotal;
    private List<String> itemCodes;

    public OrderParametersDTO() {
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public List<String> getItemCodes() {
        return itemCodes;
    }

    public void setItemCodes(List<String> itemCodes) {
        this.itemCodes = itemCodes;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderParametersDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderParametersDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderParametersDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderParametersDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderParametersDTO{" + "cityCode=" + cityCode + ", orderTotal=" + orderTotal + ", itemCodes=" + itemCodes + '}';
    }
}
