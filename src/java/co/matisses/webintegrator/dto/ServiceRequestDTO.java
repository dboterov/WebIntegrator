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
public class ServiceRequestDTO {

    private Integer requestID;
    private String customerId;
    private String subject;
    private String description;
    private String itemCode;
    private String invoiceNumber;
    private List<ServiceRequestImageDTO> images;
    private List<ProblemTypeDTO> problems;

    public ServiceRequestDTO() {
        images = new ArrayList<>();
    }

    public ServiceRequestDTO(Integer requestID, String customerId, String subject, String description, String itemCode, String invoiceNumber) {
        this.requestID = requestID;
        this.customerId = customerId;
        this.subject = subject;
        this.description = description;
        this.itemCode = itemCode;
        this.invoiceNumber = invoiceNumber;
        images = new ArrayList<>();
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(Integer requestID) {
        this.requestID = requestID;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public List<ServiceRequestImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ServiceRequestImageDTO> images) {
        this.images = images;
    }

    public List<ProblemTypeDTO> getProblems() {
        return problems;
    }

    public void setProblems(List<ProblemTypeDTO> problems) {
        this.problems = problems;
    }

    @Override
    public String toString() {
        return "ServiceRequestDTO{" + "requestID=" + requestID + ", customerId=" + customerId + ", subject=" + subject + ", description=" + description + ", itemCode=" + itemCode + ", invoiceNumber=" + invoiceNumber + '}';
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ServiceRequestDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ServiceRequestDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ServiceRequestDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ServiceRequestDTO) un.unmarshal(new StringReader(xml));
    }
}
