package main.swimmingnetworking;

import java.io.Serializable;

public record Request(RequestType type, Object data) implements Serializable {

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}