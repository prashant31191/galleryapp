package redgao.leoxun.gallery.model;

public class GalleryItem
{
    private String galleryUrl;
    private String title;
    private String imageUrl;
    private int thumbHeight;
    private int thumbWidth;

    public GalleryItem(String galleryUrl, String title, String imageUrl)
    {
        this.galleryUrl = galleryUrl;
        this.title = title;
        this.imageUrl = imageUrl;
        thumbHeight = thumbWidth = 0;
    }

    public String getGalleryUrl() {
        return galleryUrl;
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }   
    
}
