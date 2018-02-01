package co.matisses.webintegrator.mbean;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author dbotero
 */
@ViewScoped
@Named(value = "functionsManagerMBean")
public class FunctionsManagerMBean implements Serializable {

    private String nombreObjeto;
    private String nombreOperacion;

    /**
     * Creates a new instance of FunctionsManagerMBean
     */
    public FunctionsManagerMBean() {
    }

    public String getNombreObjeto() {
        return nombreObjeto;
    }

    public void setNombreObjeto(String nombreObjeto) {
        this.nombreObjeto = nombreObjeto;
    }

    public String getNombreOperacion() {
        return nombreOperacion;
    }

    public void setNombreOperacion(String nombreOperacion) {
        this.nombreOperacion = nombreOperacion;
    }

    public void agregarObjeto() {
        nombreObjeto = "";
    }

    public void agregarOperacion() {
        nombreOperacion = "";
    }
}
