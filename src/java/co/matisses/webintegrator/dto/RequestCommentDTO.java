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
public class RequestCommentDTO {

    private Long requestID;
    private String comment;

    public RequestCommentDTO() {
    }

    public RequestCommentDTO(Long requestID, String comment) {
        this.requestID = requestID;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getRequestID() {
        return requestID;
    }

    public void setRequestID(Long requestID) {
        this.requestID = requestID;
    }

    @Override
    public String toString() {
        return "RequestCommentDTO{" + "requestID=" + requestID + ", comment=" + comment + '}';
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(RequestCommentDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static RequestCommentDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(RequestCommentDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (RequestCommentDTO) un.unmarshal(new StringReader(xml));
    }
}
