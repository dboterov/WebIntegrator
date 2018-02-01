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
public class DepartmentsDTO {
    private List<DepartmentDTO> departments;

    public DepartmentsDTO() {
        departments = new ArrayList<DepartmentDTO>();
    }
    
    public void addDepartment(DepartmentDTO department){
        departments.add(department);
    }

    public List<DepartmentDTO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentDTO> departments) {
        this.departments = departments;
    }
    
    public String toXML() throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(DepartmentsDTO.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(this, sw);
        return sw.toString();
    }

    public static DepartmentsDTO fromXML(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(DepartmentsDTO.class);
        Unmarshaller un = context.createUnmarshaller();
        return (DepartmentsDTO) un.unmarshal(new StringReader(xml));
    }

    @Override
    public String toString() {
        return "DepartmentsDTO{" + "departments=" + departments + '}';
    }
    
    
}
