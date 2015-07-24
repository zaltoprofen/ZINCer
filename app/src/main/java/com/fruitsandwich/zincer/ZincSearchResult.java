package com.fruitsandwich.zincer;

import android.graphics.Bitmap;

import rx.Observable;

/**
 * Created by nakac on 15/07/18.
 */
public class ZincSearchResult {
    private Long zincId;
    private String imageUrl;
    private String subLink;
    private ZincDetail detail;
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ZincSearchResult(){
    }

    public ZincSearchResult(Long zincId, String imageUrl, String subLink) {
        this.zincId = zincId;
        this.imageUrl = imageUrl;
        this.subLink = subLink;
    }

    public Long getZincId() {
        return zincId;
    }

    public void setZincId(Long zincId) {
        this.zincId = zincId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubLink() {
        return subLink;
    }

    public void setSubLink(String subLink) {
        this.subLink = subLink;
    }

    public ZincDetail getDetail() {
        return detail;
    }

    public void setDetail(ZincDetail detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return String.format("SearchResult[%s, %s, %s]",
                zincId == null ? "null" : zincId,
                imageUrl == null ? "null" : imageUrl,
                subLink == null ? "null" : subLink);
    }
}
