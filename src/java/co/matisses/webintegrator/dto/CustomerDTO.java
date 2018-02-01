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
public class CustomerDTO {

    private String id;
    private String names;
    private String lastName1;
    private String lastName2;
    private String legalName;
    private String email;
    private String salesPersonCode;
    private String gender;
    private String birthDate;
    private String defaultBillingAddress;
    private String defaultShippingAddress;
    private List<CustomerAddressDTO> addresses;

    public CustomerDTO() {
        addresses = new ArrayList<>();
    }

    public List<CustomerAddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<CustomerAddressDTO> addresses) {
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && id.contains(".")) {
            this.id = id.replace(".", "");
        } else {
            this.id = id;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getLastName1() {
        return lastName1;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getSalesPersonCode() {
        return salesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        this.salesPersonCode = salesPersonCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDefaultBillingAddress() {
        return defaultBillingAddress;
    }

    public void setDefaultBillingAddress(String defaultBillingAddress) {
        this.defaultBillingAddress = defaultBillingAddress;
    }

    public String getDefaultShippingAddress() {
        return defaultShippingAddress;
    }

    public void setDefaultShippingAddress(String defaultShippingAddress) {
        this.defaultShippingAddress = defaultShippingAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(CustomerDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static CustomerDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CustomerDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (CustomerDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "CustomerDTO{" + "id=" + id + ", names=" + names + ", lastName1=" + lastName1 + ", lastName2=" + lastName2 + ", legalName=" + legalName + ", email=" + email + ", salesPersonCode=" + salesPersonCode + ", gender=" + gender + ", birthDate=" + birthDate + ", defaultBillingAddress=" + defaultBillingAddress + ", defaultShippingAddress=" + defaultShippingAddress + ", addresses=" + addresses + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomerDTO other = (CustomerDTO) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
