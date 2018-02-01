package co.matisses.webintegrator.ejb;

import co.matisses.webintegrator.dto.ShippingPriceDTO;
import java.util.HashMap;
import javax.ejb.Singleton;

/**
 *
 * @author dbotero
 */
@Singleton
public class ShippingPricesCache {

    private HashMap<ShippingItem, ShippingPriceDTO> shippingPricesCache;

    public ShippingPricesCache() {
        shippingPricesCache = new HashMap<>();
    }
    
    public ShippingPriceDTO getFromCache(String itemCode, String cityFrom, String cityTo){
        ShippingItem key = new ShippingItem(itemCode, cityFrom, cityTo);
        ShippingPriceDTO price = shippingPricesCache.get(key);
        if (price == null) {
            return null;
        } else if (System.currentTimeMillis() - price.getTimestamp() > 1000 * 60 * 60) {
            shippingPricesCache.remove(key);
            return null;
        } else {
            return price;
        }
    }

    public void addPriceToCache(String itemCode, String cityFrom, String cityTo, ShippingPriceDTO dto) {
        shippingPricesCache.put(new ShippingItem(itemCode, cityFrom, cityTo), dto);
    }

    private class ShippingItem {

        private String itemCode;
        private String cityFrom;
        private String cityTo;
        private Integer quantity;

        public ShippingItem() {
        }

        public ShippingItem(String itemCode, String cityFrom, String cityTo) {
            this.itemCode = itemCode;
            this.cityFrom = cityFrom;
            this.cityTo = cityTo;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getCityFrom() {
            return cityFrom;
        }

        public void setCityFrom(String cityFrom) {
            this.cityFrom = cityFrom;
        }

        public String getCityTo() {
            return cityTo;
        }

        public void setCityTo(String cityTo) {
            this.cityTo = cityTo;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.itemCode != null ? this.itemCode.hashCode() : 0);
            hash = 17 * hash + (this.cityFrom != null ? this.cityFrom.hashCode() : 0);
            hash = 17 * hash + (this.cityTo != null ? this.cityTo.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ShippingItem other = (ShippingItem) obj;
            if ((this.itemCode == null) ? (other.itemCode != null) : !this.itemCode.equals(other.itemCode)) {
                return false;
            }
            if ((this.cityFrom == null) ? (other.cityFrom != null) : !this.cityFrom.equals(other.cityFrom)) {
                return false;
            }
            if ((this.cityTo == null) ? (other.cityTo != null) : !this.cityTo.equals(other.cityTo)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ShippingItem{" + "itemCode=" + itemCode + ", cityFrom=" + cityFrom + ", cityTo=" + cityTo + ", quantity=" + quantity + '}';
        }
    }
}
