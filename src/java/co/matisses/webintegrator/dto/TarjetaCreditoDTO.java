package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class TarjetaCreditoDTO {
    private Long id;
    private String nombre;

    public TarjetaCreditoDTO() {
    }

    public TarjetaCreditoDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
