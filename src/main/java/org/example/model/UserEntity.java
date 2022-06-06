package org.example.model;



import com.google.gson.Gson;
import org.example.annotation.Attribute;

import java.util.Date;


public class UserEntity extends BasicEntity {

    @Attribute(value = 3)
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public UserEntity() {
    }


    public UserEntity(Integer objId, Integer owner, String name, Date date, String type) {
        super(objId, owner, name, date, type);
    }
}
