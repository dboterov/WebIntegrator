package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class ShippingPriceDTO {

    private Integer price;
    private Long timestamp;
    private String shippingCompany;

    public ShippingPriceDTO() {
        timestamp = System.currentTimeMillis();
    }

    public ShippingPriceDTO(Integer price, String shippingCompany) {
        this.price = price;
        timestamp = System.currentTimeMillis();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    @Override
    public String toString() {
        return "ShippingPriceDTO{" + "price=" + price + ", timestamp=" + timestamp + ", shippingCompany=" + shippingCompany + '}';
    }

}
