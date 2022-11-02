
package com.app.hometohomefreebies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Chat implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("messages")
    @Expose
    private List<Message> messages;

    @SerializedName("users")
    @Expose
    private List<User> users;

    public Chat(List<User> users) {
        this.users = users;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
