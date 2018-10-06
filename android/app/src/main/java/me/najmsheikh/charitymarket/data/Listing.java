package me.najmsheikh.charitymarket.data;

public class Listing {

    private String title;
    private String brand;
    private String description;
    private String image;
    private String price;
    private String sellerNGO;
    private String buyerNGO;
    private String seller_uid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerNGO() {
        return sellerNGO;
    }

    public void setSellerNGO(String sellerNGO) {
        this.sellerNGO = sellerNGO;
    }

    public String getBuyerNGO() {
        return buyerNGO;
    }

    public void setBuyerNGO(String buyerNGO) {
        this.buyerNGO = buyerNGO;
    }

    public String getSeller_uid() {
        return seller_uid;
    }

    public void setSeller_uid(String seller_uid) {
        this.seller_uid = seller_uid;
    }
}
