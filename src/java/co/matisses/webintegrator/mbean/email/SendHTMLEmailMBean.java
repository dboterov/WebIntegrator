package co.matisses.webintegrator.mbean.email;

import co.matisses.webintegrator.dto.MailAttachmentDTO;
import co.matisses.webintegrator.dto.MailMessageDTO;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 *
 * @author dbotero
 */
@ApplicationScoped
@Named("sendHTMLEmailMBean")
public class SendHTMLEmailMBean {

    private static final Logger log = Logger.getLogger(SendHTMLEmailMBean.class.getSimpleName());
    private String username;
    private String password;
    private String host;
    private String templatesFolder;
    private Properties props;
    @Inject
    private WebIntegratorMBean webIntegratorAppBean;

    public static enum MessageTemplate {
        factura, factura_web, cotizacion, traslado, cambiar_modelo, proforma, inventarios, llamada_servicio, nuevo_comentario_llamada_servicio, error;
    }

    public SendHTMLEmailMBean() {
    }

    @PostConstruct
    protected void initialize() {
        cargarConfiguracion();
    }

    private void inicializarParametros() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
    }

    private void cargarConfiguracion() {
        username = webIntegratorAppBean.obtenerValorPropiedad("mail.username");
        password = webIntegratorAppBean.obtenerValorPropiedad("mail.password");
        host = webIntegratorAppBean.obtenerValorPropiedad("mail.host");
        templatesFolder = webIntegratorAppBean.obtenerValorPropiedad("mail.templates");
        inicializarParametros();
    }

    private boolean validarConfiguracion() {
        //valida si los valores de configuracion son correctos
        if (username == null || password == null || host == null) {
            //si no se han cargado los valores de configuracion, vuelve a leer el archivo properties e intenta cargar los valores
            webIntegratorAppBean.cargarProperties();
            cargarConfiguracion();
            //vuelve a validar si los valores de configuracion fueron encontrados en el archivo
            if (username == null || password == null || host == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void sendMail(MailMessageDTO mailMessage, MessageTemplate templateName, Map<String, String> params, List<MailAttachmentDTO> attachments) throws Exception {
        if (!validarConfiguracion()) {
            log.log(Level.SEVERE, "No fue posible cargar los valores de configuracion del archivo baru.properties, por lo tanto no es posible enviar el mensaje {0}. ", mailMessage.toString());
            return;
        }
        log.log(Level.INFO, "Iniciando sesion en servidor de correo");
        // Get the Session object.
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        log.log(Level.INFO, "Sesion iniciada correctamente en servidor de correo");

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailMessage.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailMessage.getToList()));
            message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(mailMessage.getCcList()));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(mailMessage.getBccList()));
            message.setSubject(mailMessage.getSubject());

            //Valida que la plantilla exista
            String fullTemplateName = templatesFolder + templateName + ".html";
            log.log(Level.INFO, "Buscando plantilla {0}", fullTemplateName);
            if (!new File(fullTemplateName).exists()) {
                log.log(Level.SEVERE, "No fue posible enviar el mensaje. La plantilla {0} no existe.", fullTemplateName);
                throw new Exception("No fue posible enviar el mensaje. La plantilla " + fullTemplateName + " no existe.");
            }

            Multipart multipart = new MimeMultipart();
            BodyPart contentBodyPart = new MimeBodyPart();
            //Agrega el cuerpo del mensaje a partir de una plantilla
            try {
                String templateContent = new String(Files.readAllBytes(Paths.get(fullTemplateName)), StandardCharsets.UTF_8);
                //message.setContent(StrSubstitutor.replace(templateContent, params), "text/html");
                contentBodyPart.setContent(StrSubstitutor.replace(templateContent, params), "text/html");
                multipart.addBodyPart(contentBodyPart);
            } catch (IOException | MessagingException e) {
                log.log(Level.SEVERE, "No fue posible cargar la plantilla del mensaje. ", e);
                throw new Exception("No fue posible cargar la plantilla del mensaje. " + e.getMessage());
            }

            //Agrega los archivos adjuntos
            try {
                if (attachments != null) {
                    for (MailAttachmentDTO attachment : attachments) {
                        BodyPart attachmentBodyPart = new MimeBodyPart();
                        DataSource source = null;
                        if (attachment.getSource().equals(MailAttachmentDTO.SOURCE_WEB)) {
                            source = new URLDataSource(new URL(attachment.getFileURL()));
                        } else {
                            source = new FileDataSource(attachment.getFileURL());
                        }
                        attachmentBodyPart.setDataHandler(new DataHandler(source));
                        attachmentBodyPart.setFileName(attachment.getFileName());
                        multipart.addBodyPart(attachmentBodyPart);
                    }
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "No fue posible procesar los archivos adjuntos. ", e);
            }
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            log.log(Level.SEVERE, "No fue posible enviar el mensaje de correo. ", e);
            throw new Exception(e);
        }
    }
}
