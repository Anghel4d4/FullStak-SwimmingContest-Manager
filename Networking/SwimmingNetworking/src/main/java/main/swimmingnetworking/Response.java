package main.swimmingnetworking;

import java.io.Serializable;

public record Response(ResponseType type, Object data) implements Serializable {

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
