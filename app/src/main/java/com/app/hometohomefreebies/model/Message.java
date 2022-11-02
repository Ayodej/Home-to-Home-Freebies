
package com.app.hometohomefreebies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("is_read")
    @Expose
    private boolean isRead;

    @SerializedName("file")
    @Expose
    private String file;

    @SerializedName("sender_id")
    @Expose
    private int senderId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Message() {
    }

    public Message(int id, String message, String type, boolean isRead, String file, int senderId, String createdAt) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.file = file;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
