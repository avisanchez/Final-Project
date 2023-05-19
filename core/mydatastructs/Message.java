package core.mydatastructs;

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

    // specific states of a certain Type
    public enum Tag {
        CREATE_PLAYER,
        ASSIGN_PLAYER,
        UPDATE_PLAYER,

        // public Type type;

        // private Tag(Type type) {
        // this.type = type;
        // }
    }

    // meta categories that Tags belong to
    // public enum Type {
    // PLAYER,
    // // GAME_OBJECT?
    // }
}
