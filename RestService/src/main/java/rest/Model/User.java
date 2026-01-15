package rest.Model;

import java.io.Serializable;


public class User extends Entity<Long> implements Serializable {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String toString(){
        return "User{id=" + this.getId() + ", username=" + this.username + ", password=" + this.password + "}";
    }


}
