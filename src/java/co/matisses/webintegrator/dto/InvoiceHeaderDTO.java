package co.matisses.webintegrator.dto;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class InvoiceHeaderDTO {

    private String invoiceNumber;
    private String customerId;
    private String total;
    private String documentDate;
    private List<InvoiceDetailDTO> items;

    public InvoiceHeaderDTO() {
        items = new ArrayList<>();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<InvoiceDetailDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceDetailDTO> items) {
        this.items = items;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(InvoiceHeaderDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static InvoiceHeaderDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InvoiceHeaderDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (InvoiceHeaderDTO) un.unmarshal(new StringReader(xml));
    }

    public void addDetail(InvoiceDetailDTO detail) {
        items.add(detail);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.invoiceNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvoiceHeaderDTO other = (InvoiceHeaderDTO) obj;
        if (!Objects.equals(this.invoiceNumber, other.invoiceNumber)) {
            return false;
        }
        return true;
    }

}
