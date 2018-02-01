package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class InformacionAlmacenDTO {
    private String whsCode;
    private Integer idUbicacionTM;
    private String almacenClientes;

    public InformacionAlmacenDTO() {
    }

    public InformacionAlmacenDTO(String whsCode, Integer idUbicacionTM, String almacenClientes) {
        this.whsCode = whsCode;
        this.idUbicacionTM = idUbicacionTM;
        this.almacenClientes = almacenClientes;
    }

    public String getAlmacenClientes() {
        return almacenClientes;
    }

    public void setAlmacenClientes(String almacenClientes) {
        this.almacenClientes = almacenClientes;
    }

    public Integer getIdUbicacionTM() {
        return idUbicacionTM;
    }

    public void setIdUbicacionTM(Integer idUbicacionTM) {
        this.idUbicacionTM = idUbicacionTM;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }
}
