package co.matisses.webintegrator.dto;

import java.util.Date;

/**
 *
 * @author dbotero
 */
public class SesionSAPB1WSDTO implements Comparable<SesionSAPB1WSDTO> {

    private Integer id;
    private String usuario;
    private String idSesionSAP;
    private String estado;
    private Date fecha;

    public SesionSAPB1WSDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdSesionSAP() {
        return idSesionSAP;
    }

    public void setIdSesionSAP(String idSesionSAP) {
        this.idSesionSAP = idSesionSAP;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "SesionSAPB1WSDTO{" + "id=" + id + ", usuario=" + usuario + ", idSesionSAP=" + idSesionSAP + ", estado=" + estado + ", fecha=" + fecha + '}';
    }

    @Override
    public int compareTo(SesionSAPB1WSDTO o) {
        return this.fecha.compareTo(o.getFecha());
    }
}
