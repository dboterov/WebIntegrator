package co.matisses.webintegrator.ejb;

import co.matisses.persistence.sap.entity.BaruSubgrupo;
import co.matisses.persistence.sap.facade.BaruSubgrupoFacade;
import co.matisses.webintegrator.dto.GroupDTO;
import co.matisses.webintegrator.dto.SubGroupDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author dbotero
 */
public class SubGroupEJB {

    @EJB
    private BaruSubgrupoFacade subgrupoFacade;
    private static final Logger log = Logger.getLogger(SubGroupEJB.class.getSimpleName());

    public SubGroupEJB() {

    }

    private SubGroupDTO entity2Dto(BaruSubgrupo entity) {
        if (entity == null) {
            return null;
        }
        SubGroupDTO dto = new SubGroupDTO();
        dto.setCode(entity.getCode());
        dto.setName(entity.getUdescripcion());

        return dto;
    }

    public List<SubGroupDTO> listSubGroupsByGroup(Object group) {
        List<SubGroupDTO> result = new ArrayList<>();
        for (BaruSubgrupo subgrupo : subgrupoFacade.findByGroup(((GroupDTO) group).getCode())) {
            result.add(entity2Dto(subgrupo));
        }
        return result;
    }

    public SubGroupDTO findSubGroupById(Object subGroup) {
        try {
            return entity2Dto(subgrupoFacade.find(((SubGroupDTO) subGroup).getCode()));
        } catch (Exception e) {
            return null;
        }
    }
}
