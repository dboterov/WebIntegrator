package co.matisses.webintegrator.mbean;

import co.matisses.b1ws.client.B1WSServiceUnavailableException;
import co.matisses.b1ws.client.SAPSessionManager;
import co.matisses.persistence.sap.entity.TarjetaCreditoSAP;
import co.matisses.persistence.sap.facade.AlmacenFacade;
import co.matisses.persistence.sap.facade.TarjetaCreditoSAPFacade;
import co.matisses.persistence.web.entity.BwsSesionSAP;
import co.matisses.persistence.web.entity.DestinatarioPlantillaEmail;
import co.matisses.persistence.web.entity.FactorEntregaCiudad;
import co.matisses.persistence.web.entity.GenericCall;
import co.matisses.persistence.web.entity.GenericObject;
import co.matisses.persistence.web.entity.MunicipiosCercanos;
import co.matisses.persistence.web.entity.TransportistaExterno;
import co.matisses.persistence.web.facade.BwsSesionSAPFacade;
import co.matisses.persistence.web.facade.DestinatarioPlantillaEmailFacade;
import co.matisses.persistence.web.facade.FactorEntregaCiudadFacade;
import co.matisses.persistence.web.facade.GenericCallFacade;
import co.matisses.persistence.web.facade.GenericObjectFacade;
import co.matisses.persistence.web.facade.MunicipiosCercanosFacade;
import co.matisses.persistence.web.facade.TransportistaExternoFacade;
import co.matisses.webintegrator.dto.EmailTemplateDestinationDTO;
import co.matisses.webintegrator.dto.GenericCallDTO;
import co.matisses.webintegrator.dto.InformacionAlmacenDTO;
import co.matisses.webintegrator.dto.Operation;
import co.matisses.webintegrator.dto.OrderDTO;
import co.matisses.webintegrator.dto.SesionSAPB1WSDTO;
import co.matisses.webintegrator.dto.TarjetaCreditoDTO;
import co.matisses.webintegrator.dto.TransportistaDTO;
import co.matisses.webintegrator.dto.WebObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author dbotero
 */
@ApplicationScoped
@Named("webIntegratorMBean")
public class WebIntegratorMBean implements Serializable {

    private SAPSessionManager sapSessionManager = new SAPSessionManager();
    private Map<String, WebObject> objetos;
    private Map<String, WebObject> operaciones;
    private Map<String, String> municipiosCercanos;
    private Map<String, Map<String, Integer>> descuentoPorDestino;
    private HashMap<String, SesionSAPB1WSDTO> sesionesSAPActivas;
    private HashMap<String, EmailTemplateDestinationDTO> destinatariosPlantillaEmail;
    private HashMap<String, OrderDTO> ordenesPendientes;
    private HashMap<String, InformacionAlmacenDTO> infoAlmacen;
    private HashMap<String, TarjetaCreditoDTO> tarjetasCredito;
    private List<TransportistaDTO> transportistas;
    private Queue<SesionSAPB1WSDTO> sesionesSap;
    private Properties props = new Properties();
    private int MAX_B1WS_SESSIONS = 0;

    @EJB
    private AlmacenFacade almacenFacade;
    @EJB
    private GenericObjectFacade genericFacade;
    @EJB
    private GenericCallFacade genericCallFacade;
    @EJB
    private MunicipiosCercanosFacade municipiosCercanosFacade;
    @EJB
    private TransportistaExternoFacade transportistaFacade;
    @EJB
    private FactorEntregaCiudadFacade factorEntregaFacade;
    @EJB
    private BwsSesionSAPFacade sesionSAPFacade;
    @EJB
    private DestinatarioPlantillaEmailFacade destinatarioPlantillaFacade;
    @EJB
    private TarjetaCreditoSAPFacade tarjetaCreditoFacade;

    private static final Logger log = Logger.getLogger(WebIntegratorMBean.class.getSimpleName());

    public WebIntegratorMBean() {
        objetos = new HashMap<>();
        operaciones = new HashMap<>();
        municipiosCercanos = new HashMap<>();
        descuentoPorDestino = new HashMap<>();
        sesionesSAPActivas = new HashMap<>();
        transportistas = new ArrayList<>();
        ordenesPendientes = new HashMap<>();
        infoAlmacen = new HashMap<>();
        tarjetasCredito = new HashMap<>();
        sesionesSap = new LinkedList<>();
    }

    public HashMap<String, EmailTemplateDestinationDTO> getDestinatariosPlantillaEmail() {
        return destinatariosPlantillaEmail;
    }

    public Map<String, WebObject> getObjetos() {
        return objetos;
    }

    public Map<String, WebObject> getOperaciones() {
        return operaciones;
    }

    public SesionSAPB1WSDTO obtenerSesionDisponible() {
        SesionSAPB1WSDTO sesion = null;
        int limit = 40, count = 0;
        while (sesion == null && count < limit) {
            count++;
            log.log(Level.INFO, "Intento #{0} de obtener una sesion b1ws de la cola. ", count);
            try {
                sesion = sesionesSap.poll();
                Thread.sleep(500);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Ocurrio un error al obtener una sesion SAPB1WS. ", e);
                break;
            }
        }
        if (count == limit) {
            log.log(Level.SEVERE, "Se intento obtener una sesion pero no fue posible. ");
            //TODO: enviar correo de notificacion de cola de sesiones saturada
        } else if (sesion != null) {
            log.log(Level.INFO, "Se obtuvo la sesion {0}", sesion);
        }

        return sesion;
    }

    public void devolverSesion(SesionSAPB1WSDTO sesion) {
        sesionesSap.add(sesion);
        log.log(Level.INFO, "La sesion {0} fue devuelta a la cola. Sesiones disponibles: {1}", new Object[]{sesion, sesionesSap.size()});
    }

    public List<WebObject> listObjects() {
        List<WebObject> objList = new ArrayList<>();
        for (Map.Entry<String, WebObject> entry : objetos.entrySet()) {
            objList.add(entry.getValue());
        }
        Collections.sort(objList);
        return objList;
    }

    public List<Operation> listOperations() {
        List<Operation> opList = new ArrayList<>();
        for (Map.Entry<String, WebObject> entry : operaciones.entrySet()) {
            opList.add(new Operation(entry.getValue().getName()));
        }
        Collections.sort(opList);
        return opList;
    }

    public List<GenericCallDTO> listCalls() {
        List<GenericCallDTO> dtos = new ArrayList<>();
        List<GenericCall> entities = genericCallFacade.findAll();
        for (GenericCall entity : entities) {
            GenericCallDTO dto = new GenericCallDTO();
            dto.setClassName(entity.getClassName());
            dto.setId(entity.getIdGenericCall());
            dto.setIdObject(Integer.parseInt(entity.getIdObject()));
            dto.setIdOperation(Integer.parseInt(entity.getIdOperation()));
            dto.setMethodName(entity.getMethodName());
            dto.setParameters(entity.getParameters());
            dto.setReturnType(entity.getReturnType());
            dto.setXmlDataType(entity.getXmlDataType());

            dtos.add(dto);
        }
        return dtos;
    }

    public void cargarTransportistas() {
        transportistas = new ArrayList<>();
        for (TransportistaExterno ent : transportistaFacade.listActiveCarriers()) {
            TransportistaDTO dto = new TransportistaDTO();
            dto.setActive(ent.getActive());
            dto.setCarrierId(ent.getCarrierId());
            dto.setCarrierName(ent.getCarrierName());
            dto.setClassName(ent.getClassName());
            dto.setQuotingMethod(ent.getQuotingMethod());
            dto.setTrackingMethod(ent.getTrackingMethod());
            dto.setWsdl(ent.getWsdl());
            transportistas.add(dto);
        }
    }

    public boolean containsObject(String name) {
        return objetos.containsKey(name);
    }

    public boolean containsOperation(String name) {
        return operaciones.containsKey(name);
    }

    public Long getObjectId(String name) {
        return objetos.get(name).getId();
    }

    public Long getOperationId(String name) {
        return operaciones.get(name).getId();
    }

    public Map<String, String> getMunicipiosCercanos() {
        return municipiosCercanos;
    }

    public List<TransportistaDTO> getTransportistas() {
        return transportistas;
    }

    @PostConstruct
    public void updateLists() {
        try {
            objetos = new HashMap<>();
            operaciones = new HashMap<>();
            municipiosCercanos = new HashMap<>();
            //Inicializa el mapa de objetos
            for (GenericObject object : genericFacade.findObjects()) {
                objetos.put(object.getName(), new WebObject(object.getIdGenericObject(), object.getName()));
            }
            //Inicializa el mapa de operaciones
            for (GenericObject object : genericFacade.findOperations()) {
                operaciones.put(object.getName(), new WebObject(object.getIdGenericObject(), object.getName()));
            }
            log.log(Level.FINEST, "Lists have been updated: [{0}] operations and [{1}] objects", new Object[]{operaciones.size(), objetos.size()});
            //Inicializa el mapa de municipios cercanos
            for (MunicipiosCercanos ent : municipiosCercanosFacade.findAll()) {
                municipiosCercanos.put(ent.getCodMunicipio(), ent.getCodMunicipioPpal());
            }
            //Carga la lista de transportistas
            cargarTransportistas();
            //Carga la configuracion de descuentos por ciudad origen-destino
            cargarDescuentosCiudad();
            cargarUbicacionesTM();
            cargarTarjetasCredito();
        } catch (Exception e) {
            log.log(Level.SEVERE, "There was an error during the update operation for objects and operations lists. ", e);
        }
        cargarProperties();
        cargarDestinatariosPlantillasEmail();
        inicializarColaSesionesSAP();
    }

    private void finalizarSesionesSAP() {
        Iterator<SesionSAPB1WSDTO> it = sesionesSap.iterator();
        while (it.hasNext()) {
            try {
                sapSessionManager.logout(it.next().getIdSesionSAP());
            } catch (Exception e) {
            }
        }
    }

    private void inicializarColaSesionesSAP() {
        finalizarSesionesSAP();
        sesionesSap = new LinkedList<>();
        try {
            MAX_B1WS_SESSIONS = Integer.parseInt(obtenerValorPropiedad("b1ws.max.sessions"));
        } catch (Exception e) {
            log.log(Level.SEVERE, "No fue posible cargar el limite de sesiones desde el archivo properties. Configurando un valor de 5 por defecto. ");
            MAX_B1WS_SESSIONS = 1;
        }
        for (int i = 0; i < MAX_B1WS_SESSIONS; i++) {
            log.log(Level.INFO, "Iniciando sesion b1ws para usuario prestashop{0}", i + 1);
            sesionesSap.add(obtenerSesionSAP("prestashop" + (i + 1)));
        }
    }

    public void cargarProperties() {
        props = new Properties();
        String serverConfUrl = System.getProperty("jboss.server.config.dir");
        log.log(Level.INFO, "Server config URL [{0}]", serverConfUrl);
        String propertiesFileName = "baru.properties";
        String path = serverConfUrl + File.separator + propertiesFileName;
        log.log(Level.INFO, "Loading properties file: [{0}]", path);
        try {
            File propsFile = new File(path);
            if (propsFile.exists()) {
                props.load(new FileInputStream(propsFile));
            } else {
                props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + propertiesFileName));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "There was an error loading the file.", e);
        }
    }

    private void cargarDestinatariosPlantillasEmail() {
        destinatariosPlantillaEmail = new HashMap<>();
        try {
            for (DestinatarioPlantillaEmail entidad : destinatarioPlantillaFacade.findAll()) {
                if (destinatariosPlantillaEmail.containsKey(entidad.getNombrePlantilla())) {
                    destinatariosPlantillaEmail.get(entidad.getNombrePlantilla()).addDestinations(entidad.getPara(), entidad.getCopia(), entidad.getCopiaOculta());
                } else {
                    destinatariosPlantillaEmail.put(entidad.getNombrePlantilla(), new EmailTemplateDestinationDTO(entidad.getNombrePlantilla(), entidad.getPara(), entidad.getCopia(), entidad.getCopiaOculta()));
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al cargar los destinatarios por plantilla de correo. ", e);
        }
    }

    public void cargarDescuentosCiudad() {
        descuentoPorDestino = new HashMap<>();
        try {
            List<FactorEntregaCiudad> entidades = factorEntregaFacade.findAll();
            for (FactorEntregaCiudad entidad : entidades) {
                if (descuentoPorDestino.containsKey(entidad.getCodigoCiudadOrigen())) {
                    descuentoPorDestino.get(entidad.getCodigoCiudadOrigen()).put(entidad.getCodigoCiudadDestino(), entidad.getFactor());
                } else {
                    Map<String, Integer> desc = new HashMap<>();
                    desc.put(entidad.getCodigoCiudadDestino(), entidad.getFactor());
                    descuentoPorDestino.put(entidad.getCodigoCiudadOrigen(), desc);
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "No fue posible cargar los descuentos configurados por ciudad origen-destino. {0}", e.getMessage());
        }
    }

    public String obtenerMunicipioPpal(String codMunicipio) {
        if (municipiosCercanos != null) {
            return municipiosCercanos.get(codMunicipio);
        } else {
            return null;
        }
    }

    public Integer obtenerDescuento(String ciudadOrigen, String ciudadDestino) {
        if (descuentoPorDestino.get(ciudadOrigen) != null) {
            Integer desc = descuentoPorDestino.get(ciudadOrigen).get(ciudadDestino);
            if (desc != null) {
                return desc;
            }
        }
        return 0;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private SesionSAPB1WSDTO obtenerSesionSAP(String usuario) {
        SesionSAPB1WSDTO sesion = sesionesSAPActivas.get(usuario);
        if (sesion != null) {
            log.log(Level.INFO, "Se encontro la sesion {0} en el mapa de sesiones", sesion);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //Si la fecha de la sesion corresponde a la fecha actual, la sesion es valida
            if (sdf.format(sesion.getFecha()).equals(sdf.format(new Date()))) {
                return sesion;
            } else {
                log.log(Level.WARNING, "  Esta sesion se encuentra vencida y es necesario generar una nueva");
                sesion = null;
                sesionesSAPActivas.remove(usuario);
            }
        }

        //se consultan todas las sesiones vencidas del usuario
        List<String> sesionesSAPVencidas = sesionSAPFacade.consultarSesionesVencidasActivas(usuario);
        if (sesionesSAPVencidas != null && !sesionesSAPVencidas.isEmpty()) {
            log.log(Level.WARNING, "Se encontraron {0} sesiones vencidas en base de datos. Se procede a inactivarlas. ", sesionesSAPVencidas.size());
            //se finalizan todas las sesiones vencidas del usuario
            for (String sesionVencida : sesionesSAPVencidas) {
                try {
                    sapSessionManager.logout(sesionVencida);
                } catch (B1WSServiceUnavailableException e) {
                    if (e.getMessage() != null && e.getMessage().contains("Invalid Session ID")) {
                        log.log(Level.WARNING, "La sesion {0} no es valida en SAP y no se pudo finalizar. ", sesionVencida);
                    } else {
                        log.log(Level.SEVERE, "No fue posible comunicarse con el B1WS. ", e);
                        break;
                    }
                }
            }
            //se inactivan todas las sesiones vencidas del usuario
            sesionSAPFacade.inactivarSesiones(usuario);
        }
        //consulta si existe una sesion activa
        SesionSAPB1WSDTO dto = obtenerSesionSAPActiva(usuario);
        if (dto != null) {
            log.log(Level.INFO, "El usuario tiene una sesion activa en base de datos. {0}", dto);
            sesionesSAPActivas.put(usuario, dto);
            return dto;
        }
        try {
            String sessionId = sapSessionManager.login();
            BwsSesionSAP sesionEntidad = new BwsSesionSAP();
            sesionEntidad.setEstado("A");
            sesionEntidad.setFecha(new Date());
            sesionEntidad.setIdSesionSAP(sessionId);
            sesionEntidad.setUsuario(usuario);
            sesionSAPFacade.create(sesionEntidad);

            dto = obtenerSesionSAPActiva(usuario);
            sesionesSAPActivas.put(usuario, dto);

            log.log(Level.INFO, "Se creo la sesion sap {0}", dto);
            return dto;
        } catch (B1WSServiceUnavailableException e) {
            log.log(Level.SEVERE, "No fue posible iniciar sesion en B1WS. ", e);
        } catch (Exception e) {
            log.log(Level.SEVERE, "No fue posible registrar la sesion. ", e);
        }
        return null;
    }

    private SesionSAPB1WSDTO obtenerSesionSAPActiva(String usuario) {
        Object[] data = sesionSAPFacade.consultarSesionActiva(usuario);
        if (data == null) {
            return null;
        }
        SesionSAPB1WSDTO dto = new SesionSAPB1WSDTO();
        dto.setId((Integer) data[0]);
        dto.setUsuario((String) data[1]);
        dto.setFecha((Date) data[2]);
        dto.setIdSesionSAP((String) data[3]);
        dto.setEstado((String) data[4]);

        return dto;
    }

    public String obtenerValorPropiedad(String prop) {
        return props.getProperty(prop);
    }

    public HashMap<String, OrderDTO> getOrdenesPendientes() {
        return ordenesPendientes;
    }

    private void cargarUbicacionesTM() {
        List<Object[]> datos = almacenFacade.consultarInfoAlmacenes();
        if (datos.isEmpty()) {
            return;
        }
        for (Object[] row : datos) {
            infoAlmacen.put((String) row[0], new InformacionAlmacenDTO((String) row[0], (Integer) row[1], (String) row[2]));
        }
    }

    public InformacionAlmacenDTO getInfoAlmacen(String whsCode) {
        return infoAlmacen.get(whsCode);
    }

    public void cargarTarjetasCredito() {
        tarjetasCredito = new HashMap<>();
        List<TarjetaCreditoSAP> tarjetas = tarjetaCreditoFacade.consultartarjetasP2P();
        for (TarjetaCreditoSAP entidad : tarjetas) {
            String nombre = entidad.getCardName().substring(4).substring(0, entidad.getCardName().length() - 8);
            tarjetasCredito.put(nombre, new TarjetaCreditoDTO(entidad.getCreditCard().longValue(), nombre));
        }
    }

    public TarjetaCreditoDTO getTarjetaCredito(String nombreTarjeta) {
        return tarjetasCredito.get(nombreTarjeta);
    }
}
