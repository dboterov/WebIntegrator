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
public class WebEnabledModelsDTO {

    private List<ItemModelDTO> models;

    public WebEnabledModelsDTO() {
        models = new ArrayList<ItemModelDTO>();
    }

    public List<ItemModelDTO> getModels() {
        return models;
    }

    public void setModels(List<ItemModelDTO> models) {
        this.models = models;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(WebEnabledModelsDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static WebEnabledModelsDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(WebEnabledModelsDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (WebEnabledModelsDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "WebEnabledModelsDTO{" + "models=" + models + '}';
    }

    public void addItemModel(String model, String reference) {
        ItemModelDTO itemModel = new ItemModelDTO(model, reference);
        int pos = models.indexOf(itemModel);
        if (pos >= 0) {
            models.get(pos).addReference(reference);
        } else {
            models.add(itemModel);
        }
    }
}
