package com.jojo.homedelivery;

public class product {
    private int Id;
    private int imageCourse;
    private String productTitle;
    private String quantityCourses;
    private String urlCourse;

    public product(int id, int imageCourse, String courseTitle, String quantityCourses) {
        Id = id;
        this.imageCourse = imageCourse;
        this.productTitle = courseTitle;
        this.quantityCourses = quantityCourses;
    }

    public product(int imageCourse, String courseTitle, String quantityCourses) {
        this.imageCourse = imageCourse;
        this.productTitle = courseTitle;
        this.quantityCourses = quantityCourses;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getImageCourse() {
        return imageCourse;
    }

    public void setImageCourse(int imageCourse) {
        this.imageCourse = imageCourse;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String courseTitle) {
        this.productTitle = courseTitle;
    }

    public String getQuantityCourses() {
        return quantityCourses;
    }

    public void setQuantityCourses(String quantityCourses) {
        this.quantityCourses = quantityCourses;
    }

    public String getUrlCourse() {
        return urlCourse;
    }

    public void setUrlCourse(String urlCourse) {
        this.urlCourse = urlCourse;
    }
}
