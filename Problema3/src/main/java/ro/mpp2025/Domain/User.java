package ro.mpp2025.Domain;

public class User extends Entity<Long> {
    private final String username;
    private final String password;

    public User(Long id, String username, String password) {
        this.setId(id);

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
