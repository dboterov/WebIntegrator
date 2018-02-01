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
public class ItemStockDTO {
    private String warehouseCode;
    private int quantity;

    public ItemStockDTO() {
    }

    public ItemStockDTO(String warehouseCode, int quantity) {
        this.warehouseCode = warehouseCode;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
    
    public void addStock(Integer quantity){
        this.quantity += quantity;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ItemStockDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ItemStockDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ItemStockDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ItemStockDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "ItemStockDTO{" + "warehouseCode=" + warehouseCode + ", quantity=" + quantity + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.warehouseCode != null ? this.warehouseCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemStockDTO other = (ItemStockDTO) obj;
        if ((this.warehouseCode == null) ? (other.warehouseCode != null) : !this.warehouseCode.equals(other.warehouseCode)) {
            return false;
        }
        return true;
    }
}
