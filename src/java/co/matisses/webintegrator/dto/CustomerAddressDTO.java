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
public class CustomerAddressDTO {
    private String idAddress;
    private String addressName;
    private String address;
    private String cityCode;
    private String cityName;
    private String stateCode;
    private String stateName;
    private String email;
    private String addressType;
    private String mobile;
    private String phone;
    private String lineNumSAP;

    public CustomerAddressDTO() {
    }

    public static CustomerAddressDTO clone(CustomerAddressDTO address) {
        CustomerAddressDTO clonedAddress = new CustomerAddressDTO();
        clonedAddress.setAddressName(address.getAddressName());
        clonedAddress.setAddress(address.getAddress());
        clonedAddress.setCityCode(address.getCityCode());
        clonedAddress.setCityName(address.getCityName());
        clonedAddress.setStateCode(address.getStateCode());
        clonedAddress.setStateName(address.getStateName());
        clonedAddress.setEmail(address.getEmail());
        clonedAddress.setAddressType(address.getAddressType());
        clonedAddress.setMobile(address.getMobile());
        clonedAddress.setPhone(address.getPhone());
        clonedAddress.setLineNumSAP(address.getLineNumSAP());
        return clonedAddress;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getLineNumSAP() {
        return lineNumSAP;
    }

    public void setLineNumSAP(String lineNumSAP) {
        this.lineNumSAP = lineNumSAP;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(CustomerAddressDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static CustomerAddressDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CustomerAddressDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (CustomerAddressDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "CustomerAddressDTO{" + "addressName=" + addressName + ", address=" + address + ", cityCode=" + cityCode + ", cityName=" + cityName + ", stateCode=" + stateCode + ", stateName=" + stateName + ", email=" + email + ", addressType=" + addressType + ", mobile=" + mobile + ", phone=" + phone + '}';
    }
}
