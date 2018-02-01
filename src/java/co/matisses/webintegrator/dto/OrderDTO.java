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
public class OrderDTO {
    private boolean synchronous;
    private OrderHeaderDTO header;
    private List<OrderDetailDTO> detail;

    public OrderDTO() {
        detail = new ArrayList<>();
    }
    
    public OrderDTO(OrderHeaderDTO header, List<OrderDetailDTO> detail) {
        this.header = header;
        this.detail = detail;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public List<OrderDetailDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<OrderDetailDTO> detail) {
        this.detail = detail;
    }

    public OrderHeaderDTO getHeader() {
        return header;
    }

    public void setHeader(OrderHeaderDTO header) {
        this.header = header;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderDTO{" + "synchronous=" + synchronous + ", header=" + header + ", detail=" + detail + '}';
    }
}
