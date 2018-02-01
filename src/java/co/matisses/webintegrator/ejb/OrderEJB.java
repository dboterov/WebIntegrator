package co.matisses.webintegrator.ejb;

import co.matisses.b1ws.client.creditnotes.CreditNotesServiceConnector;
import co.matisses.b1ws.client.goodsissue.GoodsIssueDTO;
import co.matisses.b1ws.client.goodsissue.GoodsIssueDetailDTO;
import co.matisses.b1ws.client.goodsissue.GoodsIssueLocationsDTO;
import co.matisses.b1ws.client.goodsissue.GoodsIssueServiceConnector;
import co.matisses.b1ws.client.goodsissue.GoodsIssueServiceException;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptDTO;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptDetailDTO;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptLocationsDTO;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptServiceConnector;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptServiceException;
import co.matisses.b1ws.client.invoices.InvoiceServiceException;
import co.matisses.b1ws.client.invoices.InvoicesServiceConnector;
import co.matisses.b1ws.client.journalentries.JournalEntriesServiceConnector;
import co.matisses.b1ws.client.journalentries.JournalEntryServiceException;
import co.matisses.b1ws.client.orders.OrderServiceException;
import co.matisses.b1ws.client.orders.OrdersServiceConnector;
import co.matisses.b1ws.client.payments.IncomingPaymentServiceConnector;
import co.matisses.b1ws.client.payments.IncomingPaymentServiceException;
import co.matisses.b1ws.dto.ConstantTypes;
import co.matisses.b1ws.dto.CreditCardPaymentDTO;
import co.matisses.b1ws.dto.JournalEntryDTO;
import co.matisses.b1ws.dto.JournalEntryLineDTO;
import co.matisses.b1ws.dto.PaymentDTO;
import co.matisses.b1ws.dto.SalesDocumentDTO;
import co.matisses.b1ws.dto.SalesDocumentLineBinAllocationDTO;
import co.matisses.b1ws.dto.SalesDocumentLineDTO;
import co.matisses.b1ws.dto.SalesEmployeeDTO;
import co.matisses.persistence.sap.entity.BaruEstadoFactura;
import co.matisses.persistence.sap.entity.CotizacionSAP;
import co.matisses.persistence.sap.entity.DireccionSocioDeNegocios;
import co.matisses.persistence.sap.entity.DocumentosExcluidos;
import co.matisses.persistence.sap.entity.FacturaSAP;
import co.matisses.persistence.sap.entity.SocioDeNegocios;
import co.matisses.persistence.sap.facade.AlmacenFacade;
import co.matisses.persistence.sap.facade.BaruEstadoFacturaFacade;
import co.matisses.persistence.sap.facade.CotizacionSAPFacade;
import co.matisses.persistence.sap.facade.DireccionSocioDeNegociosFacade;
import co.matisses.persistence.sap.facade.DocumentosExcluidosFacade;
import co.matisses.persistence.sap.facade.FacturaSAPFacade;
import co.matisses.persistence.sap.facade.ItemInventarioFacade;
import co.matisses.persistence.sap.facade.SaldoUbicacionFacade;
import co.matisses.persistence.sap.facade.SocioDeNegociosFacade;
import co.matisses.persistence.web.entity.CotizacionWeb;
import co.matisses.persistence.web.entity.DetalleCotizacionWeb;
import co.matisses.persistence.web.entity.FranquiciaTarjeta;
import co.matisses.persistence.web.facade.CotizacionWebFacade;
import co.matisses.persistence.web.facade.DetalleCotizacionWebFacade;
import co.matisses.persistence.web.facade.FranquiciaTarjetaFacade;
import co.matisses.webintegrator.dto.AdditionalCostsDTO;
import co.matisses.webintegrator.dto.CustomerDTO;
import co.matisses.webintegrator.dto.CustomerOrdersDTO;
import co.matisses.webintegrator.dto.GenericResponse;
import co.matisses.webintegrator.dto.IncomingPaymentDTO;
import co.matisses.webintegrator.dto.InformacionAlmacenDTO;
import co.matisses.webintegrator.dto.InvoiceDetailDTO;
import co.matisses.webintegrator.dto.InvoiceHeaderDTO;
import co.matisses.webintegrator.dto.MailMessageDTO;
import co.matisses.webintegrator.dto.OrderDTO;
import co.matisses.webintegrator.dto.OrderDetailDTO;
import co.matisses.webintegrator.dto.OrderParametersDTO;
import co.matisses.webintegrator.dto.OrderTrackingDetailDTO;
import co.matisses.webintegrator.dto.OrderTrackingInfoDTO;
import co.matisses.webintegrator.dto.SalesWarehouseDTO;
import co.matisses.webintegrator.dto.SalesWarehouseItemDTO;
import co.matisses.webintegrator.dto.SesionSAPB1WSDTO;
import co.matisses.webintegrator.dto.ShippingQuotationResultDTO;
import co.matisses.webintegrator.mbean.WebIntegratorMBean;
import co.matisses.webintegrator.mbean.email.SendHTMLEmailMBean;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.NoResultException;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author dbotero
 */
@Stateless
public class OrderEJB {

    private static final Logger log = Logger.getLogger(OrderEJB.class.getSimpleName());
    private static final String PREFIJO_FACTURA_WEB = "WEB";
    private static final String NRO_FACTURA_WEB = "7";
    private String codCiudadDestino;
    @EJB
    private CotizacionSAPFacade cotizacionSAPFacade;
    @EJB
    private CotizacionWebFacade cotizacionFacade;
    @EJB
    private DireccionSocioDeNegociosFacade direccionClienteFacade;
//    @EJB
//    private PagosRecibidosFacade pagosFacade;
    @EJB
    private DetalleCotizacionWebFacade detalleCotizacionFacade;
    @EJB
    private ItemInventarioFacade itemFacade;
//    @EJB
//    private PrecioVentaItemFacade itemPriceFacade;
//    @EJB
//    private SaldoItemInventarioFacade saldoFacade;
    @EJB
    private SocioDeNegociosFacade socioDeNegociosFacade;
    @EJB
    private FacturaSAPFacade facturaSAPFacade;
    @EJB
    private FranquiciaTarjetaFacade franquiciaFacade;
    @EJB
    private SaldoUbicacionFacade saldoUbicacionFacade;
//    @EJB
//    private DetalleUbicacionCotizacionFacade detalleUbicacionCotizacionFacade;
    @EJB
    private BaruEstadoFacturaFacade estadoFacturaFacade;
    @EJB
    private InventoryItemEJB inventoryItemEJB;
    @EJB
    private AlmacenFacade almacenFacade;
    @EJB
    private DocumentosExcluidosFacade documentosExcluidosFacade;
    @Inject
    private WebIntegratorMBean webIntegratorBean;
    @Inject
    private SAPB1WSBean sapB1WSBean;
    @Inject
    private SendHTMLEmailMBean emailSender;

    public OrderEJB() {
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse createNewOrder(Object order) {
        GenericResponse response = new GenericResponse();
        OrderDTO prestashopOrder = mergeOrderRows((OrderDTO) order);
        OrderDTO validacion = webIntegratorBean.getOrdenesPendientes().get(prestashopOrder.getHeader().getPrestashopOrderId());
        //Valida que el idPrestashop no exista.
        if (validacion != null) {
            response.setCode("0201901");
            response.setDetail("La orden #" + prestashopOrder.getHeader().getPrestashopOrderId() + " ya existe y no ha sido facturada. No se puede crear una nueva orden con un ID existente.");
            return response;
        }
        //Valida que la orden con el idPrestashop recibido no se haya facturado.
        SalesDocumentDTO datosFactura = consultarDatosFactura(prestashopOrder.getHeader().getPrestashopOrderId());
        if (datosFactura != null) {
            log.log(Level.WARNING, "La orden #{0} ya fue facturada. No se puede crear una orden con un ID existente.", prestashopOrder.getHeader().getPrestashopOrderId());
            response.setCode("0201901");
            response.setDetail("La orden #" + prestashopOrder.getHeader().getPrestashopOrderId() + " ya fue facturada. No se puede crear una orden con un ID existente.");
            return response;
        }
        //Valida que el cliente exista
        try {
            SocioDeNegocios clienteSAP;
            String nit = prestashopOrder.getHeader().getCustomerId();
            if (nit != null && !nit.endsWith("CL")) {
                nit = nit.concat("CL");
                prestashopOrder.getHeader().setCustomerId(nit);
            }
            clienteSAP = socioDeNegociosFacade.find(nit);
            if (clienteSAP == null) {
                response.setCode("0201902");
                response.setDetail("El cliente ingesado en la orden no se encuentra registrado [" + nit + "]");
                return response;
            } else {
                //configura la ciudad de destino de la orden
                codCiudadDestino = clienteSAP.getuBpcoCs();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al consultar el cliente. ", e.getMessage());
            response.setCode("0201902");
            response.setDetail("El cliente ingresado en la orden no se encuentra registrado [" + prestashopOrder.getHeader().getCustomerId() + "]");
            return response;
        }

        webIntegratorBean.getOrdenesPendientes().put(prestashopOrder.getHeader().getPrestashopOrderId(), prestashopOrder);
        response.setCode("0201001");
        response.setDetail(prestashopOrder.getHeader().getPrestashopOrderId());
        return response;
    }

    private OrderDTO mergeOrderRows(OrderDTO order) {
        HashMap<String, Integer> refs = new HashMap<>();
        List<OrderDetailDTO> newDetail = new ArrayList<>();
        for (OrderDetailDTO detail : order.getDetail()) {
            if (refs.containsKey(detail.getItemCode())) {
                OrderDetailDTO dto = newDetail.get(refs.get(detail.getItemCode()));
                dto.setQuantity(dto.getQuantity() + detail.getQuantity());
            } else {
                refs.put(detail.getItemCode(), newDetail.size());
                newDetail.add(detail);
            }
        }
        order.setDetail(newDetail);
        return order;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse createInvoice(Object order) {
        GenericResponse response = new GenericResponse();
        OrderDTO prestashopOrder = (OrderDTO) order;
        log.log(Level.INFO, "Generando factura para la orden #[{0}]", prestashopOrder.getHeader().getPrestashopOrderId());
        OrderDTO orden = webIntegratorBean.getOrdenesPendientes().remove(prestashopOrder.getHeader().getPrestashopOrderId());
        if (orden == null) {
            response.setCode("0202901");
            response.setDetail("No encontro la orden para facturar.");
            return response;
        }

        SalesDocumentDTO docFactura = new SalesDocumentDTO();
        //Campos no usados
        //docFactura.setBinAbsEntry(null);
        //docFactura.setCreditNoteType(null);
        //docFactura.setDocEntry(null);
        //docFactura.setDocumentLines(null);
        //docFactura.setPosShiftId(null);
        //docFactura.setSalesEmployees(null);

        docFactura.setCardCode(orden.getHeader().getCustomerId());
        docFactura.setComments("Factura creada desde sitio web usando B1WS. ".concat(orden.getHeader().getComments()));
        docFactura.setShippingStatus("P");
        docFactura.setSource("W");
        docFactura.setPaymentGroupCode("17");
        docFactura.setRefDocnum(prestashopOrder.getHeader().getSapOrderId());
        docFactura.setPrestashopOrderID(prestashopOrder.getHeader().getPrestashopOrderId());

        Object[] datosFactura = consultarDatosFacturacionAlmacen();
        docFactura.setSeriesCode((String) datosFactura[0]);
        docFactura.setSalesCostingCode((String) datosFactura[2]);
        docFactura.setLogisticsCostingCode((String) datosFactura[3]);
        docFactura.setRouteCostingCode((String) datosFactura[4]);
        docFactura.setWuid((String) datosFactura[6]);
        docFactura.setProjectCode((String) datosFactura[5]);

        SalesEmployeeDTO salesEmp = new SalesEmployeeDTO();
        salesEmp.setSlpCode("98");
        salesEmp.setName("Vendedor Web");
        docFactura.addSalesEmployee(salesEmp);

        //Ejecuta la asignacion de ubicaciones por referencia
        int lineNum = 0;
        List<OrderDetailDTO> detalleOrden = orden.getDetail();
        for (OrderDetailDTO detalle : detalleOrden) {
            SalesDocumentLineDTO line = new SalesDocumentLineDTO();
            line.setItemCode(detalle.getItemCode());
            line.setLineNum(lineNum++);
            line.setPrice(itemFacade.getItemPrice(detalle.getItemCode()).doubleValue());
            //Consulta saldo en ubicaciones por referencia, ordenado de mayor a menor
            List<Object[]> saldosUbicacion = saldoUbicacionFacade.buscarPorReferencia(detalle.getItemCode());
            //Asigna las ubicaciones por cantidad
            int cantidadPendiente = detalle.getQuantity();
            log.log(Level.INFO, "Verificando ubicaciones para la referencia [{0}]", detalle.getItemCode());
            //Valida que la cantidad total en saldo sea suficiente para cubrir la cantidad solicitada
            int cantidadTotal = 0;
            for (Object[] row : saldosUbicacion) {
                cantidadTotal += (Integer) row[4];
            }
            if (cantidadTotal < cantidadPendiente) {
                response.setCode("0202901");
                response.setDetail("No hay saldo suficiente de la referencia " + detalle.getItemCode() + ". Cantidad necesaria: " + detalle.getQuantity() + ", cantidad disponible: " + cantidadTotal);
                return response;
            }
            for (Object[] row : saldosUbicacion) {
                String whsCode = (String) row[0];
                //Integer saldoAlmacen = (Integer) row[1];
                Integer binAbs = (Integer) row[2];
                String binCode = (String) row[3];
                Integer saldoUbicacion = (Integer) row[4];
                if (line.getWhsCode() == null) {
                    line.setWhsCode(whsCode);
                } else if (!line.getWhsCode().equals(whsCode)) {
                    //Si el almacen cambia, significa que el saldo en las ubicaciones del primer almacen no fue suficiente y se debe crear una nueva linea
                    docFactura.addLine(line);
                    line = new SalesDocumentLineDTO();
                    line.setItemCode(detalle.getItemCode());
                    line.setLineNum(lineNum++);
                    line.setPrice(null);
                    line.setWhsCode(whsCode);
                }
                if (line.getQuantity() == null) {
                    line.setQuantity(Math.min(Math.min(saldoUbicacion, cantidadPendiente), detalle.getQuantity()));
                } else {
                    line.setQuantity(line.getQuantity() + Math.min(saldoUbicacion, cantidadPendiente));
                }
                SalesDocumentLineBinAllocationDTO bin = new SalesDocumentLineBinAllocationDTO();
                bin.setBinAbsEntry(binAbs);
                bin.setWhsCode(whsCode);
                if (cantidadPendiente <= saldoUbicacion) {
                    log.log(Level.INFO, "La cantidad [{0}] en la ubicacion [{1}] satisface la cantidad pendiente [{2}]",
                            new Object[]{saldoUbicacion, binCode, cantidadPendiente});
                    bin.setQuantity(cantidadPendiente);
                    cantidadPendiente = 0;
                } else {
                    log.log(Level.INFO, "La cantidad [{0}] en la ubicacion [{1}] NO satisface la cantidad pendiente [{2}]. Faltan [{3}]",
                            new Object[]{saldoUbicacion, binCode, cantidadPendiente, cantidadPendiente - saldoUbicacion});
                    bin.setQuantity(saldoUbicacion);
                    cantidadPendiente -= saldoUbicacion;
                }
                line.addBinAllocation(bin);
                if (cantidadPendiente == 0) {
                    break;
                }
            }
            docFactura.addLine(line);
        }//Finaliza la asignacion de ubicaciones por referencia
        //Crea la factura
        String invoiceDocNum = null;
        Long docEntryFactura = null;
        FacturaSAP facturaSAP = null;
        SesionSAPB1WSDTO sesionSap = webIntegratorBean.obtenerSesionDisponible();
        if (sesionSap == null || sesionSap.getIdSesionSAP() == null) {
            sendErrorEmailNotification("crear factura web", "No se pudo obtener una sesion de B1WS luego de esperar 20 segundos. ");
            log.log(Level.SEVERE, "No fue posible configurar una conexion con el servidor de facturacion. Por favor intente de nuevo. ");
            response.setCode("0202901");
            response.setDetail("No fue posible configurar una conexion con el servidor de facturacion. Por favor intente de nuevo. ");
            return response;
        }
        InvoicesServiceConnector isc = sapB1WSBean.getInvoicesServiceConnectorInstance(sesionSap.getIdSesionSAP());
        try {
            docEntryFactura = isc.createInvoice(docFactura);
            //TODO: enviar mensaje para ejecucion asincrona de creacion de documentos adicionales
//            webIntegratorBean.sendMessage();
            facturaSAP = facturaSAPFacade.findNoTransaction(docEntryFactura.intValue());
            invoiceDocNum = Integer.toString(facturaSAP.getDocNum());
            log.log(Level.INFO, "Se creo con exito la factura {0}", invoiceDocNum);
            docFactura = consultarDatosFactura(prestashopOrder.getHeader().getPrestashopOrderId());
            //TODO: enviar correo de notificacion de nueva factura
        } catch (InvoiceServiceException e) {
            webIntegratorBean.devolverSesion(sesionSap);
            log.log(Level.SEVERE, "Ocurrio un error al crear la factura. " + e.getMessage(), e);
            response.setCode("0202901");
            response.setDetail("Ocurrio un error al crear la factura. " + e.getMessage());
            return response;
        }
        //Crea asientos de ajuste para mercancia en consignacion
        try {
            Long docEntryAsiento = createJournalEntry(docEntryFactura, sesionSap);
            if (docEntryAsiento > 0) {
                log.log(Level.INFO, "Se creo el asiento contable con ID {0}", docEntryAsiento);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al crear el asiento de ajuste para mcia en consignacion. ", e);
            sendErrorEmailNotification("creacion asiento de ajuste", e.getMessage());
        }
        //Crea la orden de venta
        try {
            Long docEntryOrden = createSalesOrder(orden, facturaSAP, docFactura, sesionSap);
            log.log(Level.INFO, "Se creo la orden de venta con docEntry={0}", docEntryOrden);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al crear la orden de venta. ", e);
            sendErrorEmailNotification("creacion orden de venta", e.getMessage());
        }
        //Crea la entrada de mercancia
        try {
            Long docEntryEntrada = createGoodsReceipt(facturaSAP, sesionSap, docFactura);
            log.log(Level.INFO, "Se creo la entrada de mercancia de clientes docEntry={0}", docEntryEntrada);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al crear la entrada de mercancia de clientes. ", e);
            sendErrorEmailNotification("creacion entrada de mercancia", e.getMessage());
        }
        webIntegratorBean.devolverSesion(sesionSap);
        response.setCode("0202001");
        response.setDetail(((String) datosFactura[1]).concat(invoiceDocNum.substring(invoiceDocNum.indexOf("0"))));
        return response;
    }

    private Long createGoodsReceipt(FacturaSAP facturaSAP, SesionSAPB1WSDTO sesionSAPDTO, SalesDocumentDTO enc) throws GoodsReceiptServiceException {
        GoodsReceiptDTO entrada = new GoodsReceiptDTO();
        entrada.setComments("Entrada de mcia para FV " + facturaSAP.getDocNum() + " y cliente " + facturaSAP.getCardCode());
        entrada.setInvoiceNumber(Integer.toString(facturaSAP.getDocNum()));
        entrada.setJournalMemo("Goods Receipt");
        entrada.setSeries(69L); //TODO: parametrizar numero de serie de entradas de mcia de clientes
        entrada.setOrigen("W");
        for (SalesDocumentLineDTO detalle : enc.getDocumentLines()) {
            InformacionAlmacenDTO infoAlmacenVenta = webIntegratorBean.getInfoAlmacen(detalle.getWhsCode());
            InformacionAlmacenDTO infoAlmacenCliente = webIntegratorBean.getInfoAlmacen(infoAlmacenVenta.getAlmacenClientes());

            GoodsReceiptLocationsDTO ubEntrada = new GoodsReceiptLocationsDTO();
            ubEntrada.setBinAbs(infoAlmacenCliente.getIdUbicacionTM());
            ubEntrada.setQuantity(detalle.getQuantity());

            GoodsReceiptDetailDTO detEntrada = new GoodsReceiptDetailDTO();
            detEntrada.addLocation(ubEntrada);
            detEntrada.setAccountCode("91051001"); //TODO: parametrizar cuenta de mcia de clientes
            detEntrada.setItemCode(detalle.getItemCode());
            detEntrada.setLineNum(detalle.getLineNum().longValue());
            detEntrada.setQuantity(detalle.getQuantity());
            detEntrada.setWhsCode(infoAlmacenVenta.getAlmacenClientes());//El whsCode se consulta ya que para productos en consignacion se manejan almacenes especiales
            detEntrada.setPrice(detalle.getPrice());
            entrada.addDetail(detEntrada);
        }
        GoodsReceiptServiceConnector grsc = sapB1WSBean.getGoodsReceiptServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
        return grsc.createDocument(entrada);
    }

    private Long createSalesOrder(OrderDTO venta, FacturaSAP facturaSAP, SalesDocumentDTO enc, SesionSAPB1WSDTO sesionSAPDTO) throws OrderServiceException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 1);
        co.matisses.b1ws.dto.OrderDTO orderDto = new co.matisses.b1ws.dto.OrderDTO();
        orderDto.setCardCode(venta.getHeader().getCustomerId());
        orderDto.setComments(venta.getHeader().getComments());
        orderDto.setDocDate(new Date());
        orderDto.setDocDueDate(cal.getTime());
        orderDto.setInvoiceNumber(Integer.toString(facturaSAP.getDocNum()));
        orderDto.setOrigen("W");
        orderDto.setSalesPersonCode(facturaSAP.getSlpCode().longValue());
        orderDto.setSeries(13L); //TODO: parametrizar codigo de numero de serie de ordenes de venta
        for (SalesDocumentLineDTO detalle : enc.getDocumentLines()) {
            InformacionAlmacenDTO infoAlmacenVenta = webIntegratorBean.getInfoAlmacen(detalle.getWhsCode());
            co.matisses.b1ws.dto.OrderDetailDTO detailDto = new co.matisses.b1ws.dto.OrderDetailDTO();
            detailDto.setEstado("P");
            detailDto.setItemCode(detalle.getItemCode());
            detailDto.setLineNum(detalle.getLineNum().longValue());
            detailDto.setQuantity(detalle.getQuantity().doubleValue());
            detailDto.setWarehouseCode(infoAlmacenVenta.getAlmacenClientes());
            orderDto.addLine(detailDto);
        }

        OrdersServiceConnector osc = sapB1WSBean.getOrderServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
        return osc.createOrder(orderDto);
    }

    private Long createJournalEntry(Long docEntryFactura, SesionSAPB1WSDTO sesionSAPDTO) throws JournalEntryServiceException {
        //Si hay productos en consignacion, realiza el asiento contable correspondiente
        List<Object[]> asiento = facturaSAPFacade.consultarDatosAsientoConsignacionFactura(docEntryFactura);
        if (!asiento.isEmpty()) {
            JournalEntryDTO journalEntryHeader = null;
            for (Object[] row : asiento) {
                if (journalEntryHeader == null) {
                    journalEntryHeader = new JournalEntryDTO();
                    journalEntryHeader.setDueDate((Date) row[0]);
                    journalEntryHeader.setTaxDate((Date) row[0]);
                    journalEntryHeader.setRefDate((Date) row[0]);
                    journalEntryHeader.setMemo((String) row[1]);
                    journalEntryHeader.setRef1((String) row[2]);
                    journalEntryHeader.setRef2((String) row[3]);
                    journalEntryHeader.setRef3((String) row[4]);
                    journalEntryHeader.setTransactionCode((String) row[5]);
                }

                JournalEntryLineDTO line = new JournalEntryLineDTO();
                line.setRef1((String) row[2]);
                line.setRef2((String) row[3]);

                line.setLineId(((BigInteger) row[6]).longValue());
                line.setShortName((String) row[7]);
                line.setLineMemo((String) row[8]);
                line.setOcrCode2((String) row[9]);
                line.setProject((String) row[10]);
                line.setInfoCo01((String) row[11]);
                line.setDebit(((Integer) row[12]).doubleValue());
                line.setCredit(((Integer) row[13]).doubleValue());

                journalEntryHeader.addLine(line);
            }
            JournalEntriesServiceConnector jesc = sapB1WSBean.getJournalEntriesServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
            return jesc.createJournalEntry(journalEntryHeader);
        } else {
            return 0L;
        }
    }

    private Object[] consultarDatosFacturacionAlmacen() {
        //consultar datos configurados para el detalle de la factura
        // 0. Cod Ciudad
        // 1. Codigo serie numeracion
        // 2. Nombre serie numeracion
        // 3. Codigo ventas
        // 4. Codigo logistica
        // 5. Codigo ruta
        // 6. Codigo proyecto
        // 7. WUID
        // 8. Serie recibo
        // 9. Resolucion fact. desde
        //10. Resolucion fact. hasta
        //11. Resolucion fact. numero y fecha (separados por "de")
        //12. Resolucion fact. prefijo
        //13. ID ubicacion COMPLEMENTOS para el almacen
        return almacenFacade.cargarDatosFacturaWeb();
    }

    private SalesDocumentDTO consultarDatosFactura(String prestashopOrderId) {
        List<Object[]> datos = facturaSAPFacade.findByPrestashopOrderId(prestashopOrderId);
        SalesDocumentDTO dto = new SalesDocumentDTO();
        if (datos == null || datos.isEmpty()) {
            log.log(Level.INFO, "No se encontraron facturas para la orden prestashop #{0}. ", prestashopOrderId);
            return null;
        }
        dto.setDocDate((Date) datos.get(0)[0]);
        dto.setDocEntry(((Integer) datos.get(0)[1]).longValue());
        dto.setRefDocnum((String) datos.get(0)[33]);
        dto.setCardCode((String) datos.get(0)[3]);
        dto.setComments("Anulacion generada por 360 para la factura " + (String) datos.get(0)[33]);
        dto.setPaymentGroupCode(((Short) datos.get(0)[5]).toString());
        dto.setSource("W");
        dto.setSeriesCode(((Short) datos.get(0)[31]).toString());
        dto.setWuid((String) datos.get(0)[16]);
        dto.setPosShiftId(((Integer) datos.get(0)[17]).longValue());
        dto.setCreditNoteType("A");

        //Agrega los vendedores a la devolucion
        if (datos.get(0)[6] != null) {
            SalesEmployeeDTO salesEmp = new SalesEmployeeDTO();
            salesEmp.setName((String) datos.get(0)[6]);
            dto.addSalesEmployee(salesEmp);
            if (datos.get(0)[7] != null) {
                SalesEmployeeDTO salesEmp2 = new SalesEmployeeDTO();
                salesEmp2.setName((String) datos.get(0)[7]);
                dto.addSalesEmployee(salesEmp2);
                if (datos.get(0)[8] != null) {
                    SalesEmployeeDTO salesEmp3 = new SalesEmployeeDTO();
                    salesEmp3.setName((String) datos.get(0)[8]);
                    dto.addSalesEmployee(salesEmp3);
                    if (datos.get(0)[9] != null) {
                        SalesEmployeeDTO salesEmp4 = new SalesEmployeeDTO();
                        salesEmp4.setName((String) datos.get(0)[9]);
                        dto.addSalesEmployee(salesEmp4);
                        if (datos.get(0)[10] != null) {
                            SalesEmployeeDTO salesEmp5 = new SalesEmployeeDTO();
                            salesEmp5.setName((String) datos.get(0)[10]);
                            dto.addSalesEmployee(salesEmp5);
                        }
                    }
                }
            }
        }

        List<SalesDocumentLineDTO> detDtos = new ArrayList<>();
        for (Object[] cols : datos) {
            dto.setShippingStatus((String) cols[27]);
            dto.setSalesCostingCode((String) cols[23]);
            dto.setLogisticsCostingCode((String) cols[24]);
            dto.setRouteCostingCode((String) cols[25]);

            SalesDocumentLineBinAllocationDTO binAllocDto = new SalesDocumentLineBinAllocationDTO();
            binAllocDto.setBinAbsEntry((Integer) cols[28]);
            binAllocDto.setQuantity((Integer) cols[30]);
            binAllocDto.setWhsCode((String) cols[22]);

            SalesDocumentLineDTO lineDto = new SalesDocumentLineDTO((String) cols[20], binAllocDto.getWhsCode(), ((Integer) cols[32]).doubleValue());
            int pos = detDtos.indexOf(lineDto);
            if (pos >= 0) {
                lineDto = detDtos.get(pos);
                lineDto.addBinAllocation(binAllocDto);
                lineDto.setQuantity(lineDto.getQuantity() + ((Integer) cols[30]));
                detDtos.set(pos, lineDto);
            } else {
                lineDto.setQuantity(((Integer) cols[30]));
                lineDto.addBinAllocation(binAllocDto);
                lineDto.setLineNum((Integer) cols[19]);
                detDtos.add(lineDto);
            }
        }
        dto.setDocumentLines(detDtos);
        return dto;
    }

    private void createJournalEntryCreditNote(SesionSAPB1WSDTO sesionSAPDTO, Long docEntryNotaCredito) {
        List<Object[]> asiento = facturaSAPFacade.consultarDatosAsientoConsignacionNotaCredito(docEntryNotaCredito);
        if (!asiento.isEmpty()) {
            log.log(Level.INFO, "Creando asiento en el diario para la anulacion de mercancia en consignacion...");
            JournalEntryDTO journalEntryHeader = null;
            for (Object[] row : asiento) {
                if (journalEntryHeader == null) {
                    journalEntryHeader = new JournalEntryDTO();
                    journalEntryHeader.setRefDate((Date) row[0]);
                    journalEntryHeader.setDueDate((Date) row[1]);
                    journalEntryHeader.setTaxDate((Date) row[2]);

                    journalEntryHeader.setMemo((String) row[3]);
                    journalEntryHeader.setRef1((String) row[4]);
                    journalEntryHeader.setRef2((String) row[5]);
                    journalEntryHeader.setRef3((String) row[6]);
                    journalEntryHeader.setTransactionCode((String) row[7]);
                }

                JournalEntryLineDTO line = new JournalEntryLineDTO();
                line.setRef1((String) row[4]);
                line.setRef2((String) row[5]);
                line.setLineId(((BigInteger) row[8]).longValue());
                line.setShortName((String) row[9]);
                line.setLineMemo((String) row[10]);
                line.setOcrCode2((String) row[11]);
                line.setProject((String) row[12]);
                line.setInfoCo01((String) row[13]);
                line.setCredit(((Integer) row[14]).doubleValue());
                line.setDebit(((Integer) row[15]).doubleValue());

                journalEntryHeader.addLine(line);
            }
            try {
                JournalEntriesServiceConnector jesc = sapB1WSBean.getJournalEntriesServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
                Long docEntryAsiento = jesc.createJournalEntry(journalEntryHeader);
                log.log(Level.INFO, "Se creo el asiento contable con ID {0}", docEntryAsiento);
            } catch (Exception e) {
                //TODO: notificar error por correo
                log.log(Level.SEVERE, "No se pudo crear el asiento contable. ", e);
            }
        }
    }

    private boolean cancelIncomingPayment(String nroFactura, SesionSAPB1WSDTO sesionSAPDTO) throws Exception {
        log.log(Level.INFO, "Cancelando recibo de caja para la factura {0}", nroFactura);
        Long docEntryRC = facturaSAPFacade.consultarDocEntryReciboCaja(nroFactura);
        if (docEntryRC == null) {
            return false;
        }
        IncomingPaymentServiceConnector ipsc = sapB1WSBean.getIncomingPaymentServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
        ipsc.cancelPayment(docEntryRC);
        return true;
    }

    private boolean needsGoodsIssue(SalesDocumentDTO orderDto) {
        return !(orderDto == null || orderDto.getShippingStatus() == null || orderDto.getShippingStatus().trim().isEmpty() || !orderDto.getShippingStatus().equals("P"));
    }

    private void createGoodsIssue(SesionSAPB1WSDTO sesionSAPDTO, SalesDocumentDTO enc, String nroFactura, Long docEntryNC) throws GoodsIssueServiceException {
        GoodsIssueDTO document = new GoodsIssueDTO();
        document.setComments("Doc. creado con B1WS Segun Nota Credito #" + docEntryNC + " (DocEntry) para Factura #" + nroFactura);
        document.setJournalMemo("Salida de mercancia de clientes");
        document.setSeries("26"); //TODO: parametrizar numero de serie de salia de mercancias por inventario
        document.setGroupNum(String.valueOf(-1L)); //Ultimo precio de compra
        document.setInvoiceNumber(nroFactura);
        for (SalesDocumentLineDTO lineDto : enc.getDocumentLines()) {
            InformacionAlmacenDTO infoAlmacenVenta = webIntegratorBean.getInfoAlmacen(lineDto.getWhsCode());
            InformacionAlmacenDTO infoAlmacenCliente = webIntegratorBean.getInfoAlmacen(infoAlmacenVenta.getAlmacenClientes());
            GoodsIssueDetailDTO det = new GoodsIssueDetailDTO();
            for (SalesDocumentLineBinAllocationDTO binDto : lineDto.getBinAllocations()) {
                GoodsIssueLocationsDTO loc = new GoodsIssueLocationsDTO();
                loc.setBinAbs(infoAlmacenCliente.getIdUbicacionTM().toString());
                loc.setQuantity(binDto.getQuantity().toString());
                det.addLocation(loc);
            }
            det.setAccountCode("91051001"); //TODO: parametrizar nro de cuenta de mercancia de clientes
            det.setItemCode(lineDto.getItemCode());
            det.setLineNum(lineDto.getLineNum().toString());
            det.setQuantity(lineDto.getQuantity().toString());
            det.setWhsCode(infoAlmacenVenta.getAlmacenClientes());
            document.addDetail(det);
        }

        GoodsIssueServiceConnector gisc = sapB1WSBean.getGoodsIssueServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
        Long docEntry = gisc.createDocument(document);
        log.log(Level.INFO, "Se creo la salida de mercancia de clientes con docEntry={0}", docEntry);
    }

    private void cancelSalesOrder(String nroFactura, SesionSAPB1WSDTO sesionSAPDTO) {
        OrdersServiceConnector osc = sapB1WSBean.getOrderServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
        try {
            Long docEntryOrden = facturaSAPFacade.consultarDocEntryOrden(nroFactura);
            osc.cancelOrder(docEntryOrden);
            log.log(Level.INFO, "Se cancelo con exito la orden de venta (docEntry) {0}", docEntryOrden);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al consultar la orden de venta. ", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse voidInvoice(Object order) {
        GenericResponse response = new GenericResponse();
        OrderDTO prestashopOrder = (OrderDTO) order;
        log.log(Level.INFO, "Anulando factura para orden #[{0}]", prestashopOrder.getHeader().getPrestashopOrderId());
        //Consultar la informacion de la factura y llenar el documento completo
        SesionSAPB1WSDTO sesionSAPDTO = webIntegratorBean.obtenerSesionDisponible();
        try {
            SalesDocumentDTO datosFactura = consultarDatosFactura(prestashopOrder.getHeader().getPrestashopOrderId());
            if (datosFactura == null) {
                webIntegratorBean.devolverSesion(sesionSAPDTO);
                log.log(Level.WARNING, "No se encontraron facturas para la orden con codigo {0}", prestashopOrder.getHeader().getPrestashopOrderId());
                response.setCode("0203901");
                response.setDetail("No se encontraron facturas para la orden con codigo " + prestashopOrder.getHeader().getPrestashopOrderId());
                return response;
            }
            if (!DateUtils.isSameDay(datosFactura.getDocDate(), new Date())) {
                log.log(Level.WARNING, "No se puede realizar la anulacion de una factura de fechas pasadas. ");
                webIntegratorBean.devolverSesion(sesionSAPDTO);
                response.setCode("0203901");
                response.setDetail("No se puede realizar la anulacion de una factura de fechas pasadas. ");
                return response;
            }
            //cancela el recibo de caja
            cancelIncomingPayment(datosFactura.getRefDocnum(), sesionSAPDTO);
            //crea la nota credito por anulacion
            CreditNotesServiceConnector sc = sapB1WSBean.getCreditNotesServiceConnectorInstance(sesionSAPDTO.getIdSesionSAP());
            Long docEntryCreditNote = sc.createCreditNote(datosFactura);
            if (docEntryCreditNote == null) {
                webIntegratorBean.devolverSesion(sesionSAPDTO);
                log.log(Level.SEVERE, "No se pudo registrar la nota credito en SAP.");
                response.setCode("0203902");
                response.setDetail("Ocurrio un error al crear la nota credito. ");
                return response;
            }
            //crea el asiento para mercancia en consignacion
            createJournalEntryCreditNote(sesionSAPDTO, docEntryCreditNote);
            if (needsGoodsIssue(datosFactura)) {
                try {
                    //crea la salida de mercancia de cliente
                    createGoodsIssue(sesionSAPDTO, datosFactura, datosFactura.getRefDocnum(), docEntryCreditNote);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "No fue posible crear la salida de mercancia. ", e);
                    //TODO: enviar notificacion de error
                }
                try {
                    //cancela la orden de venta, si aplica
                    cancelSalesOrder(datosFactura.getRefDocnum(), sesionSAPDTO);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "No fue posible cancelar la orden de venta. ", e);
                    //TODO: enviar notificacion de error
                }
            }
            //registra la factura y la nota credito en la tabla de documentos excluidos para que no se visualicen en el informe de ventas
            try {
                DocumentosExcluidos fac = new DocumentosExcluidos();
                DocumentosExcluidos dev = new DocumentosExcluidos();
                long id = System.currentTimeMillis();
                fac.setCode(Long.toString(id));
                fac.setName(Long.toString(id));
                fac.setDocNum(datosFactura.getRefDocnum());
                fac.setFechaExclusion(new Date());
                fac.setUsuarioExcluye("prestashop");
                fac.setTipoDocumento(DocumentosExcluidos.TipoDocumento.Factura);

                dev.setCode(Long.toString(id + 1));
                dev.setName(Long.toString(id + 1));
                dev.setDocNum(datosFactura.getRefDocnum());
                dev.setFechaExclusion(new Date());
                dev.setUsuarioExcluye("prestashop");
                dev.setTipoDocumento(DocumentosExcluidos.TipoDocumento.Devolucion);

                documentosExcluidosFacade.create(fac);
                documentosExcluidosFacade.create(dev);
            } catch (Exception e) {
                log.log(Level.WARNING, "No fue posible excluir la factura y la nota credito del informe de ventas. ", e);
            }
            webIntegratorBean.devolverSesion(sesionSAPDTO);
            Object[] datos = facturaSAPFacade.getVoidedInvoiceRelatedDocuments(datosFactura.getRefDocnum());
            response.setCode("0203001");
            response.setDetail("La factura ha sido anulada exitosamente. [FV=" + datosFactura.getRefDocnum() + "] [DV=" + datos[0] + "] [OV="
                    + datos[1] + "] [SM=" + datos[2] + "]");
            if (datos == null) {
                //TODO: enviar correo de advertencia indicando que no se pudo consultar el estado de la creacion de documentos
                response.setDetail("La factura ha sido anulada exitosamente. [FV=" + datosFactura.getRefDocnum() + "]");
            } else {
                response.setDetail("La factura ha sido anulada exitosamente. [FV=" + datosFactura.getRefDocnum() + "] [DV=" + datos[0] + "] [OV="
                        + datos[1] + "] [SM=" + datos[2] + "]");
            }
            return response;
        } catch (Exception e) {
            webIntegratorBean.devolverSesion(sesionSAPDTO);
            log.log(Level.SEVERE, "Ocurrio un error al crear el documento en SAP. ", e);
            response.setCode("0203902");
            response.setDetail("Ocurrio un error al crear la nota credito. " + e.getMessage());
            return response;
        }
    }

    private Long createIncomingPayment(FacturaSAP factura, IncomingPaymentDTO payment) throws IncomingPaymentServiceException {
        PaymentDTO pagoDto = new PaymentDTO();
        pagoDto.setPaymentType(PaymentDTO.PaymentTypeDTO.CUSTOMER);
        pagoDto.setCardCode(factura.getCardCode());
        pagoDto.setCreditType("I");
        pagoDto.setInvoiceDocEntry(factura.getDocEntry().toString());
        pagoDto.setPaidTotal(Long.toString(factura.getDocTotal().longValue()));
        pagoDto.setSeriesCode("151"); //TODO: parametrizar serie de numeracion
        pagoDto.setDocType(ConstantTypes.DocType.INVOICE);
        List<CreditCardPaymentDTO> creditCardPayments = new ArrayList<>();

        CreditCardPaymentDTO creditPayment = new CreditCardPaymentDTO();
        creditPayment.setCreditCardCode(webIntegratorBean.getTarjetaCredito(payment.getFranquicia()).getId().toString());
        creditPayment.setCreditCardNumber(payment.getNroTarjeta());
        creditPayment.setNumberOfPayments("1");
        creditPayment.setPaidSum(Long.toString(factura.getDocTotal().longValue()));
        creditPayment.setValidUntil(null);//TODO: configurar fecha de validez
        creditPayment.setVoucherNumber(payment.getVoucher());
        creditCardPayments.add(creditPayment);
        pagoDto.setCreditCardPayments(creditCardPayments);

        SesionSAPB1WSDTO sesionSap = webIntegratorBean.obtenerSesionDisponible();
        IncomingPaymentServiceConnector ipsc = sapB1WSBean.getIncomingPaymentServiceConnectorInstance(sesionSap.getIdSesionSAP());
        Long docEntryRecibo = ipsc.addPayment(pagoDto);
        webIntegratorBean.devolverSesion(sesionSap);
        return docEntryRecibo;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenericResponse addPayment(Object incomingPayment) {
        GenericResponse response = new GenericResponse();
        IncomingPaymentDTO payment = (IncomingPaymentDTO) incomingPayment;
        if (payment.getNroFactura() != null && payment.getNroFactura().startsWith(PREFIJO_FACTURA_WEB)) {
            payment.setNroFactura(payment.getNroFactura().replace(PREFIJO_FACTURA_WEB, NRO_FACTURA_WEB));
        }
        FacturaSAP factura = null;
        try {
            factura = facturaSAPFacade.findByDocNum(Integer.parseInt(payment.getNroFactura()));
        } catch (Exception e) {
            log.log(Level.SEVERE, "No se encontro una factura con el numero " + payment.getNroFactura(), e);
            response.setCode("0204901");
            response.setDetail("No se encontro ninguna orden con el numero de factura SAP " + payment.getNroFactura());
            return response;
        }
        if (factura.getDocStatus().equals('C')) {
            response.setCode("0204902");
            response.setDetail("La factura" + factura.getDocNum() + " se encuentra cerrada y no puede recibir pagos");
            return response;
        }
        if (payment.getNroTarjeta() == null || payment.getNroTarjeta().trim().isEmpty() && payment.getTipo().equals("CREDITO")) {
            response.setCode("0204903");
            response.setDetail("Se debe indicar los ultimos 4 digitos de la tarjeta con la que se realizo el pago");
            return response;
        }
        if (payment.getTipo().equals("DEBITO") && payment.getNroTarjeta().trim().isEmpty()) {
            log.log(Level.INFO, "Se configura el numero de tarjeta 0000 y la franquicia MAESTRO ya que se paga con tarjeta DEBITO. Franquicia original: {0}", payment.getFranquicia());
            payment.setNroTarjeta("0000");
            payment.setFranquicia("MAESTRO");
        }
        //Si envian mas de 4 digitos de la tarjeta, se utilizan solo los 4 ultimos
        if (payment.getNroTarjeta().length() > 4) {
            payment.setNroTarjeta(payment.getNroTarjeta().substring(payment.getNroTarjeta().length() - 4));
        }
        if (payment.getVoucher() == null || payment.getVoucher().trim().isEmpty()) {
            response.setCode("0204904");
            response.setDetail("Se debe indicar el numero del comprobante de pago (voucher)");
            return response;
        }
        if (payment.getFranquicia() == null || payment.getFranquicia().trim().isEmpty()) {
            response.setCode("0204908");
            response.setDetail("Se debe indicar la franquicia de la tarjeta de credito");
            return response;
        }
        if (payment.getTipo() == null || payment.getTipo().trim().isEmpty()) {
            response.setCode("0204909");
            response.setDetail("Se debe indicar el tipo de tarjeta de credito");
            return response;
        }
        try {
            FranquiciaTarjeta franquicia = franquiciaFacade.find(payment.getFranquicia());
            if (!franquicia.getTipoFranquicia().equals(payment.getTipo())) {
                response.setCode("0204910");
                response.setDetail("El tipo de tarjeta no corresponde con la franquicia. Se esperaba [" + franquicia.getTipoFranquicia()
                        + "] y se recibio [" + payment.getTipo() + "]");
                return response;
            }
        } catch (NoResultException e) {
            response.setCode("0204911");
            response.setDetail("No se encontro la franquicia enviada [" + payment.getFranquicia() + "]");
            return response;
        } catch (Exception e) {
            response.setCode("0204912");
            response.setDetail("Ocurrio un error al validar la franquicia [" + payment.getFranquicia() + "]. " + e.getMessage());
            return response;
        }

        try {
            Long docEntryRecibo = createIncomingPayment(factura, payment);
            log.log(Level.INFO, "Se creo el recibo de caja (docEntry) #{0}", docEntryRecibo);
            //enviar email notificacion
            sendInvoiceEmailNotification(factura);
            response.setCode("0204001");
            response.setDetail(docEntryRecibo.toString());
            return response;
        } catch (Exception e) {
            response.setCode("0204906");
            response.setDetail("Ocurrio un error al registrar el pago. " + e.getMessage());
            return response;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CustomerOrdersDTO findOrders(Object customer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        CustomerDTO dto = (CustomerDTO) customer;
        List<Object[]> facturas = facturaSAPFacade.listCustomerOrders(dto.getId());
        List<InvoiceHeaderDTO> ordenes = new ArrayList<>();
        for (Object[] row : facturas) {
            String prefix = (String) row[0];
            String docnum = (String) row[1];
            Date docdate = (Date) row[2];
            String itemcode = (String) row[3];
            Integer cantidad = (Integer) row[4];
            Integer precio = (Integer) row[5];

            InvoiceDetailDTO detail = new InvoiceDetailDTO();
            detail.setItemCode(itemcode);
            detail.setQuantity(cantidad.toString());
            detail.setPrice(precio.longValue());

            String invoiceNumber = prefix.concat(docnum.substring(docnum.indexOf("0")));
            InvoiceHeaderDTO header = new InvoiceHeaderDTO();
            header.setInvoiceNumber(invoiceNumber);
            int pos = ordenes.indexOf(header);
            if (pos >= 0) {
                header = ordenes.get(pos);
                int pos2 = header.getItems().indexOf(detail);
                if (pos2 >= 0) {
                    header.getItems().get(pos2).setQuantity(header.getItems().get(pos2).getQuantity() + cantidad);
                } else {
                    header.addDetail(detail);
                }
            } else {
                header.addDetail(detail);
                header.setDocumentDate(sdf.format(docdate));
                ordenes.add(header);
            }
        }
        return new CustomerOrdersDTO(ordenes);
    }

    public GenericResponse scheduleDelivery(Object order) {
        GenericResponse response = new GenericResponse();
        OrderDTO prestashopOrder = (OrderDTO) order;

        return response;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AdditionalCostsDTO calculateAditionalCosts(Object parameters) {
        AdditionalCostsDTO dto = new AdditionalCostsDTO();
        OrderParametersDTO order = (OrderParametersDTO) parameters;

        int valor = (int) (Math.random() * 10);
        if (valor < 4) {
            dto.setDeliveryCost("0");
        } else if (valor < 8) {
            dto.setDeliveryCost("100000");
        } else {
            dto.setDeliveryCost("350000");
        }

        int valor2 = (int) (Math.random() * 10);
        if (valor2 < 5) {
            dto.setInstallationCost("0");
        } else {
            dto.setInstallationCost("200000");
        }

        return dto;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public OrderTrackingInfoDTO trackOrder(Object order) {
        OrderTrackingInfoDTO response = new OrderTrackingInfoDTO();
        OrderDTO prestashopOrder = (OrderDTO) order;

        if (prestashopOrder.getHeader() == null || prestashopOrder.getHeader().getSapOrderId() == null) {
            response.setCode("0205901");
            response.setDetail("No se recibio el numero de factura para rastrear");
            return response;
        }

        List<BaruEstadoFactura> estados = estadoFacturaFacade.findByOrderNumber(prestashopOrder.getHeader().getSapOrderId());
        if (estados.isEmpty()) {
        }

        List<OrderTrackingDetailDTO> detail = new ArrayList<>();

        OrderTrackingDetailDTO det = new OrderTrackingDetailDTO();
        det.setDate("20141115164322");
        det.setItemCode("10900000000000000272");
        det.setStatus("EN PREPARACION");

        OrderTrackingDetailDTO det2 = new OrderTrackingDetailDTO();
        det2.setDate("20141116164322");
        det2.setItemCode("10900000000000000272");
        det2.setStatus("EN TRANSITO");

        OrderTrackingDetailDTO det3 = new OrderTrackingDetailDTO();
        det3.setDate("20141117164322");
        det3.setItemCode("10900000000000000272");
        det3.setStatus("ENTREGADO");

        OrderTrackingDetailDTO det4 = new OrderTrackingDetailDTO();
        det4.setDate("20141115164322");
        det4.setItemCode("10900000000000000262");
        det4.setStatus("GUARDADO");

        detail.add(det);
        detail.add(det2);
        detail.add(det3);
        detail.add(det4);

        response.setTrackingInfo(detail);

        return response;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ShippingQuotationResultDTO quoteShipping(Object order) throws Exception {
        //TODO: realizar pruebas con ordenes que no superen el monto minimo para envio gratuito
        OrderDTO dto = (OrderDTO) order;
        String orderId = dto.getHeader().getPrestashopOrderId();
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new Exception("Por favor indique el numero de orden [prestashopOrderId]");
        }
        log.log(Level.INFO, "Generando cotizacion de envio para orden #[{0}]", orderId);
        CotizacionWeb cotizacionEntidad = cotizacionFacade.findByPrestashopId(Integer.valueOf(orderId));
        if (cotizacionEntidad == null || cotizacionEntidad.getNroDocPrestashop() == null) {
            throw new Exception("No se encontro ninguna orden con el numero [" + orderId + "]");
        }
        List<DetalleCotizacionWeb> filasCotizacion = detalleCotizacionFacade.findByIdCotizacion(cotizacionEntidad.getIdCotizacion());
        List<SalesWarehouseItemDTO> reqItems = new ArrayList<>();
        for (DetalleCotizacionWeb entidadDetalle : filasCotizacion) {
            SalesWarehouseItemDTO itemDto = new SalesWarehouseItemDTO();
            itemDto.setItemCode(entidadDetalle.getReferencia());
            itemDto.setQuantity(entidadDetalle.getCantidad());

            reqItems.add(itemDto);
        }
        log.log(Level.INFO, "La cotizacion tiene {0} articulos", reqItems.size());

        String codigoCiudad = null;
        try {
            CotizacionSAP entidadCotizacionSAP = cotizacionSAPFacade.find(Integer.valueOf(cotizacionEntidad.getNumeroDocSAP()));
            String nombreDireccion = entidadCotizacionSAP.getPayToCode();
            List<DireccionSocioDeNegocios> direccionesSAP = direccionClienteFacade.findByCardCode(entidadCotizacionSAP.getCardCode());
            for (DireccionSocioDeNegocios entidadDireccion : direccionesSAP) {
                if (entidadDireccion.getDireccionSocioDeNegociosPK().getAddress().equals(nombreDireccion)) {
                    codigoCiudad = entidadDireccion.getUMunicipio();
                    break;
                }
            }
            if (codigoCiudad == null) {
                throw new Exception("No fue posible obtener la ciudad de origen de la mercancia");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al consultar la ciudad para la cotizacion SAP #[{0}]", dto.getHeader().getSapOrderId());
            throw new Exception("No fue posible obtener la ciudad de origen de la mercancia");
        }

        SalesWarehouseDTO reqDto = new SalesWarehouseDTO();
        reqDto.setPrestashopId(orderId);
        reqDto.setDestinationCityCode(codigoCiudad);
        reqDto.setItems(reqItems);

        return inventoryItemEJB.quoteShipping(reqDto);
    }

    public void scheduleOrderPicking(Object order) {
        OrderDTO orderDto = (OrderDTO) order;

    }

    public GenericResponse giftIncludesDeluxePackaging(String valor) {
        int montoLimite = 1000000;
        GenericResponse response = new GenericResponse("0150001", null);
        if (valor != null && !valor.trim().isEmpty()) {
            try {
                if (Integer.parseInt(valor) >= montoLimite) {
                    response.setDetail("1");
                    return response;
                }
            } catch (Exception e) {
            }
        }
        response.setDetail("0");
        return response;
    }

    private void sendInvoiceEmailNotification(FacturaSAP factura) {
        MailMessageDTO mailMessage = new MailMessageDTO();
        mailMessage.setFrom("Ventas Web <ventasweb@matisses.co>");
        mailMessage.setSubject("Nueva factura WEB #" + factura.getDocNum());
        mailMessage.addToAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("factura_web").getTo());
        mailMessage.addCcAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("factura_web").getCc());
        mailMessage.addBccAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("factura_web").getBcc());
        NumberFormat nf = NumberFormat.getInstance();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("customerName", factura.getCardName());
        parameters.put("customerId", factura.getCardCode());
        parameters.put("invoiceTotal", nf.format(factura.getDocTotal().intValue()));
        parameters.put("invoiceNumber", Integer.toString(factura.getDocNum()));
        try {
            emailSender.sendMail(mailMessage, SendHTMLEmailMBean.MessageTemplate.factura_web, parameters, null);
        } catch (Exception e) {
            log.log(Level.SEVERE, "No se pudo enviar el correo de notificacion. ", e);
        }
    }

    private void sendErrorEmailNotification(String methodName, String errorMessage) {
        MailMessageDTO mailMessage = new MailMessageDTO();
        mailMessage.setFrom("Ventas Web <ventasweb@matisses.co>");
        mailMessage.setSubject("Ocurrio un error creando factura web [" + methodName + "]");
        mailMessage.addToAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("error").getTo());
        mailMessage.addCcAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("error").getCc());
        mailMessage.addBccAddresses(webIntegratorBean.getDestinatariosPlantillaEmail().get("error").getBcc());
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
}
