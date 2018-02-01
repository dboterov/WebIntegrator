package co.matisses.webintegrator.ejb;

import co.matisses.persistence.sap.entity.DepartamentoPK;
import co.matisses.persistence.sap.entity.DepartamentoSAP;
import co.matisses.persistence.sap.entity.DireccionSocioDeNegocios;
import co.matisses.persistence.sap.entity.SocioDeNegocios;
import co.matisses.persistence.sap.facade.DepartamentoSAPFacade;
import co.matisses.persistence.sap.facade.DireccionSocioDeNegociosFacade;
import co.matisses.persistence.sap.facade.SocioDeNegociosFacade;
import co.matisses.persistence.web.entity.ClienteWeb;
import co.matisses.persistence.web.entity.DireccionClienteWeb;
import co.matisses.persistence.web.facade.ClienteWebFacade;
import co.matisses.persistence.web.facade.DireccionClienteWebFacade;
import co.matisses.webintegrator.dto.CustomerAddressDTO;
import co.matisses.webintegrator.dto.CustomerDTO;
import co.matisses.webintegrator.dto.GenericResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

/**
 *
 * @author dbotero
 */
@Stateless
@LocalBean
public class CustomerEJB  {

    private static final String EMPLEADO_WEB = "98";
    @EJB
    private SocioDeNegociosFacade sapCustomerFacade;
    @EJB
    private DireccionSocioDeNegociosFacade direccionSocioNegociosFacade;
    @EJB
    private ClienteWebFacade webCustomerFacade;
    @EJB
    private DireccionClienteWebFacade webCustomerAddressFacade;
    @EJB
    private DepartamentoSAPFacade departamentoFacade;
    private static final Logger log = Logger.getLogger(CustomerEJB.class.getSimpleName());

    public CustomerEJB() {

    }

    private CustomerDTO entity2Dto(SocioDeNegocios entity) {
        if (entity == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getCardCode());
        dto.setEmail(entity.getEmail());
        dto.setNames(entity.getNombres());
        dto.setLastName1(entity.getApellido1());
        dto.setLastName2(entity.getApellido2());
        dto.setLegalName(entity.getRazonSocial());
        dto.setSalesPersonCode(entity.getSlpCode() != null ? entity.getSlpCode() : null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dto.setBirthDate(
                sdf.format(entity.getFechaNacimiento() != null ? entity.getFechaNacimiento() : new GregorianCalendar(1900, 0, 1).getTime()));
        dto.setGender(entity.getSexo());
        dto.setDefaultBillingAddress(entity.getDireccionEstandarFactura());
        dto.setDefaultShippingAddress(entity.getDireccionEstandarEntrega());

        List<DireccionSocioDeNegocios> direccionesSAP = direccionSocioNegociosFacade.findByCardCode(entity.getCardCode());
        List<CustomerAddressDTO> direcciones = new ArrayList<>();
        for (DireccionSocioDeNegocios dirSAP : direccionesSAP) {
            CustomerAddressDTO dir = new CustomerAddressDTO();
            dir.setMobile(dirSAP.getBuilding());
            dir.setCityName(dirSAP.getCity());
            dir.setCityCode(dirSAP.getUMunicipio());
            dir.setStateCode(dirSAP.getState());

            DepartamentoSAP dpto = departamentoFacade.find(new DepartamentoPK(dirSAP.getState(), dirSAP.getCountry()));
            if (dpto != null) {
                dir.setStateName(dpto.getName());
            }

            dir.setAddress(dirSAP.getStreet());
            dir.setEmail(dirSAP.getCounty());
            dir.setAddressName(dirSAP.getDireccionSocioDeNegociosPK().getAddress());
            dir.setPhone(dirSAP.getBlock());
            dir.setAddressType(dirSAP.getDireccionSocioDeNegociosPK().getAdresType() == 'B' ? "F" : "E");
            dir.setLineNumSAP(dirSAP.getLineNum().toString());
            direcciones.add(dir);
        }
        dto.setAddresses(direcciones);

        return dto;
    }

    private ClienteWeb dtoToEntity(CustomerDTO dto) {
        ClienteWeb webCustomer = new ClienteWeb();

        if (!dto.getId().toUpperCase().endsWith("CL")) {
            webCustomer.setNit(dto.getId() + "CL");
        } else {
            webCustomer.setNit(dto.getId());
        }
        webCustomer.setDigitoVerificacion("");
        webCustomer.setOrigenModifica("Prestashop");
        webCustomer.setApellido1(dto.getLastName1().toUpperCase());
        webCustomer.setApellido2(dto.getLastName2().toUpperCase());
        webCustomer.setCodAsesor(dto.getSalesPersonCode());
        webCustomer.setEmail(dto.getEmail());
        webCustomer.setTipoPersona("01");
        webCustomer.setTipoDocumento("13");
        webCustomer.setAutorretenedor("N");
        webCustomer.setRegimenTributario("");
        webCustomer.setNacionalidad("01");

        if (dto.getBirthDate() != null && !dto.getBirthDate().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaNacimiento = sdf.parse(dto.getBirthDate());
                webCustomer.setFechaNacimiento(fechaNacimiento);
            } catch (Exception e) {
                GregorianCalendar cal = new GregorianCalendar(1900, 0, 1);
                webCustomer.setFechaNacimiento(cal.getTime());
                log.log(Level.SEVERE, "Ocurrio un error al procesar la fecha de nacimiento proporcionada [{0}]", dto.getBirthDate());
            }
        } else {
            log.log(Level.WARNING, "Configurando fecha de nacimiento estandar ya que no se proporciono una desde prestashop");
            GregorianCalendar cal = new GregorianCalendar(1900, 0, 1);
            webCustomer.setFechaNacimiento(cal.getTime());
        }

        webCustomer.setNombres(dto.getNames().toUpperCase());
        webCustomer.setRazonSocial(dto.getLegalName().toUpperCase());

//        try {
//            webCustomer.setFechaNacimiento(dto.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").parse(dto.getBirthDate()) : null);
//        } catch (Exception e) {
//            log.log(Level.SEVERE, "Ocurrio un error formateando la fecha de nacimiento. ", e);
//        }

        webCustomer.setSexo(dto.getGender());
        webCustomer.setDirEstadarFac(dto.getDefaultBillingAddress());
        webCustomer.setDirEstandarEnt(dto.getDefaultShippingAddress());

        List<DireccionClienteWeb> direcciones = new ArrayList<>();
        //Si solo hay una direccion, la copia para tener una de cada tipo (facturacion y entrega)
        if (dto.getAddresses() != null && dto.getAddresses().size() == 1) {
            String tipo = "";
            CustomerAddressDTO dir = dto.getAddresses().get(0);
            if (dir.getAddressType().equals("F")) {
                tipo = "E";
            } else {
                tipo = "F";
            }
            CustomerAddressDTO dir2 = CustomerAddressDTO.clone(dir);
            dir2.setAddressType(tipo);
            dto.getAddresses().add(dir2);
        }
        for (CustomerAddressDTO direccion : dto.getAddresses()) {
            DireccionClienteWeb dirWeb = new DireccionClienteWeb();
            if (direccion.getLineNumSAP() != null) {
                dirWeb.setLineNumSAP(new Integer(direccion.getLineNumSAP()));
            }

            dirWeb.setCelular(direccion.getMobile());
            dirWeb.setCodCiudad(direccion.getCityCode());
            dirWeb.setCodDepartamento(direccion.getStateCode());
            dirWeb.setDireccion(direccion.getAddress());
            dirWeb.setEmail(direccion.getEmail());
            dirWeb.setNit(webCustomer.getNit());
            dirWeb.setNombre(direccion.getAddressName());
            dirWeb.setNombreCiudad(direccion.getCityName());
            dirWeb.setNombreDepartamento(direccion.getStateName());
            dirWeb.setTelefono(direccion.getPhone());
            dirWeb.setTipo(direccion.getAddressType());

//            if (webCustomer.getCelular() == null && direccion.getMobile() != null) {
//                webCustomer.setCelular(direccion.getMobile());
//            }
//
//            if (webCustomer.getTelefono() == null && direccion.getPhone() != null) {
//                webCustomer.setTelefono(direccion.getPhone());
//            }

            if ((webCustomer.getDirEstadarFac() == null || webCustomer.getDirEstadarFac().isEmpty()) && dirWeb.getTipo().equals("F")) {
                webCustomer.setDirEstadarFac(dirWeb.getNombre());
            }

            if ((webCustomer.getDirEstandarEnt() == null || webCustomer.getDirEstandarEnt().isEmpty()) && dirWeb.getTipo().equals("E")) {
                webCustomer.setDirEstandarEnt(dirWeb.getNombre());
            }

            direcciones.add(dirWeb);
        }
        webCustomer.setDirecciones(direcciones);

        return webCustomer;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CustomerDTO getCustomerByEmail(Object customer) {
        log.log(Level.INFO, "Searching for customer with email [{0}]", ((CustomerDTO) customer).getEmail());
        CustomerDTO dto = findCustomerSAPDatabaseByEmail(((CustomerDTO) customer).getEmail());
        if (dto != null && dto.getId() != null) {
            log.log(Level.INFO, "Customer [{0}] found in SAP database", dto.getLegalName());
            return dto;
        }

        log.log(Level.INFO, "No customer with email [{0}] was found in any database...", ((CustomerDTO) customer).getEmail());
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CustomerDTO getCustomerById(Object customer) {
        log.log(Level.INFO, "Searching for customer with id [{0}]", ((CustomerDTO) customer).getId());

        CustomerDTO dto = findCustomerSAPDatabaseById(((CustomerDTO) customer).getId());
        if (dto != null && dto.getId() != null) {
            log.log(Level.INFO, "Customer [{0}] found in SAP database", dto.getLegalName());
            return dto;
        }

        log.log(Level.INFO, "No customer with id [{0}] was found in any database...", ((CustomerDTO) customer).getId());
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private CustomerDTO findCustomerSAPDatabaseByEmail(String email) {
        try {
            SocioDeNegocios entity = sapCustomerFacade.findByEmail(email);
            return entity2Dto(entity);
        } catch (NoResultException e) {
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error consultanto el cliente. ", e);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private CustomerDTO findCustomerSAPDatabaseById(String id) {
        try {
            if (id != null && !id.toUpperCase().endsWith("CL")) {
                id = id + "CL";
            }
            SocioDeNegocios entity = sapCustomerFacade.findByCardCode(id);
            return entity2Dto(entity);
        } catch (NoResultException e) {
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error consultanto el cliente. ", e);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse createNewCustomer(Object customer) {
        GenericResponse response = new GenericResponse();

        CustomerDTO prestashopCustomer = (CustomerDTO) customer;
        prestashopCustomer.setSalesPersonCode(EMPLEADO_WEB);

        //Valida que la longitud del NIT sea correcta
        if (prestashopCustomer.getId() != null
                && ((prestashopCustomer.getId().toUpperCase().endsWith("CL") && prestashopCustomer.getId().length() > 13)
                || (!prestashopCustomer.getId().toUpperCase().endsWith("CL") && prestashopCustomer.getId().length() > 11))) {
            response.setCode("0401901");
            response.setDetail("El NIT del cliente no puede tener una longitud mayor a 11 caracteres");
            return response;
        }

        //Valida que el genero no sea nulo
        if (prestashopCustomer.getGender() == null) {
            response.setCode("0401902");
            response.setDetail("Se debe enviar un valor para el genero");
            return response;
        }

        //Valida que el genero se encuentre dentro de los valores aceptados (1-Masculino, 2-Femenino, 3-Sin especificar)
        try {
            int genero = Integer.parseInt(prestashopCustomer.getGender());
            if (genero < 1 || genero > 3) {
                response.setCode("0401903");
                response.setDetail("El valor enviado para el genero [" + genero + "] no es valido. Los valores validos son 1-Masculino, 2-Femenino, 3-Sin especificar");
                return response;
            }
        } catch (NumberFormatException e) {
            response.setCode("0401904");
            response.setDetail("El valor enviado para el genero [" + prestashopCustomer.getGender() + "] no es valido");
            return response;
        }

        ClienteWeb webCustomer = dtoToEntity(prestashopCustomer);
        CustomerDTO dbCustomer = getCustomerById(customer);

        if (dbCustomer == null) {
            webCustomer.setEstadoG("N");
            webCustomerFacade.create(webCustomer);

            int retryCount = 0;
            while (retryCount < 20 && (webCustomer.getEstadoG().equals("N") || webCustomer.getEstadoG().trim().isEmpty())) {
                try {
                    log.log(Level.INFO, "Intento de consulta de estado de cliente #{0}", retryCount);
                    webCustomer = webCustomerFacade.find(webCustomer.getNit());
                    retryCount++;
                    Thread.sleep(980);
                } catch (Exception e) {
                }
            }
            log.log(Level.INFO, "Se finalizo en el intento #{0}, estado [{1}]", new Object[]{retryCount, webCustomer.getEstadoG()});
            if (retryCount >= 20) {
                response.setCode("0401901");
                response.setDetail("No se pudo validar el estado del proceso de creacion del cliente.");
                return response;
            } else {
                response.setCode("0401001");
                response.setDetail("El cliente se creó de manera exitosa");
                return response;
            }
        } else {
            return modifyCustomer(dbCustomer);
        }
    }

    private CustomerDTO mergeDTOs(CustomerDTO prestashop, CustomerDTO fromDB) throws Exception {
        CustomerDTO merged = new CustomerDTO();
        if (!prestashop.getId().equals(fromDB.getId())) {
            throw new Exception("No se pueden unir dos DTOs con diferente ID");
        }
        //Prevalece la informacion de prestashop
        merged.setId(prestashop.getId());
        merged.setNames(prestashop.getNames().toUpperCase());
        merged.setLastName1(prestashop.getLastName1().toUpperCase());
        merged.setLastName2(prestashop.getLastName2().toUpperCase());
        merged.setLegalName(prestashop.getLegalName().toUpperCase());
        merged.setEmail(prestashop.getEmail());
        merged.setGender(prestashop.getGender());
        merged.setBirthDate(prestashop.getBirthDate());
        merged.setDefaultBillingAddress(prestashop.getDefaultBillingAddress());
        merged.setDefaultShippingAddress(prestashop.getDefaultShippingAddress());
        merged.setSalesPersonCode(prestashop.getSalesPersonCode());

        //Para las direcciones, se compara si alguna direccion ya existe y se asignan los numeros de linea de sap
        List<CustomerAddressDTO> mergedAddresses = new ArrayList<>();
        for (CustomerAddressDTO dirPrestashop : prestashop.getAddresses()) {
            boolean existe = false;
            for (CustomerAddressDTO dirFromDB : fromDB.getAddresses()) {
                if (dirPrestashop.getAddressName().equals(dirFromDB.getAddressName())
                        && dirPrestashop.getAddressType().equals(dirFromDB.getAddressType())) {
                    existe = true;
                    dirPrestashop.setLineNumSAP(dirFromDB.getLineNumSAP());
                    mergedAddresses.add(dirPrestashop);
                }
            }
            if (!existe) {
                mergedAddresses.add(dirPrestashop);
            }
        }
        merged.setAddresses(mergedAddresses);
        return merged;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse modifyCustomer(Object customer) {
        GenericResponse response = new GenericResponse();
        CustomerDTO prestashopCustomer = (CustomerDTO) customer;
        if (!prestashopCustomer.getId().toUpperCase().endsWith("CL")) {
            prestashopCustomer.setId(prestashopCustomer.getId().concat("CL"));
        }

        CustomerDTO fromDB = getCustomerById(customer);
        if (fromDB == null) {
            log.log(Level.SEVERE, "Se intentó modificar un cliente que no existe");
            response.setCode("0402901");
            response.setDetail("No existe un cliente con el NIT [" + prestashopCustomer.getId() + "]");
            return response;
        }

        try {
            CustomerDTO merged = mergeDTOs(prestashopCustomer, fromDB);
            ClienteWeb webCustomer = dtoToEntity(merged);
            webCustomer.setEstadoG("M");

            //Elimina las direcciones de la base de datos web para evitar conflictos con claves primarias
            try {
                webCustomerAddressFacade.eliminarDireccionesCliente(webCustomer.getNit());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Ocurrio un error al eliminar las direcciones del cliente. ", e);
            }

            try {
                webCustomerFacade.edit(webCustomer);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Ocurrio un error al modificar el registro en BaruWeb. ", e);
                response.setCode("0402902");
                response.setDetail("Ocurrio un error al modificar el registro en base de datos web");
                return response;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al unir los DTOs de prestashop y BD. ", e);
            response.setCode("0402903");
            response.setDetail("Ocurrio un error al unir los DTOs de prestashop y BD");
            return response;
        }

        response.setCode("0402001");
        response.setDetail("El cliente se modifico de manera exitosa");
        return response;
    }
}
