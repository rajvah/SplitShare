package com.models.split;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;


/**
 * @author Harshit Rajvaidya
 */


@Entity
@Table(name = "splits")
public class Splits implements Serializable {

    @Id
    @Column(name = "split_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID split_id;

    @Column(name = "owner")
    private String owner;

    @Type(type = "string-array")
    @Column(name = "members", columnDefinition = "text[]")
    private String[] members;

    @Column(name = "amount")
    private String amount;

    @Column(name ="created_at" )
    private Timestamp created_at;

    @Column(name ="updated_at" )
    private Timestamp updated_at;

    public UUID getSplit_id() {
        return split_id;
    }

    public void setSplit_id(UUID split_id) {
        this.split_id = split_id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }


}
