package co.matisses.webintegrator.ws;

import co.matisses.persistence.web.entity.GenericCall;
import co.matisses.persistence.web.entity.LogWebIntegrator;
import co.matisses.persistence.web.facade.GenericCallFacade;
import co.matisses.persistence.web.facade.LogWebIntegratorFacade;
import co.matisses.webintegrator.dto.GenericRequest;
import co.matisses.webintegrator.dto.GenericResponse;
import co.matisses.webintegrator.dto.Operation;
import co.matisses.webintegrator.dto.WebObject;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dbotero
 */
@WebService
public class GenericFacade implements Serializable {

    @Inject
    private WebIntegratorMBean appBean;
    @EJB
    private GenericCallFacade genericCallFacade;
    @EJB
    private LogWebIntegratorFacade logWebIntegratorFacade;
    private static final Logger log = Logger.getLogger(GenericFacade.class.getSimpleName());

    public GenericFacade() {
    }

    @WebMethod(exclude = true)
    public WebIntegratorMBean getAppBean() {
        return appBean;
    }

    @WebMethod(exclude = true)
    public void setAppBean(WebIntegratorMBean appBean) {
        this.appBean = appBean;
    }

    public GenericResponse listAvailableObjects() {
        log.info("Listing available objects");

        List<WebObject> objetos = appBean.listObjects();

        StringBuilder sb = new StringBuilder();
        for (WebObject o : objetos) {
            try {
                sb.append(o.toXML());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Could not marshall object " + o.getName(), e);
            }
        }
        return new GenericResponse("0102001", sb.toString());
    }

    public GenericResponse listAvailableOperations() {
        log.info("Listing available operations");

        List<Operation> objetos = appBean.listOperations();

        StringBuilder sb = new StringBuilder();
        for (Operation o : objetos) {
            try {
                sb.append(o.toXML());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Could not marshall operation " + o.getName(), e);
            }
        }
        return new GenericResponse("0103001", sb.toString());
    }

    public GenericResponse callService(@WebParam(name = "genericRequest") GenericRequest request) {
        long idOperacion = System.currentTimeMillis();
        if (request == null) {
            log.log(Level.SEVERE, "A null request has been received... ");
            GenericResponse response = new GenericResponse("0101901", "Se recibió una solicitud nula que no puede ser procesada");
            log.log(Level.INFO, "Closing call for operation for null request. ");
            return response;
        }

        log.log(Level.INFO, "Calling operation [{0}] for object [{1}] with data [{2}]",
                new Object[]{request.getOperation(), request.getObject(), StringUtils.normalizeSpace(request.getData())});
        LogWebIntegrator entry = logRequest(idOperacion, request.getObject(), request.getOperation(), request.getData());

        //Valida que los valores no esten vacios
        if (request.getOperation() == null || request.getOperation().trim().isEmpty()) {
            GenericResponse response = new GenericResponse("0101902", "Se debe especificar la operacion");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101902", "Se debe especificar la operacion");
            return response;
        }
        if (request.getObject() == null || request.getObject().trim().isEmpty()) {
            GenericResponse response = new GenericResponse("0101903", "Se debe especificar el objeto");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101903", "Se debe especificar el objeto");
            return response;
        }
        if (request.getSource() == null || request.getSource().trim().isEmpty()) {
            GenericResponse response = new GenericResponse("0101904", "Se debe especificar el origen del llamado");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101904", "Se debe especificar el origen del llamado");
            return response;
        }

        //Valida que el objeto enviado se encuentre en el mapa de objetos
        if (!appBean.containsObject(request.getObject())) {
            GenericResponse response = new GenericResponse("0101905", "El objeto enviado no se reconoce [" + request.getObject() + "]");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101905", "El objeto enviado no se reconoce [" + request.getObject() + "]");
            return response;
        }

        //Valida que la operacion enviada se encuentre en la lista de operaciones
        if (!appBean.containsOperation(request.getOperation())) {
            GenericResponse response = new GenericResponse("0101906", "La operacion enviada no se reconoce [" + request.getOperation() + "]");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101906", "La operacion enviada no se reconoce [" + request.getOperation() + "]");
            return response;
        }

        //Busca en base de datos la configuracion de ejecucion para el objeto y operacion enviados. La respuesta indica
        //cual clase y cual metodo dentro de esa clase debe ser invocado, ademas de los parametros que recibe el metodo
        GenericCall methodCall = genericCallFacade.findByObjectAndOperation(appBean.getObjectId(request.getObject()), appBean.getOperationId(request.getOperation()));
        if (methodCall == null) {
            GenericResponse response = new GenericResponse("0101907", "No se ha configurado un servicio para la combinacion (objeto,metodo) (" + request.getObject() + "," + request.getOperation() + ")");
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101907", "No se ha configurado un servicio para la combinacion (objeto,metodo) (" + request.getObject() + "," + request.getOperation() + ")");
            return response;
        }

        //Si la llamada generica configurada indica que el metodo recibe parametros y el request trae el parametro data vacio, informa al usuario del error
        if (methodCall.getParameters() != null && !methodCall.getParameters().isEmpty() && (request.getData() == null || request.getData().trim().isEmpty())) {
            GenericResponse response = new GenericResponse("0101908", "Se intentó llamar la operación " + request.getOperation() + " del objeto " + request.getObject()
                    + " sin parámetros, pero la función solicita los siguientes parametros: " + methodCall.getParameters());
            log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
            logResponse(entry, "0101908", "La operacion enviada no se reconoce [" + request.getOperation() + "]");
            return response;
        }

        boolean continuar = true;
        while (continuar) {
            try {
                String facadeName = String.format("java:global/WebIntegrator/%1$s!co.matisses.webintegrator.ejb.%1$s", methodCall.getClassName());

                InitialContext ctx = new InitialContext();
                Object facade = ctx.lookup(facadeName);
                continuar = false;

                log.log(Level.FINEST, "---- {0} ----", facade.getClass().getCanonicalName());
                for (Method method : facade.getClass().getDeclaredMethods()) {
                    log.log(Level.FINEST, "{0}\t{1}\t", new Object[]{method.getName(), method.getReturnType().getCanonicalName()});
                    for (Class cl : method.getParameterTypes()) {
                        log.log(Level.FINEST, "\t{0}", cl.getCanonicalName());
                    }
                }

                Object result = null;
                if (methodCall.getParameters() != null && methodCall.getParameters().length() > 0 && methodCall.getParameters().contains("Object")) {
                    Object inputParam = Class.forName(methodCall.getXmlDataType()).getMethod("fromXML", String.class).invoke(null, request.getData());
                    Method method = facade.getClass().getMethod(methodCall.getMethodName(), Class.forName(methodCall.getParameters()));
                    result = method.invoke(facade, inputParam);
                } else if (methodCall.getParameters() != null && methodCall.getParameters().length() > 0 && !methodCall.getParameters().contains("Object")) {
                    Method method = facade.getClass().getMethod(methodCall.getMethodName(), Class.forName(methodCall.getParameters()));
                    if (methodCall.getParameters().equals("java.lang.Integer")) {
                        result = method.invoke(facade, Integer.parseInt(request.getData()));
                    } else {
                        result = method.invoke(facade, request.getData());
                    }
                } else {
                    Method method = facade.getClass().getMethod(methodCall.getMethodName());
                    result = method.invoke(facade);
                }

                //Valida que el metodo haya retornado un valor valido, con base en la configuracion del generic call
                if (result == null && !methodCall.getReturnType().equalsIgnoreCase("void")) {
                    GenericResponse response = new GenericResponse("0101909", "El método invocado no retornó ningún valor");
                    log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
                    logResponse(entry, "0101909", "El método invocado no retornó ningún valor");
                    return response;
                } else if (!methodCall.getReturnType().equalsIgnoreCase("void")) {
                    if (methodCall.getReturnType().equals("co.matisses.webintegrator.dto.GenericResponse")) {
                        log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), ((GenericResponse) result).toString()});
                        logResponse(entry, ((GenericResponse)result).getCode(), ((GenericResponse)result).getDetail());
                        return (GenericResponse) result;
                    } else if (methodCall.getReturnType().startsWith("java.lang")) {
                        GenericResponse response = new GenericResponse("0101001", result.toString());
                        log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), result.toString()});
                        logResponse(entry, response.getCode(), response.getDetail());
                        return response;
                    } else {
                        GenericResponse response = new GenericResponse("0101002", (String) Class.forName(methodCall.getReturnType()).getMethod("toXML").invoke(result));
                        log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
                        logResponse(entry, response.getCode(), response.getDetail());
                        return response;
                    }
                } else {
                    GenericResponse response = new GenericResponse("0101001", "Se ejecutó la operación de manera exitosa");
                    log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
                    logResponse(entry, response.getCode(), response.getDetail());
                    return response;
                }
            } catch (NamingException | SecurityException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                GenericResponse response = new GenericResponse("0101910", "Ocurrio un error ejecutando la funcion. " + e.getMessage());
                log.log(Level.INFO, "Closing call for operation [{0}] for object [{1}]. Response {2}", new Object[]{request.getOperation(), request.getObject(), response.toString()});
                log.log(Level.SEVERE, "", e);
                logResponse(entry, response.getCode(), response.getDetail());
                return response;
            }
        }
        return null;
    }

    private LogWebIntegrator logRequest(Long idOperacion, String objeto, String operacion, String datos) {
        LogWebIntegrator instanciaLog = new LogWebIntegrator();
        instanciaLog.setDatos(StringUtils.normalizeSpace(datos));
        instanciaLog.setFecha(new Date());
        instanciaLog.setIdOperacion(idOperacion);
        instanciaLog.setObjeto(objeto);
        instanciaLog.setOperacion(operacion);
        try {
            logWebIntegratorFacade.create(instanciaLog);
            return instanciaLog;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al registrar el evento en la base de datos. ", e);
            return null;
        }
    }

    private void logResponse(LogWebIntegrator entry, String codigo, String detalle) {
        if (entry == null) {
            return;
        }
        entry.setCodigoRespuesta(codigo);
        entry.setDetalleRespuesta(detalle);
        entry.setTiempoRespuesta(new Date());
        try {
            logWebIntegratorFacade.edit(entry);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al actualizar la respuesta de la operacion " + entry.getIdOperacion() + ". ", e);
        }
    }
}
