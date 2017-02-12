package fbla.mobileapp.app.dysp;

/**
 * Created by net.assistant on 2/5/2017.
 */

public class ModifiedDisplayItems {
    private String title;
    private String image_string;
    private String pricelist;
    private String owner;

    public ModifiedDisplayItems(String title, String image_string, String pricelist, String owner){
        this.title = title;
        this.image_string = image_string;
        this.pricelist = pricelist;
        this.owner = owner;
    }
    public String getTitle(){
        return this.title;
    }
    public String getImage_string(){
        return this.image_string;
    }
    public String getPricelist(){
        return this.pricelist;
    }
    public String getOwner() { return this.owner; }
}

