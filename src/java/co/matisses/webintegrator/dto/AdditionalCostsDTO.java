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
public class AdditionalCostsDTO {
    private String deliveryCost;
    private String installationCost;

    public AdditionalCostsDTO() {
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getInstallationCost() {
        return installationCost;
    }

    public void setInstallationCost(String installationCost) {
        this.installationCost = installationCost;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(AdditionalCostsDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static AdditionalCostsDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(AdditionalCostsDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (AdditionalCostsDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "DeliveryAndInstallationCostsDTO{" + "deliveryCost=" + deliveryCost + ", installationCost=" + installationCost + '}';
    }
}
