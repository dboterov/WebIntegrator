package co.matisses.webintegrator.ejb;

import co.matisses.persistence.sap.entity.ItemInventario;
import co.matisses.persistence.sap.entity.SaldoItemInventario;
import co.matisses.persistence.sap.facade.ItemInventarioFacade;
import co.matisses.persistence.sap.facade.SaldoItemInventarioFacade;
import co.matisses.persistence.web.entity.TransportistaOrden;
import co.matisses.persistence.web.facade.TransportistaOrdenFacade;
import co.matisses.webintegrator.dto.InventoryChangesDTO;
import co.matisses.webintegrator.dto.InventoryItemDTO;
import co.matisses.webintegrator.dto.InventoryItemListDTO;
import co.matisses.webintegrator.dto.ItemBrandDTO;
import co.matisses.webintegrator.dto.ItemColorDTO;
import co.matisses.webintegrator.dto.ItemMaterialDTO;
import co.matisses.webintegrator.dto.SalesWarehouseDTO;
import co.matisses.webintegrator.dto.SalesWarehouseItemDTO;
import co.matisses.webintegrator.dto.ShippingQuotationResultDTO;
import co.matisses.webintegrator.dto.TransportistaDTO;
import co.matisses.webintegrator.dto.WebEnabledModelsDTO;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class InventoryItemEJB {

    private static final Logger log = Logger.getLogger(InventoryItemEJB.class.getSimpleName());
    private static final int MONTO_VENTA_2 = 2000000;
    private static final int MONTO_VENTA_7 = 7000000;
    private static final int MONTO_VENTA_300 = 300000;
    private static final int VALOR_MINIMO_FLETE = 30000;
    @Inject
    private WebIntegratorMBean webIntegratorBean;
    @EJB
    private ItemInventarioFacade inventoryItemFacade;
    @EJB
    private SaldoItemInventarioFacade saldoItemFacade;
    @EJB
    private ShippingPricesCache shippingPricesCache;
    @EJB
    private TransportistaOrdenFacade transportistaOrdenFacade;

    public InventoryItemEJB() {
    }

    public WebIntegratorMBean getWebIntegratorBean() {
        return webIntegratorBean;
    }

    public void setWebIntegratorBean(WebIntegratorMBean webIntegratorBean) {
        this.webIntegratorBean = webIntegratorBean;
    }

    private InventoryItemDTO entity2Dto(ItemInventario entity) {
        if (entity == null) {
            return null;
        }

        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setItemCode(entity.getItemCode());
        dto.setDepartmentCode(null);
        dto.setDepartmentName(null);
        dto.setGroupCode(null);
        dto.setGroupName(null);
        dto.setItemName(entity.getItemName());
        dto.setPrice(null);
        dto.setProviderCode(entity.getUURefPro());
        dto.setSubgroupCode(null);
        dto.setSubgroupName(null);

        return dto;
    }

    private InventoryItemDTO processQueryResultItemNameOnly(Object cols[]) {
        InventoryItemDTO invItem = new InventoryItemDTO();
        invItem.setItemCode((String) cols[0]);
        return invItem;
    }

    private InventoryItemDTO processQueryResult(Object cols[]) {
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
        String warehouse = (String) cols[col++];
        Integer quantity = (Integer) cols[col++];
        String model = (String) cols[col++];
        String colorCode = (String) cols[col++];
        String colorName = (String) cols[col++];
        String colorHexa = (String) cols[col++];
        String materialCode = (String) cols[col++];
        String materialName = (String) cols[col++];
        String materialCare = (String) cols[col++];
        String description = (String) cols[col++];
        Date newFrom = (Date) cols[col++];
        String idYoutube = (String) cols[col++];
        String descCorta = (String) cols[col++];
        Date reprocessImages = (Date) cols[col++];
        String mainCombination = (String) cols[col++];
        String brandCode = (String) cols[col++];
        String brandName = (String) cols[col++];

        String departmentCode = null;
        String departmentName = null;
        String groupCode = null;
        String groupName = null;
        String subgroupName = null;

        InventoryItemDTO invItem = new InventoryItemDTO();
        invItem.setItemCode(itemCode);
        invItem.setProviderCode(providerCode);
        invItem.setItemName(itemName);
        invItem.setDepartmentCode(departmentCode);
        invItem.setDepartmentName(departmentName);
        invItem.setGroupCode(groupCode);
        invItem.setGroupName(groupName);
        invItem.setSubgroupCode(subgroupCode);
        invItem.setSubgroupName(subgroupName);
        invItem.setWebName(webName);
        invItem.setIdYoutube(idYoutube);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public InventoryItemListDTO listByModel(Object obj) {
        InventoryItemListDTO items = new InventoryItemListDTO();
        try {
            InventoryItemDTO itemDto = (InventoryItemDTO) obj;
            boolean includeAll = ((InventoryItemDTO) itemDto).getIncludeAll() != null ? ((InventoryItemDTO) itemDto).getIncludeAll() : false;
            List<Object> results = inventoryItemFacade.getWebEnabledItemByModel(itemDto.getModel(), !includeAll);
            for (Object result : results) {
                Object cols[] = (Object[]) result;
                InventoryItemDTO invItem = processQueryResultItemNameOnly(cols);
                if (!items.contains(invItem)) {
                    items.add(invItem);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al listar productos por modelo. ", e);
            return null;
        }
        return items;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public InventoryItemDTO findByItemCode(Object item) {
        try {
            InventoryItemDTO invItem = null;
            boolean includeAll = ((InventoryItemDTO) item).getIncludeAll() != null ? ((InventoryItemDTO) item).getIncludeAll() : false;
            List<Object> results = inventoryItemFacade.getWebEnabledItem(((InventoryItemDTO) item).getItemCode(), !includeAll);
            for (Object result : results) {
                Object cols[] = (Object[]) result;
                if (invItem == null) {
                    invItem = processQueryResult(cols);
                } else {
                    invItem.mergeStockAndMaterials(processQueryResult(cols));
                }
            }
            return invItem;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al buscar productos por referencia. ", e);
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<InventoryItemDTO> listByDepartment(Object item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InventoryChangesDTO getStockChanges(Integer minutes) {
        InventoryChangesDTO changes = new InventoryChangesDTO();
        List<InventoryItemDTO> items = new ArrayList<>();
        List<Object> result = inventoryItemFacade.listChanges(minutes);
        for (Object row : result) {
            Object cols[] = (Object[]) row;
            String itemCode = (String) cols[0];
            String whsCode = (String) cols[1];
            Integer quantity = (Integer) cols[2];

            InventoryItemDTO item = new InventoryItemDTO();
            item.setItemCode(itemCode);
            if (items.contains(item)) {
                items.get(items.indexOf(item)).addStock(whsCode, quantity);
            } else {
                item.addStock(whsCode, quantity);
                items.add(item);
            }
        }
        changes.setChanges(items);
        return changes;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InventoryItemDTO getWebEnabledStock(Object item) {
        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setItemCode(((InventoryItemDTO) item).getItemCode());

        List<Object> stock = inventoryItemFacade.getWebEnabledStock(dto.getItemCode());
        for (Object row : stock) {
            Object cols[] = (Object[]) row;
            dto.addStock((String) cols[0], (Integer) cols[1]);
        }

        return dto;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InventoryChangesDTO getDetailedLastDayStockChanges() {
        InventoryChangesDTO changes = new InventoryChangesDTO();
        List<InventoryItemDTO> items = new ArrayList<>();
        List<Object> result = inventoryItemFacade.listDetailedLastDayChanges();
        InventoryItemDTO invItem = null;
        for (Object row : result) {
            Object cols[] = (Object[]) row;
            if (invItem == null) {
                invItem = processQueryResult(cols);
            } else {
                invItem.mergeStockAndMaterials(processQueryResult(cols));
            }
        }
        changes.setChanges(items);
        return changes;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InventoryChangesDTO getDetailedItemInfoWithStock(Object itemCodes) {
        InventoryChangesDTO changes = new InventoryChangesDTO();
        List<InventoryItemDTO> items = new ArrayList<>();
        List<String> references = new ArrayList<>();
        if (itemCodes != null && ((InventoryItemListDTO) itemCodes).getItems() != null) {
            for (InventoryItemDTO dto : ((InventoryItemListDTO) itemCodes).getItems()) {
                if (dto != null && dto.getItemCode() != null) {
                    references.add(dto.getItemCode());
                }
            }
        }
        List<Object> result = inventoryItemFacade.listItemsWithStock(references);
        InventoryItemDTO invItem = null;
        for (Object row : result) {
            Object cols[] = (Object[]) row;
            InventoryItemDTO tmp = new InventoryItemDTO();
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
        changes.setChanges(items);
        return changes;
    }

    /**
     * Se comenta el metodo ya que al parecer no esta en uso
     *
     * @return
     */
//    public InventoryChangesDTO getItemCodesWithStock() {
//        InventoryChangesDTO changes = new InventoryChangesDTO();
//        List<InventoryItemDTO> items = new ArrayList<>();
//        List<Object> result = inventoryItemFacade.listItemsWithStock();
//        Map<String, String> mapaReferencias = new HashMap<>();
//        for (Object row : result) {
//            Object cols[] = (Object[]) row;
//            String itemCode = (String) cols[0];
//            String model = (String) cols[12];
//
//            //Si el mapa ya contiene la referencia, la ignora y continua en el siguiente ciclo.
//            if (mapaReferencias.containsKey(itemCode)) {
//                continue;
//            }
//
//            mapaReferencias.put(itemCode, model);
//
//            InventoryItemDTO invItem = new InventoryItemDTO();
//            invItem.setItemCode(itemCode);
//            invItem.setModel(model);
//            items.add(invItem);
//        }
//        changes.setChanges(items);
//        return changes;
//    }
    public WebEnabledModelsDTO getModelsWithStock() {
        WebEnabledModelsDTO models = new WebEnabledModelsDTO();
        List<Object> result = inventoryItemFacade.listItemsWithStock(null);
        for (Object row : result) {
            Object cols[] = (Object[]) row;
            String itemCode = (String) cols[0];
            String model = (String) cols[12];

            models.addItemModel(model, itemCode);
        }
        return models;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SalesWarehouseDTO getSalesWarehouseByProduct(Object salesWarehouseDTO) throws ItemOutOfStockException {
        SalesWarehouseDTO dto = (SalesWarehouseDTO) salesWarehouseDTO;

        if (dto == null) {
            //TODO: retornar mensaje de error
            return null;
        }
        if (dto.getDestinationCityCode() == null) {
            //TODO: retornar mensaje de error
            return null;
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            //TODO: retornar mensaje de error
            return null;
        }

        SalesWarehouseDTO newDto = new SalesWarehouseDTO();
        newDto.setDestinationCityCode(dto.getDestinationCityCode());
        newDto.setPrestashopId(dto.getPrestashopId());

        for (SalesWarehouseItemDTO itemDto : dto.getItems()) {
            //consultar saldo por referencia
            List<SaldoItemInventario> saldo = saldoItemFacade.findWithParameters(itemDto.getItemCode(), "0", true);
            if (!saldo.isEmpty()) {
                List<SalesWarehouseItemDTO> items = getItemWarehouse(dto.getDestinationCityCode(), itemDto, saldo);
                newDto.getItems().addAll(items);
            } else {
                throw new ItemOutOfStockException(itemDto.getItemCode());
            }
        }
        return newDto;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SalesWarehouseItemDTO> getItemWarehouse(String destinationCityCode, SalesWarehouseItemDTO itemDto, List<SaldoItemInventario> stock) {
        List<String> excludedWarehouses = new ArrayList<>();

        //0.  si el saldo no se encuentra disponible, devolver un codigo de error
        if (stock.isEmpty()) {
            //TODO: retornar mensaje de error
            return null;
        }
        int saldoTotal = 0;
        for (SaldoItemInventario saldoItem : stock) {
            saldoTotal += saldoItem.getOnHandAsInt();
        }
        if (saldoTotal < itemDto.getQuantity()) {
            //TODO: retornar mensaje de error
            return null;
        }

        List<SalesWarehouseItemDTO> items = new ArrayList<>();
        //1.  si el saldo esta disponible en un solo almacen, seleccionarlo
        String whsSeleccionado = null;
        String sourceCityCode = null;
        for (SaldoItemInventario itemStock : stock) {
            if (itemStock.getOnHandAsInt() >= itemDto.getQuantity()) {
                whsSeleccionado = itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode();
                sourceCityCode = itemStock.getSaldoItemInventarioPK().getWhsCode().getCodigoCiudad();
                break;
            }
        }
        if (whsSeleccionado != null) {
            SalesWarehouseItemDTO newItemDto = new SalesWarehouseItemDTO();
            newItemDto.setItemCode(itemDto.getItemCode());
            newItemDto.setQuantity(itemDto.getQuantity());
            newItemDto.setWhsCode(whsSeleccionado);
            newItemDto.setSourceCityCode(sourceCityCode);
            items.add(newItemDto);
            return items;
        }

        //2.  si el saldo esta disponible en mas de un almacen y hay saldo en la ciudad de 
        //    destino tomar lo que haya en los almacenes de esa ciudad
        int saldoPendiente = itemDto.getQuantity();
        for (SaldoItemInventario itemStock : stock) {
            //si la lista de almacenes excluidos no contiene el almacen actual, lo procesa
            if (!excludedWarehouses.contains(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode())) {
                if (itemStock.getSaldoItemInventarioPK().getWhsCode().getCodigoCiudad().equals(destinationCityCode)) {
                    SalesWarehouseItemDTO newItemDto = new SalesWarehouseItemDTO();
                    newItemDto.setItemCode(itemDto.getItemCode());
                    newItemDto.setWhsCode(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode());
                    newItemDto.setSourceCityCode(itemStock.getSaldoItemInventarioPK().getWhsCode().getCodigoCiudad());

                    excludedWarehouses.add(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode());

                    if (saldoPendiente >= itemStock.getOnHandAsInt()) {
                        saldoPendiente -= itemStock.getOnHandAsInt();
                        newItemDto.setQuantity(itemStock.getOnHandAsInt());
                        items.add(newItemDto);
                    } else {
                        newItemDto.setQuantity(saldoPendiente);
                        saldoPendiente = 0;
                        items.add(newItemDto);
                        break;
                    }
                }
            }
        }

        //3.  si aun queda saldo pendiente, lo saca de los almacenes restantes
        if (saldoPendiente <= 0) {
            return items;
        }

        for (SaldoItemInventario itemStock : stock) {
            //si la lista de almacenes excluidos no contiene el almacen actual, lo procesa
            if (!excludedWarehouses.contains(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode())) {
                SalesWarehouseItemDTO newItemDto = new SalesWarehouseItemDTO();
                newItemDto.setItemCode(itemDto.getItemCode());
                newItemDto.setWhsCode(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode());
                newItemDto.setSourceCityCode(itemStock.getSaldoItemInventarioPK().getWhsCode().getCodigoCiudad());

                excludedWarehouses.add(itemStock.getSaldoItemInventarioPK().getWhsCode().getWhsCode());

                if (saldoPendiente >= itemStock.getOnHandAsInt()) {
                    saldoPendiente -= itemStock.getOnHandAsInt();
                    newItemDto.setQuantity(itemStock.getOnHandAsInt());
                    items.add(newItemDto);
                } else {
                    newItemDto.setQuantity(saldoPendiente);
                    saldoPendiente = 0;
                    items.add(newItemDto);
                    break;
                }
            }
        }
        return items;
    }

    private int getMatissesShippingCost(String destCityCode, List<SalesWarehouseItemDTO> items, List<Object[]> precios) throws DestinationNotSupportedException {
        //Calcular valor total de la compra
        int totalCompra = 0;
        int porcentaje = 100;
        boolean ciudadPpal = false;
        //valida si la ciudad de destino pertenece al area metropolitana de una ciudad principal para matisses
        String codCiudadPpal = webIntegratorBean.obtenerMunicipioPpal(destCityCode);
        //si el codigo obtenido es '00000', significa que la ciudad de destino no esta dentro de las ciudades
        //a las que se puedan realizar entregas
        if (codCiudadPpal != null && codCiudadPpal.equals("00000")) {
            throw new DestinationNotSupportedException();
        }
        if (destCityCode != null && codCiudadPpal != null) {
            ciudadPpal = true;
        }

        for (SalesWarehouseItemDTO itemDto : items) {
            for (Object[] row : precios) {
                String itemCode = (String) row[0];
                if (itemCode.equals(itemDto.getItemCode())) {
                    totalCompra += (Integer) row[1] * itemDto.getQuantity();
                    break;
                }
            }
        }
        log.log(Level.FINE, "El total de la compra es [{0}]", totalCompra);

        //Si la ciudad de envio es medellin, bogota o areas metropolitanas y el monto es mayor a 2 millones
        //Si la ciudad es diferente y el envio es mayor a 7 millones
        //Si la ciudad es diferente y el envio es mayor a 2 millones y menor a 7
        //Si la ciudad es diferente y el envio es menor a 2 millones
        //Si la ciudad de envio es medellin, bogota o areas metropolitanas y el monto es menor a 2 millones
        if ((ciudadPpal && totalCompra >= MONTO_VENTA_2) || (!ciudadPpal && totalCompra >= MONTO_VENTA_7)) {
            //envio gratuito
            porcentaje = 0;
        } else if (!ciudadPpal && totalCompra >= MONTO_VENTA_2) {
            //7% del monto total
            porcentaje = 7;
        } else if (totalCompra < MONTO_VENTA_300) {
            return VALOR_MINIMO_FLETE;
        } else {
            //10% del monto total
            porcentaje = 10;
        }
        return totalCompra * porcentaje / 100;
    }

    private void guardarTransportistaOrden(String idOrden, Integer idTransportista, Integer valor) {
        if (idOrden == null) {
            return;
        }
        TransportistaOrden entidad = new TransportistaOrden();
        try {
            entidad = transportistaOrdenFacade.find(Integer.parseInt(idOrden));
            if (entidad == null) {
                entidad = new TransportistaOrden();
                entidad.setIdOrdenPrestashop(Integer.parseInt(idOrden));
                entidad.setIdTransportista(idTransportista);
                entidad.setValor(valor);
                transportistaOrdenFacade.create(entidad);
            } else {
                entidad.setIdTransportista(idTransportista);
                entidad.setValor(valor);
                transportistaOrdenFacade.edit(entidad);
            }
        } catch (Exception e) {
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ShippingQuotationResultDTO quoteShipping(Object salesWarehouseDTO) {
        Map<Integer, Integer> shippingCostPerCarrier = new HashMap<>();
        ShippingQuotationResultDTO shippingTotal = new ShippingQuotationResultDTO();

        //Consulta si la orden ya fue facturada. De ser asi, devuelve el valor que se facturo y no vuelve a procesar la cotizacion
        Object[] orderShippingData = transportistaOrdenFacade.consultarOrdenEnviada(((SalesWarehouseDTO) salesWarehouseDTO).getPrestashopId());
        if (orderShippingData != null) {
            shippingTotal.setShippingCompany((String) orderShippingData[0]);
            shippingTotal.setTotal((Integer) orderShippingData[1]);
            return shippingTotal;
        }

        Integer descuento = webIntegratorBean.obtenerDescuento("00000", ((SalesWarehouseDTO) salesWarehouseDTO).getDestinationCityCode());
        if (descuento == 100) {
            log.log(Level.INFO, "La ciudad de destino tiene configurado un descuento de envios del 100% y por lo tanto no se calculara costo de envio");
            shippingTotal.setShippingCompany("1");
            shippingTotal.setTotal(0);
            return shippingTotal;
        }

        //Obtener almacen origen
        SalesWarehouseDTO dto = null;
        try {
            dto = getSalesWarehouseByProduct(salesWarehouseDTO);
        } catch (ItemOutOfStockException e) {
            log.log(Level.SEVERE, "No se puede cotizar el envio de un producto que no tiene saldo disponible [{0}]", e.getMessage());
            shippingTotal.setErrorMessage("No se puede cotizar el envio de un producto que no tiene saldo disponible [" + e.getMessage() + "]");
            return shippingTotal;
        }

        if (dto == null || dto.getDestinationCityCode() == null || dto.getDestinationCityCode().trim().isEmpty()) {
            log.log(Level.SEVERE, "No se especifico una ciudad de destino. No se puede proceder con la cotizacion del envio");
            return new ShippingQuotationResultDTO("No se especifico una ciudad de destino. No se puede proceder con la cotizacion del envio");
        }

        //Consulta los precios de los articulos para calcular el total de la compra
        List<String> itemCodes = new ArrayList<>();
        for (SalesWarehouseItemDTO itemDto : dto.getItems()) {
            itemCodes.add(itemDto.getItemCode());
        }
        List<Object[]> precios = inventoryItemFacade.getItemsPrices(itemCodes);
        if (precios == null) {
            shippingTotal.setShippingCompany(null);
            shippingTotal.setTotal(null);
            shippingTotal.setErrorMessage("No fue posible obtener los precios de venta actualizados, por lo que no es posible cotizar el envio");
            return shippingTotal;
        }
        log.log(Level.FINE, "Iniciando proceso de cotizacion de envio para productos");

        //validar monto completo de la orden, si supera un monto parametrizable no se cobra envio
        try {
            int matissesShippingCost = getMatissesShippingCost(dto.getDestinationCityCode(), dto.getItems(), precios);
            if (matissesShippingCost == 0) {
                log.log(Level.FINE, "Las politicas de envio de matisses cubren el envio gratuito de esta orden");
                shippingTotal.setShippingCompany("1");
                shippingTotal.setTotal(0);

                //Almacena el registro de transportistaxorden
                guardarTransportistaOrden(dto.getPrestashopId(), 1, 0);
                return shippingTotal;
            }
            shippingCostPerCarrier.put(1, matissesShippingCost);
        } catch (DestinationNotSupportedException e) {
            log.log(Level.SEVERE, "La ciudad recibida ({0}) no puede ser usada como ciudad de destino. ", dto.getDestinationCityCode());
            //La ciudad destino ingresada no es valida para envios
            shippingTotal.setShippingCompany(null);
            shippingTotal.setTotal(null);
            shippingTotal.setErrorMessage("Lo sentimos, no podemos realizar entregas a la ciudad que seleccionaste (" + dto.getDestinationCityCode() + ")");
            return shippingTotal;
        }

        //Consulta si existe un factor de descuento de envios con terceros para la ciudad de destino, 
        //que se aplica al valor final del envio
//        float factor = 1f;
//        FactorEntregaCiudad factorEntrega = null;
//        try {
//            factorEntrega = factorEntregaFacade.findBySourceAndDestination(itemDto.getSourceCityCode(), dto.getDestinationCityCode());
//        } catch (Exception e) {
//            log.log(Level.WARNING, "Ocurrio un error al consultar el factor de descuendo para entregas. ", e);
//        }
        //si matisses no cubre gratuitamente el envio, se consultan transportistas para cotizar y comparar
        Map<String, List<String[]>> productos = new HashMap<>();
        for (SalesWarehouseItemDTO itemDto : dto.getItems()) {
            float alto = 1;
            float ancho = 1;
            float largo = 1;
            float peso = 1;
            int precio = 0;
            try {
                ItemInventario entidad = inventoryItemFacade.find(itemDto.getItemCode());
                alto = entidad.getSHeight1().floatValue();
                ancho = entidad.getSWidth1().floatValue();
                largo = entidad.getSLength1().floatValue();
                peso = entidad.getSWeight1().floatValue();
                if (peso < 1) {
                    peso = 1f;
                }
                for (Object[] p : precios) {
                    if (p[0].equals(itemDto.getItemCode())) {
                        precio = (Integer) p[1];
                        break;
                    }
                }
                //precio = precioVentaItemFacade.findByItemCode(itemDto.getItemCode(), true);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Ocurrio un error al configurar las dimensiones del producto. ", e);
            }
            String ciudadOrigenCompleta = StringUtils.rightPad(itemDto.getSourceCityCode(), 8, "0");
            //1.alto, 2. ancho, 3. largo, 4. peso, 5. unidades, 6. valor
            String[] item = new String[]{Float.toString(alto), Float.toString(ancho), Float.toString(largo), Float.toString(peso),
                Integer.toString(itemDto.getQuantity()), Integer.toString(precio)};
            if (productos.containsKey(ciudadOrigenCompleta)) {
                productos.get(ciudadOrigenCompleta).add(item);
            } else {
                List<String[]> detalleItem = new ArrayList<>();
                detalleItem.add(item);
                productos.put(ciudadOrigenCompleta, detalleItem);
            }
        }
        for (TransportistaDTO transDto : webIntegratorBean.getTransportistas()) {
            try {
                String fullClassName = "co.matisses.carriers." + transDto.getCarrierName().toLowerCase() + "." + transDto.getClassName();
                Object result = Class.forName(fullClassName).getMethod(transDto.getQuotingMethod(), String.class, String.class, Map.class).invoke(null,
                        transDto.getWsdl(), StringUtils.rightPad(dto.getDestinationCityCode(), 8, "0"), productos);
                //Se redondea el total cotizado de envio al multiplo de 50 superior para evitar redondeo automatico en SAP
                result = (int) (Math.ceil((Integer) result / 50d) * 50);
                shippingCostPerCarrier.put(transDto.getCarrierId(), (Integer) result);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("ubl")) {
                    log.log(Level.SEVERE, "Ocurrio un error al invocar el servicio del transportista. ", e.getMessage());
                } else {
                    log.log(Level.SEVERE, "Ocurrio un error al invocar el servicio de cotizacion para " + transDto.getCarrierName(), e);
                }
            }
        }

        int valorMinimo = -1;
        int bestShipping = -1;
        //Elige entre todos los transportistas al mas economico
        for (Integer carrier : shippingCostPerCarrier.keySet()) {
            if (valorMinimo < 0 || valorMinimo > shippingCostPerCarrier.get(carrier)) {
                valorMinimo = shippingCostPerCarrier.get(carrier);
                bestShipping = carrier;
            }
        }
        shippingTotal.setShippingCompany(Integer.toString(bestShipping));
        shippingTotal.setTotal(shippingCostPerCarrier.get(bestShipping));

        guardarTransportistaOrden(dto.getPrestashopId(), bestShipping, shippingCostPerCarrier.get(bestShipping));
        return shippingTotal;

//
//        if (factorEntrega != null) {
//            factor = factorEntrega.getFactor().floatValue();
//            if (factor < 0 || factor > 1) {
//                log.log(Level.WARNING, "No se pudo asignar el factor para la ciudad [{0}] ya que su valor no esta en el rango aceptado (0 >= factor >= 1)",
//                        dto.getDestinationCityCode());
//                factor = 1;
//            }
//        }
//        for (SalesWarehouseItemDTO itemDto : dto.getItems()) {
//            log.log(Level.INFO, "Calculando costo de envio para [{3}] unidades de ref [{0}] desde [{1}] hacia [{2}]",
//                    new Object[]{itemDto.getItemCode(), itemDto.getSourceCityCode(), dto.getDestinationCityCode(), itemDto.getQuantity()});
//            if (dto.getDestinationCityCode().equals(itemDto.getSourceCityCode())) {
//                log.log(Level.INFO, "No se calculara el costo de envio ya que la ciudad de origen y destino son las mismas");
//                shippingTotal.setShippingCompany("1");
//                shippingTotal.setTotal(shippingTotal.getTotal());
//                continue;
//            }
        //validar descuento por ciudad destino
        //invocar servicio(s) de cotizacion con transportistas solo si el factor es mayor que cero
//            if (factor > 0) {
        //usar cliente para servicio de cotizacion de transporte
//                ShippingQuotationResultDTO shippingQuotationBeforeDiscount = consultarEnvioProducto(itemDto.getItemCode(), itemDto.getSourceCityCode(),
//                        dto.getDestinationCityCode(), itemDto.getQuantity());
//                shippingTotal.setShippingCompany(shippingQuotationBeforeDiscount.getShippingCompany());
//                shippingTotal.setTotal((int) (shippingQuotationBeforeDiscount.getTotal() * factor));
//                log.log(Level.INFO, "El costo del envio es de [{0}] antes de descuento. Se aplico un descuento de [{1}] y se sumaran [{2}] al total",
//                        new Object[]{shippingQuotationBeforeDiscount.getTotal(), factor, shippingQuotationBeforeDiscount.getTotal() * factor});
//            } else {
//                log.log(Level.INFO, "No se consultara el valor del envio ya que la ruta tiene configurado un factor de cubrimiento del 100% ");
//                shippingTotal.setShippingCompany("1");
//                shippingTotal.setTotal(0);
//            }
//        }
//        log.log(Level.INFO, "El total de envio es [{0}]", shippingTotal);
//        return shippingTotal;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Integer requestCarrierPickup(Object salesWarehouseDTO) {
        return null;
    }

//    private ShippingQuotationResultDTO consultarEnvioProducto(String itemCode, String cityFrom, String cityTo, Integer quantity) {
//        ShippingQuotationResultDTO quotationDto = new ShippingQuotationResultDTO();
//        ShippingPriceDTO shippingPrice = shippingPricesCache.getFromCache(itemCode, cityFrom, cityTo);
//        if (shippingPrice == null) {
//            //TODO: implementar consulta dinamica con diferentes proveedores, habilitandolos por base de datos
//            CoordinadoraWSMain coordinadoraMain = new CoordinadoraWSMain();
//            coordinadoraMain.cotizarEnvio(cityTo, articulos)
//        }
//        quotationDto.setTotal(shippingPrice.getPrice());
//        quotationDto.setShippingCompany(shippingPrice.getShippingCompany());
//        return quotationDto;
//    }
}
