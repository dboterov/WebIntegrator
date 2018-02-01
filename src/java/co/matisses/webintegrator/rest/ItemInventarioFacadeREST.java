package co.matisses.webintegrator.rest;

import co.matisses.persistence.sap.entity.ItemInventario;
import co.matisses.persistence.sap.facade.AbstractFacade;
import co.matisses.webintegrator.dto.InventoryItemPrestashopDTO;
import co.matisses.webintegrator.dto.InventoryItemStatusDTO;
import co.matisses.webintegrator.dto.ItemBrandDTO;
import co.matisses.webintegrator.dto.ItemColorDTO;
import co.matisses.webintegrator.dto.ItemMaterialDTO;
import co.matisses.webintegrator.dto.MailMessageDTO;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import co.matisses.webintegrator.mbean.email.SendHTMLEmailMBean;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author dbotero
 */
@Stateless
@Path("iteminventario")
public class ItemInventarioFacadeREST extends AbstractFacade<ItemInventario> {

    private static final Logger log = Logger.getLogger(ItemInventarioFacadeREST.class.getSimpleName());
    @PersistenceContext(unitName = "SAPPersistencePU")
    private EntityManager em;
    @Inject
    private WebIntegratorMBean webIntegratorAppBean;
    @Inject
    private SendHTMLEmailMBean emailSender;

    public ItemInventarioFacadeREST() {
        super(ItemInventario.class);
    }

    private List<Object[]> queryStatusData(String ref) {
        log.log(Level.INFO, "consultando informacion en BD para ref {0}", ref);
        List<Object[]> results = em.createNativeQuery("select * from FN_ESTADO_PRODUCTOS_WEB(?)").setParameter(1, ref).getResultList();
        log.log(Level.INFO, "consulta a BD finalizada. {0} filas recuperadas. Iniciando parseo de datos. ", results.size());
        return results;
    }

    private void processStatusData(List<Object[]> data, List<InventoryItemStatusDTO> items) {
        for (Object[] result : data) {
            String referencia = (String) result[0];
            String motivo = (String) result[1];
            //boolean tieneImagenes = hasImages(referencia);
            InventoryItemStatusDTO statusDto = new InventoryItemStatusDTO();
            statusDto.setActivo(motivo == null || motivo.isEmpty());
            statusDto.setMotivo(motivo);
            //statusDto.setMotivo((tieneImagenes ? "" : "imagenes,") + motivo);
            //statusDto.setActivo((motivo == null || motivo.isEmpty()) && tieneImagenes);
            statusDto.setReferencia(referencia);
            items.add(statusDto);
        }
    }

    private boolean hasImages(String reference) {
        log.log(Level.INFO, "Validando existencia de imagenes para la referencia {0}", reference);
        String rootImagesFolder = "W:\\";
        File mainFolder = new File(rootImagesFolder + File.separator + reference);
        if (mainFolder.exists()) {
            String imagesFolderName = "images";
            for (File folder : mainFolder.listFiles()) {
                if (folder.getName().equals(imagesFolderName)) {
                    for (File image : folder.listFiles()) {
                        if (image.getName().endsWith(".jpg") && image.getName().startsWith(reference)) {
                            log.log(Level.INFO, "Se encontro al menos una imagen valida para la referencia {0}", reference);
                            return true;
                        }
                    }
                    log.log(Level.SEVERE, "No se encontraron imagenes validas para la referencia {0}", reference);
                    return false;
                }
            }
            log.log(Level.SEVERE, "No existe la carpeta images para la referencia {0}", reference);
            return false;
        } else {
            log.log(Level.SEVERE, "No existe la carpeta raiz de imagenes para la referencia {0}", reference);
            return false;
        }
    }

    @POST
    @Path("estado")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response listItemsStatus(final String[] references) {
        log.log(Level.INFO, "consultando el estado de {0} productos para pagina web", (references == null ? "(todos los productos)" : references.length));
        try {
            List<InventoryItemStatusDTO> items = new ArrayList<>();
            if (references != null) {
                for (String ref : references) {
                    processStatusData(queryStatusData(ref), items);
                }
            } else {
                processStatusData(queryStatusData(null), items);
            }
            log.log(Level.INFO, "Finalizo la consulta con {0} items", items.size());
            return Response.ok(items).build();
        } catch (NoResultException e) {
            log.log(Level.WARNING, "No se encontro el articulo especificado.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al ejecutar la consulta de estado de articulos. ", e);
        }
        return Response.ok().build();
    }

    @GET
    @Path("inactivosConSaldo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listInactiveItemsWithStock() {
        return listInactiveItemsWithStock(null);
    }

    @GET
    @Path("inactivosConSaldo/{reference}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listInactiveItemsWithStock(@PathParam("reference") String reference) {
        log.log(Level.INFO, "consultando los productos inactivos y con saldo {0}", (reference != null ? "con referencia " + reference : "(todos los productos)"));
        try {
            List<InventoryItemStatusDTO> items = new ArrayList<>();
            log.log(Level.INFO, "consultando informacion en BD");
            List<Object[]> results = em.createNativeQuery("select * from FN_ESTADO_PRODUCTOS_WEB(?)").setParameter(1, reference).getResultList();
            log.log(Level.INFO, "consulta a BD finalizada. {0} filas recuperadas. Iniciando parseo de datos. ", results.size());
            for (Object[] result : results) {
                String referencia = (String) result[0];
                String motivo = (String) result[1];
                if (motivo != null && !motivo.isEmpty() && !motivo.contains("saldo")) {
                    InventoryItemStatusDTO statusDto = new InventoryItemStatusDTO();
                    statusDto.setActivo(false);
                    statusDto.setMotivo(motivo);
                    statusDto.setReferencia(referencia);
                    items.add(statusDto);
                }
            }
            log.log(Level.INFO, "Parseo de datos finalizado. {0} articulos encontrados. ", items.size());
            return Response.ok(items).build();
        } catch (NoResultException e) {
            log.log(Level.WARNING, "No se encontro el articulo especificado. {0}", reference);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al ejecutar la consulta de articulos inactivos con saldo. ", e);
        }
        return Response.ok().build();
    }

    private List<Object[]> queryData(String ref, Integer minutos, boolean includeAll) {
        log.log(Level.INFO, "consultando informacion en BD para referencia {0}", (ref == null ? "(todas las referencias)" : ref));
        List<Object[]> results = null;
        if (ref == null) {
            results = em.createNativeQuery("{call SP_CONSULTAR_PRODUCTOS_WEB(?,?,?)}").setParameter(1, ref).setParameter(2, minutos).setParameter(3, includeAll).getResultList();
        } else {
            results = em.createNativeQuery("{call SP_CONSULTAR_PRODUCTOS_WEB(?,?,?)}").setParameter(1, ref).setParameter(2, null).setParameter(3, includeAll).getResultList();
        }

        log.log(Level.INFO, "consulta a BD finalizada. {0} filas recuperadas. Iniciando parseo de datos. ", results.size());
        return results;
    }

    private void processQueryResult(List<InventoryItemPrestashopDTO> items, InventoryItemPrestashopDTO invItem, List<Object[]> results) {
        for (Object[] cols : results) {
            InventoryItemPrestashopDTO tmp = new InventoryItemPrestashopDTO();
            tmp.setItemCode((String) cols[0]);
            if (items.contains(tmp)) {
                invItem = items.get(items.indexOf(tmp));
            } else {
                invItem = null;
            }
            if (invItem == null) {
                invItem = processQueryResult(cols);
                items.add(invItem);
            } else {
                invItem.mergeStockAndMaterials(processQueryResult(cols));
                items.set(items.indexOf(invItem), invItem);
            }
        }
    }

    @POST
    @Path("consultaRecientes5M")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response listRecentlyChangedWebItems() {
        log.log(Level.INFO, "consultando informacion de productos con cambios en los ultimos 5 minutos");
        try {
            List<InventoryItemPrestashopDTO> items = new ArrayList<>();
            InventoryItemPrestashopDTO invItem = null;
            processQueryResult(items, invItem, queryData(null, 5, false));
            return Response.ok(items).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al ejecutar el procedimiento almacenado. ", e);
        }
        return Response.ok().build();
    }

    private List<InventoryItemPrestashopDTO> listWebItems(String[] references, boolean includeAll) {
        List<InventoryItemPrestashopDTO> items = new ArrayList<>();
        InventoryItemPrestashopDTO invItem = null;
        if (references != null) {
            for (String ref : references) {
                processQueryResult(items, invItem, queryData(ref, null, includeAll));
            }
        } else {
            //se envia el tiempo nulo ya que la programacion de tiempo se puede realizar en el procedimiento almacenado
            processQueryResult(items, invItem, queryData(null, null, includeAll));
        }
        log.log(Level.INFO, "Parseo de datos finalizado.");
        printResponseData(items);
        return items;
    }

    @POST
    @Path("consultaSinSaldo")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Response listAllWebItems(final String[] references) {
        log.log(Level.INFO, "consultando informacion de {0} productos para pagina web", (references == null ? 0 : references.length));
        try {
            return Response.ok(listWebItems(references, true)).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al ejecutar el procedimiento almacenado. ", e);
            sendErrorMail("listar productos web (listWebItems)", e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("consulta")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Response listWebItems(final String[] references) {
        log.log(Level.INFO, "consultando informacion de {0} productos para pagina web", (references == null ? 0 : references.length));
        try {
            return Response.ok(listWebItems(references, false)).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al ejecutar el procedimiento almacenado. ", e);
            sendErrorMail("listar productos web (listWebItems)", e.getMessage());
            return Response.serverError().build();
        }
    }

    private void printResponseData(List<InventoryItemPrestashopDTO> items) {
        int reprocesarFoto = 0;
        for (InventoryItemPrestashopDTO dto : items) {
            log.info(dto.toString());
            if (dto.getProcessImages().equals("1")) {
                reprocesarFoto++;
            }
        }
        log.log(Level.INFO, "Se obtuvieron {0} items, de los cuales {1} estan marcados para reprocesar imagenes", new Object[]{items.size(), reprocesarFoto});
    }

    private void sendErrorMail(String methodName, String errorMessage) {
        MailMessageDTO mailMessage = new MailMessageDTO();
        mailMessage.setFrom("Errores sonda sitio web <erroressitioweb@matisses.co>");
        mailMessage.setSubject("Ocurrio un error al ejecutar la operacion " + methodName);
        mailMessage.addToAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("error").getTo());
        mailMessage.addCcAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("error").getCc());
        mailMessage.addBccAddresses(webIntegratorAppBean.getDestinatariosPlantillaEmail().get("error").getBcc());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("errorMessage", errorMessage);
        parameters.put("processName", methodName);
        parameters.put("username", "prestashop");
        try {
            emailSender.sendMail(mailMessage, SendHTMLEmailMBean.MessageTemplate.error, parameters, null);
        } catch (Exception e) {
            log.log(Level.SEVERE, "No se pudo enviar el correo de notificacion. ", e);
        }
    }

    private InventoryItemPrestashopDTO processQueryResult(Object cols[]) {
        int col = 0;

        String itemCode = (String) cols[col++];
        String providerCode = itemCode != null ? itemCode.substring(0, 3) : "";
        String itemName = (String) cols[col++];
        String webName = (String) cols[col++];
        String keyWords = (String) cols[col++];
        Integer price = (Integer) cols[col++];
        String subgroupCode = (String) cols[col++];
        Integer height = (Integer) cols[col++];
        Integer depth = (Integer) cols[col++];
        Integer width = (Integer) cols[col++];
        Integer weight = (Integer) cols[col++];
        String model = (String) cols[col++];
        String colorCode = (String) cols[col++];
        String colorName = (String) cols[col++];
        String colorHexa = (String) cols[col++];
        String description = (String) cols[col++];
        Date newFrom = (Date) cols[col++];
        //String idYoutube = (String) cols[col++];
        String descCorta = (String) cols[col++];
        Date reprocessImages = (Date) cols[col++];
        String brandCode = (String) cols[col++];
        String brandName = (String) cols[col++];
        String mainCombination = (String) cols[col++];
        String warehouse = (String) cols[col++];
        Integer quantity = (Integer) cols[col++];
        String materialCode = (String) cols[col++];
        String materialName = (String) cols[col++];
        String materialCare = (String) cols[col++];

        InventoryItemPrestashopDTO invItem = new InventoryItemPrestashopDTO();
        invItem.setItemCode(itemCode);
        invItem.setProviderCode(providerCode);
        invItem.setItemName(itemName);
        invItem.setSubgroupCode(subgroupCode);
        invItem.setWebName(webName);
        invItem.setKeyWords(keyWords);
        invItem.setPrice(price != null ? price.toString() : "0");
        invItem.setHeight(height != null ? height.toString() : "");
        invItem.setDepth(depth != null ? depth.toString() : "");
        invItem.setWidth(width != null ? width.toString() : "");
        invItem.setWeight(weight != null ? weight.toString() : "");
        invItem.setModel(model);
        invItem.setMainCombination(mainCombination);
        invItem.setDescription(description);
        if (descCorta == null || descCorta.trim().isEmpty()) {
            descCorta = description;
        }
        invItem.setShortDescription(descCorta);
        if (newFrom != null) {
            invItem.setNewFrom(Long.toString(newFrom.getTime()));
        }

        if (reprocessImages != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (sdf.format(reprocessImages).equals(sdf.format(new Date()))) {
                    invItem.setProcessImages("1");
                } else {
                    invItem.setProcessImages("0");
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Ocurrio un error al procesar la fecha del producto. [" + reprocessImages + "]", e);
                invItem.setProcessImages("0");
            }
        } else {
            invItem.setProcessImages("0");
        }
        invItem.addStock(warehouse, quantity);

        ItemColorDTO itemColor = new ItemColorDTO(colorCode, colorName, colorHexa);
        invItem.setColor(itemColor);

        ItemMaterialDTO itemMaterial = new ItemMaterialDTO(materialCode, materialName, materialCare);
        invItem.addMaterial(itemMaterial);

        ItemBrandDTO itemBrand = new ItemBrandDTO(brandCode, brandName);
        invItem.setBrand(itemBrand);

        return invItem;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
