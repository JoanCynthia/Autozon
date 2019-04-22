package redeem.com.autozon;

public class CatalogBean
{
    private String name, imageName, description, link;

    public CatalogBean(String name, String imageName, String description, String link) {
        this.name = name;
        this.imageName = imageName;
        this.description = description;
        this.link = link;
    }

    public String getImageName() {
        return imageName;
    }

//    public void setImageName(String imageName) {
//        this.imageName = imageName;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
