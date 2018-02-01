package co.matisses.webintegrator.ejb;

import co.matisses.persistence.sap.entity.BaruGrupo;
import co.matisses.persistence.sap.facade.BaruGrupoFacade;
import co.matisses.webintegrator.dto.DepartmentDTO;
import co.matisses.webintegrator.dto.GroupDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author dbotero
 */
@Stateless
@LocalBean
public class GroupEJB {

    @EJB
    private BaruGrupoFacade grupoFacade;
    private static final Logger log = Logger.getLogger(GroupEJB.class.getName());

    public GroupEJB() {
    }

    private GroupDTO entity2Dto(BaruGrupo entity) {
        if (entity == null) {
            return null;
        }
        GroupDTO dto = new GroupDTO();
        dto.setCode(entity.getCode());
        dto.setName(entity.getUWeb());

        return dto;
    }

    public List<GroupDTO> listGroupsByDepartment(Object department) {
        log.log(Level.INFO, "Searching for groups by department [{0}]", ((DepartmentDTO) department).getCode());
        List<GroupDTO> result = new ArrayList<>();
        for (BaruGrupo grupo : grupoFacade.findByDepartment(((DepartmentDTO) department).getCode())) {
            result.add(entity2Dto(grupo));
        }
        return result;
    }

    public GroupDTO findGroupById(Object group) {
        try {
            return entity2Dto(grupoFacade.find(((GroupDTO) group).getCode()));
        } catch (Exception e) {
            return null;
        }
    }
}
