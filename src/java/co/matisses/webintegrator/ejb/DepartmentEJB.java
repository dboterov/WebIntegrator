package co.matisses.webintegrator.ejb;

import co.matisses.persistence.sap.entity.GrupoItems;
import co.matisses.persistence.sap.facade.GrupoItemsFacade;
import co.matisses.webintegrator.dto.DepartmentDTO;
import co.matisses.webintegrator.dto.DepartmentsDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

/**
 *
 * @author dbotero
 */
@Stateless
public class DepartmentEJB {

    @EJB
    private GrupoItemsFacade grupoItemsFacade;
    private static final Logger log = Logger.getLogger(DepartmentEJB.class.getSimpleName());

    public DepartmentEJB() {
    }

    private DepartmentDTO entity2Dto(GrupoItems entity) {
        if (entity == null) {
            return null;
        }

        DepartmentDTO dto = new DepartmentDTO();
        dto.setCode(entity.getItmsGrpCod().toString());
        dto.setName(entity.getItmsGrpNam());

        return dto;
    }

    public DepartmentDTO findByCode(Object department) {
        log.log(Level.INFO, "Searching for department with code [{0}]", ((DepartmentDTO) department).getCode());
        return entity2Dto(grupoItemsFacade.find(((DepartmentDTO) department).getCode()));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DepartmentsDTO listDepartments() {
        DepartmentsDTO departments = new DepartmentsDTO();
        List<GrupoItems> entidades = grupoItemsFacade.findAll();
        for (GrupoItems grupo : entidades) {
            departments.addDepartment(entity2Dto(grupo));
        }

        log.info(departments.toString());
        try {
            log.info(departments.toXML());
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, "Ocurrio un error al listar los departamentos. ", ex);
        }
        return departments;
    }
}
