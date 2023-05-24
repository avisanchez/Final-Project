package core.mydatastruct;

import java.io.Serializable;

public class Message implements Serializable {
    public Tag tag;

    private String metadata;
    private Serializable data;

    public Message(Tag tag, String metadata, Serializable data) {
        this.tag = tag;
        this.metadata = metadata;
        this.data = data;
    }

    public Message(Tag tag, Serializable data) {
        this(tag, "", data);
    }

    public Message(Tag tag) {
        this(tag, "", null);
    }

    public String getMetadata() {
        return metadata;
    }

    public Serializable getData() {
        return data;
    }

    public enum Tag {
        CREATE_PLAYER,
        ASSIGN_PLAYER,
        UPDATE_PLAYER,
        DELETE_PLAYER,

    }

}
