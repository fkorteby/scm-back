package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAds;
    private String adTitle;
    private String adDescription;
    private String imageUrl;
    private String targetUrl;
    private String wilayas;
    private String Specialites;
    private LocalDate startDate;
    private LocalDate endDate;

    public Ads() {
    }

    public Ads(Long idAds, String adTitle, String adDescription, String imageUrl, String targetUrl, String wilayas, String specialites, LocalDate startDate, LocalDate endDate) {
        this.idAds = idAds;
        this.adTitle = adTitle;
        this.adDescription = adDescription;
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
        this.wilayas = wilayas;
        Specialites = specialites;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getIdAds() {
        return idAds;
    }

    public void setIdAds(Long idAds) {
        this.idAds = idAds;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdDescription() {
        return adDescription;
    }

    public void setAdDescription(String adDescription) {
        this.adDescription = adDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getWilayas() {
        return wilayas;
    }

    public void setWilayas(String wilayas) {
        this.wilayas = wilayas;
    }

    public String getSpecialites() {
        return Specialites;
    }

    public void setSpecialites(String specialites) {
        Specialites = specialites;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
