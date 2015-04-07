package bean;

/**
 * Created by wangpeng on 15-4-3.
 */
public class CompanyDataBean {
    private String id;
    private String data_title;
    private String data_information;
    private String data_image;
    private String data_thumbnail;
    private String data_5;
    private String data_6;
    private String data_7;
    private String data_8;
    private String data_9;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setDataTitle(String data_title) {
        this.data_title = data_title;
    }

    public String getDataTitle() {
        return data_title;
    }

    public void setDataInformation(String data_information) {
        this.data_information = data_information;
    }

    public String getDataInformation() {
        return this.data_information;
    }

    public void setDataImage(String data_image) {
        this.data_image = data_image;
    }

    public String getDataImage() {
        return data_image;
    }

    public void setDataThumbnail(String data_thumbnail) {
        this.data_thumbnail = data_thumbnail;
    }

    public String getDataThumbnail() {
        return this.data_thumbnail;
    }

    @Override
    public String toString() {
        return "CompanyDataBean { id= " + this.id
                + " data_title=" + this.data_title
                + " data_information=" + this.data_information
                + " data_thumbnail=" + this.data_thumbnail
                + " }";
    }
}
