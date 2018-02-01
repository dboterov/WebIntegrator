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
public class ShippingQuotationResultDTO {

    private Integer prestashopOrderId;
    private String shippingCompany;
    private Integer total = 0;
    private String errorMessage;

    public ShippingQuotationResultDTO() {
    }

    public Integer getPrestashopOrderId() {
        return prestashopOrderId;
    }

    public void setPrestashopOrderId(Integer prestashopOrderId) {
        this.prestashopOrderId = prestashopOrderId;
    }

    public ShippingQuotationResultDTO(Integer total) {
        this.total = total;
    }

    public ShippingQuotationResultDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void addCost(Integer value) {
        this.total += value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ShippingQuotationResultDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ShippingQuotationResultDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ShippingQuotationResultDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ShippingQuotationResultDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ShippingQuotationResultDTO{" + "total=" + total + '}';
    }
}
