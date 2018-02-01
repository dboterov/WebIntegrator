package co.matisses.webintegrator.ejb;

import co.matisses.b1ws.client.servicecalls.ServiceCallsServiceConnector;
import co.matisses.b1ws.dto.ServiceCallDTO;
import co.matisses.persistence.sap.entity.Empleado;
import co.matisses.persistence.sap.entity.LlamadaDeServicio;
import co.matisses.persistence.sap.facade.EmpleadoFacade;
import co.matisses.persistence.sap.facade.FacturaSAPFacade;
import co.matisses.persistence.sap.facade.LlamadaDeServicioFacade;
import co.matisses.webintegrator.dto.GenericResponse;
import co.matisses.webintegrator.dto.MailAttachmentDTO;
import co.matisses.webintegrator.dto.MailMessageDTO;
import co.matisses.webintegrator.dto.ProblemTypeDTO;
import co.matisses.webintegrator.dto.RequestCommentDTO;
import co.matisses.webintegrator.dto.ServiceRequestDTO;
import co.matisses.webintegrator.dto.ServiceRequestHistoryDTO;
import co.matisses.webintegrator.dto.ServiceRequestImageDTO;
import co.matisses.webintegrator.dto.ServiceRequestStatusDTO;
import co.matisses.webintegrator.dto.SesionSAPB1WSDTO;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import co.matisses.webintegrator.mbean.email.SendHTMLEmailMBean;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dbotero
 */
@Stateless
public class ServiceRequestEJB {

    private enum ServiceCallStatus {
        ABIERTA(-3, "Abierta"), CERRADA(-1, "Cerrada"), PENDIENTE(-2, "Pendiente");

        private int id;
        private String nombre;

        private ServiceCallStatus(int id, String nombre) {
            this.nombre = nombre;
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public int getId() {
            return id;
        }

        public static ServiceCallStatus getById(int i) {
            for (ServiceCallStatus e : values()) {
                if (e.id == i) {
                    return e;
                }
            }
            return null;
        }
    }
    @EJB
    private FacturaSAPFacade facturaFacade;
    @EJB
    private EmpleadoFacade empleadoFacade;
    @EJB
    private LlamadaDeServicioFacade llamadaServicioFacade;
    @Inject
    private SAPB1WSBean sapB1WSBean;
    @Inject
    private WebIntegratorMBean webIntegratorAppBean;
    @Inject
    private SendHTMLEmailMBean emailSender;
    private static final Logger log = Logger.getLogger(ServiceRequestEJB.class.getSimpleName());

    public ServiceRequestEJB() {
    }

    private Long assignEmployee() {
        //Carga la lista de empleados del dpto de servicio al cliente
        List<Empleado> empleados = empleadoFacade.listCustomerServiceEmployees();
        //Si no se obtuvo ningun empleado se retorna con error, ya que no se puede crear una solicitud sin asignar
        if (empleados == null || empleados.isEmpty()) {
            return -1L;
        }
        //Carga la lista de empleados a los que se les ha asignado las ultimas solicitudes
        List<Short> asignados = llamadaServicioFacade.getLastAssignedRequests(empleados.size() - 1);
        //Valida a cual empleado no se le ha asignado una de las ultimas solicitudes y se la asigna
        Integer idEmpleado = null;
        for (Empleado empleado : empleados) {
            boolean asignado = false;
            for (short idEmpleadoAsignado : asignados) {
                if (empleado.getUserId() != null && idEmpleadoAsignado == empleado.getUserId()) {
                    asignado = true;
                    break;
                }
            }
            if (!asignado) {
                idEmpleado = empleado.getUserId();
                break;
            }
        }
        if (idEmpleado == null || idEmpleado <= 0) {
            log.log(Level.SEVERE, "No se pudo encontrar un empleado para asignarle la solicitud. Se asignara el primero de la lista");
            idEmpleado = empleados.get(0).getEmpID();
        }
        return idEmpleado.longValue();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse createServiceRequest(Object request) {
        GenericResponse response = new GenericResponse();
        ServiceRequestDTO serviceRequest = (ServiceRequestDTO) request;

        //Valida que se haya enviado correctamente el numero de identificacion del cliente
        if (serviceRequest.getCustomerId() == null || serviceRequest.getCustomerId().trim().isEmpty() || serviceRequest.getCustomerId().equals("CL")) {
            response.setCode("0301901");
            response.setDetail("Se debe enviar la identificacion del cliente");
            return response;
        }

        //Valida que se haya enviado correctamente la descripcion del problema
        if (serviceRequest.getDescription() == null || serviceRequest.getDescription().trim().isEmpty()) {
            response.setCode("0301902");
            response.setDetail("Se debe enviar la descripcion del problema");
            return response;
        }

        //Valida que se haya enviado correctamente el numero de factura
        if (serviceRequest.getInvoiceNumber() == null || serviceRequest.getInvoiceNumber().trim().isEmpty()) {
            response.setCode("0301903");
            response.setDetail("Se debe enviar el número de factura correspondiente a la compra");
            return response;
        }

        //Valida que se haya enviado correctamente la referencia del articulo
        if (serviceRequest.getItemCode() == null || serviceRequest.getItemCode().trim().isEmpty()) {
            response.setCode("0301904");
            response.setDetail("Se debe enviar la referencia para la cual se solicita la garantía");
            return response;
        }

        //Valida que se haya enviado correctamente el asunto (titulo)
        if (serviceRequest.getSubject() == null || serviceRequest.getSubject().trim().isEmpty()) {
            response.setCode("0301905");
            response.setDetail("Se debe enviar un asunto que resuma el problema");
            return response;
        }

        //Valida que la factura exista y que contenga el articulo relacionado
        Integer invoiceNumber = facturaFacade.findByDocNumWithPrefixAndItemCode(serviceRequest.getInvoiceNumber(), serviceRequest.getItemCode());
        if (invoiceNumber == null) {
            response.setCode("0301909");
            response.setDetail("El numero de factura enviado no se encuentra registrado o no contiene el item especificado");
            return response;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ServiceCallDTO serviceCall = new ServiceCallDTO();
        serviceCall.setCallType(1L);//1=garantia
        serviceCall.setCardCode(serviceRequest.getCustomerId());
        serviceCall.setItemCode(serviceRequest.getItemCode());
        serviceCall.setOrigin(ServiceCallDTO.Origin.WEB);
        serviceCall.setPriority(ServiceCallDTO.Priority.HIGH);
        serviceCall.setProblemType(ServiceCallDTO.ProblemType.HIGH);
        serviceCall.setSeries(36L);//TODO: parametrizar
        serviceCall.setSubject(serviceRequest.getSubject());
        serviceCall.setInvoiceNumber(invoiceNumber.toString());

        StringBuilder description = new StringBuilder();
        description.append(sdf.format(new Date()));
        description.append(": ");
        description.append(serviceRequest.getDescription());
        if (!serviceRequest.getProblems().isEmpty()) {
            description.append(". El cliente reportó los siguientes problemas:\n");
            for (ProblemTypeDTO problem : serviceRequest.getProblems()) {
                description.append("   - ");
                description.append(problem.getName());
                description.append("\n");
            }
        }
        serviceCall.setDescription(description.toString());

        Long idEmployee = assignEmployee();
        if (idEmployee == -1L) {
            response.setCode("0301906");
            response.setDetail("No hay empleados del departamento de servicio al cliente activos para asignarles la solicitud");
            return response;
        }
        serviceCall.setAssigneeCode(idEmployee);

        try {
            SesionSAPB1WSDTO sesionSap = webIntegratorAppBean.obtenerSesionDisponible();
            ServiceCallsServiceConnector scsc = sapB1WSBean.getServiceCallsServiceConnectorInstance(sesionSap.getIdSesionSAP());
            Long serviceCallId = scsc.createServiceCall(serviceCall);
            webIntegratorAppBean.devolverSesion(sesionSap);
            if (serviceCallId != null && serviceCallId > 0) {
                try {
                    List<MailAttachmentDTO> attachments = new ArrayList<>();
                    for (ServiceRequestImageDTO img : serviceRequest.getImages()) {
                        //String fileExtension = img.getImageName().substring(img.getImageName().indexOf(".", img.getImageName().length() - 5) + 1);
                        attachments.add(new MailAttachmentDTO("imagen", img.getImageName(), MailAttachmentDTO.SOURCE_WEB));
                    }
                    sendNewRequestNotificationEmail(serviceCallId, serviceCall, attachments);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Ocurrio un error al enviar el correo de notificacion de nueva solicitud de servicio. ", e);
                }
                response.setCode("0301001");
                response.setDetail(serviceCallId.toString());
                return response;
            } else {
                response.setCode("0301907");
                response.setDetail("No se pudo obtener el numero de la garantia");
                return response;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al crear la solicitud de garantia. ", e);
            response.setCode("0301908");
            response.setDetail("No se pudo registrar la solicitud de garantia. " + e.getMessage());
            return response;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ServiceRequestHistoryDTO getRequestHistory(Integer ticketNumber) {
        ServiceRequestHistoryDTO response = new ServiceRequestHistoryDTO();
        List<Object[]> resultado = llamadaServicioFacade.getStatusChanges(ticketNumber);
        List<ServiceRequestStatusDTO> history = new ArrayList<>();
        for (Object[] fila : resultado) {
            history.add(new ServiceRequestStatusDTO((Short) fila[0], (String) fila[1], (Date) fila[2]));
        }

        //Consulta los comentarios y los formatea para agregarlos a la lista del historial
        LlamadaDeServicio llamada = llamadaServicioFacade.findByDocNum(ticketNumber);
        String historialComentarios = llamada.getDescrption();
        String[] listaComentarios = StringUtils.split(historialComentarios, "\r");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (String comentario : listaComentarios) {
            log.log(Level.INFO, "Procesando comentario [{0}]", comentario);
            String fecha = comentario.substring(0, 16);
            String com = comentario.substring(18);
            try {
                history.add(new ServiceRequestStatusDTO((short) 0, com, sdf.parse(fecha)));
            } catch (ParseException ex) {
                log.log(Level.SEVERE, "Ocurrio un error al convertir la fecha [{0}]", fecha);
            }
        }
        Collections.sort(history);
        try {
            response.setStatusCode(llamada.getStatus().toString());
            response.setStatusName(ServiceCallStatus.getById(llamada.getStatus().intValue()).getNombre());
        } catch (Exception e) {
            log.log(Level.SEVERE, "No fue posible obtener el estado actual de la solicitud de servicio. ", e);
            //TODO: enviar email de notificacion de error
        }

        response.setHistory(history);
        return response;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse addCommentsToRequest(Object comment) {
        GenericResponse response = new GenericResponse();
        RequestCommentDTO request = (RequestCommentDTO) comment;
        Long ticketNumber = request.getRequestID();
        String newComments = request.getComment();

        if (ticketNumber == null || ticketNumber <= 0) {
            response.setCode("0302910");
            response.setDetail("El numero del ticket no es valido");
            return response;
        }

        if (newComments == null || newComments.trim().isEmpty()) {
            response.setCode("0302911");
            response.setDetail("El comentario a adicionar se encuentra vacio. Especifique un comentario valido");
            return response;
        }

        try {
            SesionSAPB1WSDTO sesionSap = webIntegratorAppBean.obtenerSesionDisponible();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ServiceCallsServiceConnector scsc = sapB1WSBean.getServiceCallsServiceConnectorInstance(sesionSap.getIdSesionSAP());
            scsc.addCommentToServiceCall(ticketNumber, sdf.format(new Date()).concat(": ").concat(newComments));
            webIntegratorAppBean.devolverSesion(sesionSap);
        } catch (Exception e) {
            response.setCode("0302912");
            response.setDetail("Ocurrio un error al modificar la solicitud. " + e.getMessage());
            return response;
        }

        sendNewMessageNotificationEmail(ticketNumber, newComments);
        response.setCode("0302001");
        response.setDetail("El comentario se agrego de manera exitosa");
        return response;
    }

    private void sendNewMessageNotificationEmail(Long ticketNumber, String nuevoComentario) {
        try {
            MailMessageDTO mailMessage = new MailMessageDTO();
            mailMessage.setFrom("Notificaciones SAP <notificaciones@matisses.co>");
            mailMessage.setSubject("Nuevo comentario agregado a solicitud #" + ticketNumber);
            mailMessage.addToAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("nuevo_comentario_llamada_servicio").getTo());
            mailMessage.addCcAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("nuevo_comentario_llamada_servicio").getCc());
            mailMessage.addBccAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("nuevo_comentario_llamada_servicio").getBcc());

            Map<String, String> parameters = new HashMap<>();
            parameters.put("ticketNumber", ticketNumber.toString());
            parameters.put("nuevoComentario", nuevoComentario);
            emailSender.sendMail(mailMessage, SendHTMLEmailMBean.MessageTemplate.nuevo_comentario_llamada_servicio, parameters, null);
        } catch (Exception e) {
            log.log(Level.SEVERE, "No se pudo enviar el correo de notificacion. ", e);
        }
    }

    private void sendNewRequestNotificationEmail(Long ticketNumber, ServiceCallDTO serviceCall, List<MailAttachmentDTO> adjuntos) {
        try {
            MailMessageDTO mailMessage = new MailMessageDTO();
            mailMessage.setFrom("Notificaciones SAP <notificaciones@matisses.co>");
            mailMessage.setSubject("Nueva solicitud de servicio #" + ticketNumber);
            mailMessage.addToAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("llamada_servicio").getTo());
            mailMessage.addCcAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("llamada_servicio").getCc());
            mailMessage.addBccAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("llamada_servicio").getBcc());

            Map<String, String> parameters = new HashMap<>();
            parameters.put("ticketNumber", ticketNumber.toString());
            parameters.put("comentario", serviceCall.getDescription().replaceAll("\n", "<br/>"));
            parameters.put("itemCode", serviceCall.getItemCode());
            parameters.put("cardCode", serviceCall.getCardCode());
            emailSender.sendMail(mailMessage, SendHTMLEmailMBean.MessageTemplate.llamada_servicio, parameters, adjuntos);
        } catch (Exception e) {
            log.log(Level.SEVERE, "No se pudo enviar el correo de notificacion. ", e);
        }
    }
}
