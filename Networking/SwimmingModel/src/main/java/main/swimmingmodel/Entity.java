package main.swimmingmodel;

import java.io.Serializable;

public class Entity<ID> implements Serializable {
    private ID id;

    public Entity() {
    }

    public ID getId() {

        return this.id;
    }

    public void setId(ID id) {

        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Entity)) {
            return false;
        } else {
            Entity<?> entity = (Entity) o;
            return this.getId().equals(entity.getId());
        }
    }

    public int hashCode() {

        return this.getId().hashCode();
    }

    public String toString() {
        return "Entity{id=" + this.id + "}";
    }

}
