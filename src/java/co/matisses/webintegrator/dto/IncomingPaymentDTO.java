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
public class IncomingPaymentDTO {
    private String nroFactura;
    private String voucher;
    private String nroTarjeta;
    private String franquicia;
    private String tipo;
    private Integer idPago;

    public IncomingPaymentDTO() {
    }

    public IncomingPaymentDTO(String nroFactura, String voucher, String nroTarjeta, Integer idPago, String franquicia, String tipo) {
        this.nroFactura = nroFactura;
        this.voucher = voucher;
        this.nroTarjeta = nroTarjeta;
        this.idPago = idPago;
        this.franquicia = franquicia;
        this.tipo = tipo;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(String nroFactura) {
        this.nroFactura = nroFactura;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getFranquicia() {
        return franquicia;
    }

    public void setFranquicia(String franquicia) {
        this.franquicia = franquicia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(IncomingPaymentDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static IncomingPaymentDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(IncomingPaymentDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (IncomingPaymentDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "IncomingPaymentDTO{" + "nroFactura=" + nroFactura + ", voucher=" + voucher + ", nroTarjeta=" + nroTarjeta + ", idPago=" + idPago + '}';
    }
}
