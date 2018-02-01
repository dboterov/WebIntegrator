package co.matisses.webintegrator.ejb;

import co.matisses.b1ws.businessparters.BusinessPartnersService;
import static co.matisses.b1ws.client.B1WSServiceInfo.BUSINESS_PARTNERS_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.CREDIT_NOTES_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.DRAFTS_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.INCOMING_PAYMENT_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.INVOICES_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.GOODS_ISSUE_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.GOODS_RECEIPT_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.ITEMS_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.JOURNAL_ENTRIES_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.ORDER_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.SERVICE_CALLS_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.STOCK_TRANSFER_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.VENDOR_PAYMENTS_SERVICE_WSDL_NAME;
import static co.matisses.b1ws.client.B1WSServiceInfo.RUTA_WSDL;
import co.matisses.b1ws.client.businesspartners.BusinessPartnersServiceConnector;
import co.matisses.b1ws.client.creditnotes.CreditNotesServiceConnector;
import co.matisses.b1ws.client.drafts.DraftsServiceConnector;
import co.matisses.b1ws.client.goodsissue.GoodsIssueServiceConnector;
import co.matisses.b1ws.client.goodsreceipt.GoodsReceiptServiceConnector;
import co.matisses.b1ws.client.invoices.InvoicesServiceConnector;
import co.matisses.b1ws.client.items.ItemsServiceConnector;
import co.matisses.b1ws.client.journalentries.JournalEntriesServiceConnector;
import co.matisses.b1ws.client.orders.OrdersServiceConnector;
import co.matisses.b1ws.client.payments.IncomingPaymentServiceConnector;
import co.matisses.b1ws.client.servicecalls.ServiceCallsServiceConnector;
import co.matisses.b1ws.client.stocktransfer.StockTransferServiceConnector;
import co.matisses.b1ws.client.vendorpayment.VendorPaymentServiceConnector;
import co.matisses.b1ws.creditnotes.CreditNotesService;
import co.matisses.b1ws.drafts.DraftsService;
import co.matisses.b1ws.goodsissue.InventoryGenExitService;
import co.matisses.b1ws.goodsreceipt.InventoryGenEntryService;
import co.matisses.b1ws.incomingpayment.IncomingPaymentsService;
import co.matisses.b1ws.invoices.InvoicesService;
import co.matisses.b1ws.items.ItemsService;
import co.matisses.b1ws.journalentries.JournalEntriesService;
import co.matisses.b1ws.orders.OrdersService;
import co.matisses.b1ws.servicecalls.ServiceCallsService;
import co.matisses.b1ws.stocktransfer.StockTransferService;
import co.matisses.b1ws.vendorpayment.VendorPaymentsService;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author dbotero
 */
@ApplicationScoped
@Named("SAPB1WSBean")
public class SAPB1WSBean {

    private static final Logger log = Logger.getLogger(SAPB1WSBean.class.getSimpleName());
    private InvoicesService invoiceService = null;
    private IncomingPaymentsService incomingPaymentService = null;
    private BusinessPartnersService businessPartnerService = null;
    private InventoryGenExitService inventoryGenExitService = null;
    private DraftsService draftsService = null;
    private InventoryGenEntryService inventoryGenEntryService = null;
    private StockTransferService stockTransferService = null;
    private CreditNotesService creditNotesService = null;
    private VendorPaymentsService vendorPaymentService = null;
    private OrdersService ordersService = null;
    private JournalEntriesService journalEntriesService = null;
    private ItemsService itemsService = null;
    private ServiceCallsService serviceCallsService = null;

    @PostConstruct
    public void initialize() {
        log.info("Iniciando instancias de servicios de SAPB1WS");
        initializeServices();
    }

    private void initializeServices() {
        try {
            invoiceService = new InvoicesService(new URL(String.format(RUTA_WSDL, INVOICES_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de facturas. ", e);
        }

        try {
            incomingPaymentService = new IncomingPaymentsService(new URL(String.format(RUTA_WSDL, INCOMING_PAYMENT_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de pagos. ", e);
        }

        try {
            businessPartnerService = new BusinessPartnersService(new URL(String.format(RUTA_WSDL, BUSINESS_PARTNERS_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de clientes. ", e);
        }

        try {
            inventoryGenExitService = new InventoryGenExitService(new URL(String.format(RUTA_WSDL, GOODS_ISSUE_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de salidas de mercancia. ", e);
        }

        try {
            draftsService = new DraftsService(new URL(String.format(RUTA_WSDL, DRAFTS_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de documentos preliminares. ", e);
        }

        try {
            inventoryGenEntryService = new InventoryGenEntryService(new URL(String.format(RUTA_WSDL, GOODS_RECEIPT_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de entradas de mercancia. ", e);
        }

        try {
            journalEntriesService = new JournalEntriesService(new URL(String.format(RUTA_WSDL, JOURNAL_ENTRIES_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de asientos contables. ", e);
        }

        try {
            stockTransferService = new StockTransferService(new URL(String.format(RUTA_WSDL, STOCK_TRANSFER_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de transferencia de stock. ", e);
        }

        try {
            creditNotesService = new CreditNotesService(new URL(String.format(RUTA_WSDL, CREDIT_NOTES_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de notas credito. ", e);
        }

        try {
            vendorPaymentService = new VendorPaymentsService(new URL(String.format(RUTA_WSDL, VENDOR_PAYMENTS_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de egresos. ", e);
        }

        try {
            ordersService = new OrdersService(new URL(String.format(RUTA_WSDL, ORDER_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de ordenes de venta. ", e);
        }

        try {
            itemsService = new ItemsService(new URL(String.format(RUTA_WSDL, ITEMS_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de items de inventario. ", e);
        }

        try {
            serviceCallsService = new ServiceCallsService(new URL(String.format(RUTA_WSDL, SERVICE_CALLS_SERVICE_WSDL_NAME)));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Ocurrio un error al inicializar el servicio de llamadas de servicio (garantias). ", e);
        }
    }

    public InvoicesServiceConnector getInvoicesServiceConnectorInstance(String sessionId) {
        return new InvoicesServiceConnector(invoiceService, sessionId);
    }

    public IncomingPaymentServiceConnector getIncomingPaymentServiceConnectorInstance(String sessionId) {
        return new IncomingPaymentServiceConnector(incomingPaymentService, sessionId);
    }

    public BusinessPartnersServiceConnector getBusinessPartnersServiceConnectorInstance(String sessionId) {
        return new BusinessPartnersServiceConnector(businessPartnerService, sessionId);
    }

    public GoodsIssueServiceConnector getGoodsIssueServiceConnectorInstance(String sessionId) {
        return new GoodsIssueServiceConnector(inventoryGenExitService, sessionId);
    }

    public DraftsServiceConnector getDraftsServiceConnectorInstance(String sessionId) {
        return new DraftsServiceConnector(draftsService, sessionId);
    }

    public GoodsReceiptServiceConnector getGoodsReceiptServiceConnectorInstance(String sessionId) {
        return new GoodsReceiptServiceConnector(inventoryGenEntryService, sessionId);
    }

    public StockTransferServiceConnector getStockTransferServiceConnectorInstance(String sessionId) {
        return new StockTransferServiceConnector(stockTransferService, sessionId);
    }

    public CreditNotesServiceConnector getCreditNotesServiceConnectorInstance(String sessionId) {
        return new CreditNotesServiceConnector(creditNotesService, sessionId);
    }

    public VendorPaymentServiceConnector getVendorPaymentServiceConnectorInstance(String sessionId) {
        return new VendorPaymentServiceConnector(vendorPaymentService, sessionId);
    }

    public OrdersServiceConnector getOrderServiceConnectorInstance(String sessionId) {
        return new OrdersServiceConnector(ordersService, sessionId);
    }

    public JournalEntriesServiceConnector getJournalEntriesServiceConnectorInstance(String sessionId) {
        return new JournalEntriesServiceConnector(journalEntriesService, sessionId);
    }

    public ItemsServiceConnector getItemsServiceConnectorInstance(String sessionId) {
        return new ItemsServiceConnector(itemsService, sessionId);
    }

    public ServiceCallsServiceConnector getServiceCallsServiceConnectorInstance(String sessionId) {
        return new ServiceCallsServiceConnector(serviceCallsService, sessionId);
    }
}
