package com.foundation.io.simple.contact.room;

import java.io.Serializable;

/**
 * @author : jacksonz
 * @date : 2021/5/30 9:42
 */
public class MessageDto implements Serializable {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
