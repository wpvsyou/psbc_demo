package bean;

/**
 * Created by wangpeng on 15-4-3.
 */
public class PersonalBean {
    private String id;
    private String user_name;
    private String password;
    private String validity;
    private Integer level;
    private String data_3;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getUserName() {
        return this.user_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getValidity() {
        return this.validity;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public void setData3(String data3) {
        this.data_3 = data3;
    }

    public String getData3() {
        return this.data_3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!super.equals(o))
            return false;
        if (getClass() != o.getClass())
            return false;
        PersonalBean other = (PersonalBean) o;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (user_name == null) {
            if (other.user_name != null)
                return false;
        } else if (!user_name.equals(other.user_name))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (validity == null) {
            if (other.validity != null)
                return false;
        } else if (!validity.equals(other.validity))
            return false;
        if (level == null) {
            if (other.level != null)
                return false;
        } else if (!level.equals(other.level))
            return false;
        if (data_3 == null) {
            if (other.data_3 != null)
                return false;
        } else if (!data_3.equals(other.data_3))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((user_name == null) ? 0 : user_name.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((validity == null) ? 0 : validity.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((data_3 == null) ? 0 : data_3.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PersonalBean { id=" + this.id
                + " user_name= " + this.user_name
                + " password= " + this.password
                + " validity= " + this.validity
                + " level= " + this.level
                + " data_3= " + this.data_3
                + " }";
    }
}
