package co.matisses.webintegrator.dto;

/**
 * This class represents a generic request for the web integrator service. Such service 
 * works as a standard facade between matisses.co website and SBO.
 * @author dbotero
 */
public class GenericRequest {

    private String object;
    private String operation;
    private String source;
    private String data;

    public GenericRequest() {
    }

    public GenericRequest(String object, String operation, String source, String data) {
        this.object = object;
        this.operation = operation;
        this.source = source;
        this.data = data;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
