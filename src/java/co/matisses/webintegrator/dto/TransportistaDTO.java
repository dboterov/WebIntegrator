package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class TransportistaDTO {

    private Integer carrierId;
    private String carrierName;
    private String wsdl;
    private String className;
    private String quotingMethod;
    private String trackingMethod;
    private Boolean active;

    public TransportistaDTO() {
    }

    public Integer getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getQuotingMethod() {
        return quotingMethod;
    }

    public void setQuotingMethod(String quotingMethod) {
        this.quotingMethod = quotingMethod;
    }

    public String getTrackingMethod() {
        return trackingMethod;
    }

    public void setTrackingMethod(String trackingMethod) {
        this.trackingMethod = trackingMethod;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
