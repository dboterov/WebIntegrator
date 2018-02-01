package co.matisses.webintegrator.dto;

import co.matisses.b1ws.client.ObjectUtils;
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
public class ServiceRequestHistoryDTO {

    private String statusCode;
    private String statusName;
    private List<ServiceRequestStatusDTO> history;

    public ServiceRequestHistoryDTO() {
        history = new ArrayList<>();
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<ServiceRequestStatusDTO> getHistory() {
        return history;
    }

    public void setHistory(List<ServiceRequestStatusDTO> history) {
        this.history = history;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(ServiceRequestHistoryDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static ServiceRequestHistoryDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ServiceRequestHistoryDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (ServiceRequestHistoryDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this);
    }
}
