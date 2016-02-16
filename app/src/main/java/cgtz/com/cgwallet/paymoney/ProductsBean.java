package cgtz.com.cgwallet.paymoney;

/**
 * Created by 朋 on 2015/12/31.
 */
public class ProductsBean {
    private String parValue;

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    private String salePrice;

    public String getParValue() {
        return parValue;
    }

    public void setParValue(String parValue) {
        this.parValue = parValue;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    private boolean onSale;




}
