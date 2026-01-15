package rest.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
public class Entity<ID> implements Serializable {
    private ID id;

    public Entity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public ID getId() {

        return this.id;
    }

    public void setId(ID id) {

        this.id = id;
    }

    @Override
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

    @Override
    public int hashCode() {

        return this.getId().hashCode();
    }

    public String toString() {
        return "Entity{id=" + this.id + "}";
    }

}
