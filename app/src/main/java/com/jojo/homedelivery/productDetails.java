package com.jojo.homedelivery;

public class productDetails {
    String price;
    String name;
    String desc;

    public productDetails(){

    }
    public productDetails(String name, String price, String desc){
        this.name = name;
        this.price = price;
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDesc() { return desc; }

    public void setPrice(String price) { this.price = price; }

    public void setName(String name) { this.name = name; }

    public void setDesc(String desc) { this.desc = desc; }


}
