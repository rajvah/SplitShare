package com.resource.split;

import com.dao.split.SplitDao;
import com.dao.user.UserDao;
import com.models.split.Splits;
import com.models.user.Users;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


/**
 * @author Harshit Rajvaidya
 */

@Path("/split")
@Produces(MediaType.APPLICATION_JSON)
public class SplitResource {

    public SplitDao splitDao;
    public UserDao userDao;

    public SplitResource(SplitDao splitDao, UserDao userDao) {
        this.splitDao = splitDao;
        this.userDao = userDao;
    }

    public SplitResource(SplitDao splitDao) {
        this.splitDao = splitDao;
    }

    @GET
    @Path("/find")
    @UnitOfWork
    public Splits getSplit(@QueryParam("split_id") UUID splitId){
        return splitDao.find(splitId);
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response addSplit(@Valid Splits splitsModel){

        splitsModel.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        splitsModel.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));

        Users owner = userDao.find(splitsModel.getOwner());
        if(owner == null){
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "User :"+ splitsModel.getOwner()+ "does not exist").build();
        }

        Splits split = splitDao.save(splitsModel);
        if(split == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error creating a split, please try again").build();
        }

        // verify members

        // update for owner
        String[] owned_splits;
        if(owner.getOwned_splits() == null){
            owned_splits = new String[0];
        }
        else{
            owned_splits = owner.getOwned_splits();
        }

        List<String> ownedSplitsList = Arrays.asList(owned_splits);
        List<String> arraylist = new ArrayList<>(ownedSplitsList);
        arraylist.add(splitsModel.getSplit_id().toString());

        String k[] = arraylist.toArray(new String[arraylist.size()]);
        owner.setOwned_splits(k);

        // set split amount as amount_get for owner
        int memberCount = splitsModel.getMembers().length;
        Map<String, String> current_get;
        if(owner.getAmount_get() != null){
            current_get = owner.getAmount_get();
        } else{
            current_get = new HashMap<>();
        }

        float amount = Float.parseFloat(splitsModel.getAmount()) / (memberCount+1);

        for( String friend : splitsModel.getMembers()){
            current_get.put(friend, Float.toString(amount));
        }
        owner.setAmount_get(current_get);
        userDao.save(owner);

        // update for members

        for(String friend : splitsModel.getMembers()){
            Users member = userDao.find(friend);
            if(member == null){
                return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "User :"+ friend+ "does not exist").build();
            }
            String[] m = updateMemberSplit(member.getMember_of_splits(),
                    splitsModel.getSplit_id().toString());
            member.setMember_of_splits(m);

            // set split amount as amount_owed to owner
            Map<String, String> current_owed;
            if(member.getAmount_owe() != null){
                current_owed = member.getAmount_owe();
            } else{
                current_owed = new HashMap<>();
            }

            current_owed.put(owner.getUser_id(), Float.toString(amount));

            member.setAmount_owe(current_owed);
        }

        return Response.ok(splitsModel).build();
    }

    @POST
    @Path("/payment")
    @UnitOfWork
    public Response markPayment(@Valid Splits splitsModel, @QueryParam("user_id") String user_id){
        Users user = userDao.find(user_id);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "User :"+ user_id+ "does not exist").build();
        }
        Users owner = userDao.find(splitsModel.getOwner());
        if(owner == null){
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "User :"+ splitsModel.getOwner()+ "does not exist").build();
        }
        float amtSettle = Float.parseFloat(splitsModel.getAmount());

        Map<String, String> owed;
        if(user.getAmount_owe() != null){
            owed = user.getAmount_owe();
        } else{
            owed = new HashMap<>();
        }
        Map<String , String> ownersGet;
        if(owner.getAmount_get() != null){
            ownersGet = owner.getAmount_get();
        } else{
            ownersGet = new HashMap<>();
        }

        float amtOwed = Float.parseFloat(owed.getOrDefault(splitsModel.getOwner(), "0.0"));
        float finalAmt;
        if(amtOwed < amtSettle){
            // reverse owed settlement
            finalAmt = amtSettle - amtOwed;
            Map<String , String > get;
            if(user.getAmount_get() != null){
                get = user.getAmount_get();
            } else{
                get = new HashMap<>();
            }
            Map<String ,String> ownersOwe ;
            if(owner.getAmount_owe() != null){
                ownersOwe = owner.getAmount_owe();
            } else{
                ownersOwe = new HashMap<>();
            }
            ownersOwe.put(user_id, Float.toString(finalAmt));
            get.put(splitsModel.getOwner(), Float.toString(finalAmt));
            user.setAmount_get(get);
            owner.setAmount_owe(ownersOwe);

        } else{
            finalAmt = amtOwed - amtSettle;
            owed.put(splitsModel.getOwner(), Float.toString(finalAmt));
            ownersGet.put(user_id, Float.toString(finalAmt));
            user.setAmount_owe(owed);
            owner.setAmount_get(ownersGet);
        }
        userDao.save(user);
        userDao.save(owner);

        return Response.ok(user).build();

    }

    @PUT
    @Path("/update")
    @UnitOfWork
    public Response updateSplit(@Valid Splits splitsModel) {
        splitsModel.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        return Response.ok(splitDao.update(splitsModel)).build();
    }

    public String[] updateMemberSplit(String[] memberOfSplits, String splitId){
        String[] member_of_splits;
        if(memberOfSplits == null){
            member_of_splits = new String[0];
        }
        else{
            member_of_splits = memberOfSplits;
        }

        List<String> memberSplitsList = Arrays.asList(member_of_splits);
        List<String> arraylist = new ArrayList<>(memberSplitsList);
        arraylist.add(splitId);

        String k[] = arraylist.toArray(new String[arraylist.size()]);
        return k;
    }

}
