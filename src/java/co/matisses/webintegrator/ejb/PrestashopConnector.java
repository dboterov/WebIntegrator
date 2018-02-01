package co.matisses.webintegrator.ejb;

import co.matisses.webintegrator.dto.CustomerDTO;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author dbotero
 */
@Stateless
public class PrestashopConnector {

    private String serviceUrl;
    private String namespaceUri;
    private String serviceName;
    private String portName;
    private static final Logger log = Logger.getLogger(PrestashopConnector.class.getSimpleName());

    public PrestashopConnector() {
        try {
            Properties props = new Properties();
            String serverConfUrl = System.getProperty("jboss.server.config.url");
            String propertiesFileName = "prestashop.properties";
            String path = serverConfUrl + propertiesFileName;
            URL url = new URL(path);
            if (new File(url.toURI()).exists()) {
                props.load(url.openStream());
            } else {
                props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + propertiesFileName));
            }
            if (props != null && !props.isEmpty()) {
                //http://www2.matisses.co/modules/wsmatisses/servicio.php?wsdl
                serviceUrl = props.getProperty("service.url");
                //urn:server
                namespaceUri = props.getProperty("service.namespace.uri");
                //server
                serviceName = props.getProperty("service.name");
                //serverPort
                portName = props.getProperty("service.port.name");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "There was an error loading the file. " + e.getMessage(), e);
        }
    }

//    public CustomerDTO findCustomerById(CustomerDTO customer){
//        if(customer == null){
//            log.log(Level.SEVERE, "Calling operation get customer to prestashop with NULL params");
//            return null;
//        }
//        log.log(Level.INFO, "Calling operation get customer to prestashop with id[{0}]", customer.getId());
//        try {
//            return CustomerDTO.fromXML(callService("customer", "get", "sap", customer.toXML()));
//        } catch (Exception e) {
//            log.log(Level.SEVERE, "Ocurrio un error al buscar cliente por ID", e);
//            return null;
//        }
//    }
    public void createCustomer(CustomerDTO customer) {
        if (customer == null) {
            log.log(Level.SEVERE, "Calling operation create customer to prestashop with NULL params");
            return;
        }
        log.log(Level.INFO, "Calling operation create customer to prestashop with params[{0}]", customer.toString());
    }

//    public String callService(String param1, String param2, String param3, String param4) {
//        try {
//            URL helloWsdlUrl = new URL(this.serviceUrl);
//
//            ServiceFactory serviceFactory = ServiceFactory.newInstance();
//            Service prestashopService = serviceFactory.createService(helloWsdlUrl, new QName(namespaceUri, serviceName));
//
//            ServerPortType myProxy = (ServerPortType) prestashopService.getPort(new QName(namespaceUri, portName), ServerPortType.class);
//            String result = myProxy.wsmatisses(param1, param2, param3, param4);
//            log.info(result);
//            return result;
//        } catch (Exception ex) {
//            log.log(Level.SEVERE, "There was an error calling prestashop service " + ex.getMessage(), ex);
//            //ex.printStackTrace();
//            return null;
//        }
//    }
}
