package com.models.user;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Builder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Harshit Rajvaidya
 */

@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        ),
        @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})


@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "com.models.user.Users.findAll",
                query = "select e from Users e"),
        @NamedQuery(name = "validateUserWithEmail",
                query = "select e from Users e where e.email = :email")
})

public class Users {
    public Users() {
    }

    @Id
    @Column(name = "user_id")
    private String user_id;

    @Column(name = "first_name" )
    private String first_name;

    @Column(name = "last_name" )
    private String last_name;

    @Column(name = "email" )
    private String email;

    @Type(type = "string-array")
    @Column(name = "owned_splits", columnDefinition = "text[]")
    private String[] owned_splits;

    @Type(type = "string-array")
    @Column(name = "member_of_splits" , columnDefinition = "text[]")
    private String[] member_of_splits;

    @Type(type = "string-array")
    @Column(name = "friends", columnDefinition = "text[]")
    private String[] friends;

    @Column(name ="created_at" )
    private Timestamp created_at;

    @Type( type = "json" )
    @Column(name = "amount_owe")
    private Map<String, String> amount_owe;

    public Map<String, String> getAmount_owe() {
        return amount_owe;
    }

    public void setAmount_owe(Map<String, String> amount_owe) {
        this.amount_owe = amount_owe;
    }

    public Map<String, String> getAmount_get() {
        return amount_get;
    }

    public void setAmount_get(Map<String, String> amount_get) {
        this.amount_get = amount_get;
    }

    @Type( type = "json" )
    @Column(name = "amount_get")
    private Map<String , String> amount_get;

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getMember_of_splits() {
        return member_of_splits;
    }

    public void setMember_of_splits(String[] member_of_splits) {
        this.member_of_splits = member_of_splits;
    }

    public String[] getOwned_splits() {
        return owned_splits;
    }

    public void setOwned_splits(String[] owned_splits) {
        this.owned_splits = owned_splits;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

}
