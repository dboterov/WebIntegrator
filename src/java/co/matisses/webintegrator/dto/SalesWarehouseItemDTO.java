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
public class SalesWarehouseItemDTO {
    private String itemCode;
    private int quantity;
    private String whsCode;
    private String sourceCityCode;

    public SalesWarehouseItemDTO() {
    }

    public SalesWarehouseItemDTO(String itemCode, int quantity, String whsCode, String sourceCityCode) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.whsCode = whsCode;
        this.sourceCityCode = sourceCityCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public String getSourceCityCode() {
        return sourceCityCode;
    }

    public void setSourceCityCode(String sourceCityCode) {
        this.sourceCityCode = sourceCityCode;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(SalesWarehouseItemDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static SalesWarehouseItemDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(SalesWarehouseItemDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (SalesWarehouseItemDTO) un.unmarshal(new StringReader(xml));
    }
}
