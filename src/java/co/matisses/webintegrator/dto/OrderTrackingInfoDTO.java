package co.matisses.webintegrator.dto;

import java.io.StringReader;
import java.io.StringWriter;
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
public class OrderTrackingInfoDTO extends GenericResponse {

    private List<OrderTrackingDetailDTO> trackingInfo;

    public OrderTrackingInfoDTO() {
    }

    public List<OrderTrackingDetailDTO> getTrackingInfo() {
        return trackingInfo;
    }

    public void setTrackingInfo(List<OrderTrackingDetailDTO> trackingInfo) {
        this.trackingInfo = trackingInfo;
    }

    @Override
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(OrderTrackingInfoDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static OrderTrackingInfoDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrderTrackingInfoDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (OrderTrackingInfoDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "OrderTrackingInfoDTO{" + "trackingInfo=" + trackingInfo + '}';
    }
}
