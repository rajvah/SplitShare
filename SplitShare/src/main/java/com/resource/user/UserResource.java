package com.resource.user;

import com.dao.split.SplitDao;
import com.dao.user.UserDao;
import com.models.split.NetSplits;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Harshit Rajvaidya
 */

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    public UserDao userdao;
    public SplitDao splitDao;

    public UserResource(UserDao userdao) {
        this.userdao = userdao;
    }

    public UserResource(UserDao userdao, SplitDao splitDao) {
        this.userdao = userdao;
        this.splitDao = splitDao;
    }

    @POST
    @Path("/new")
    @UnitOfWork
    public Response addUser(@Valid Users usersModel) {
        usersModel.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));

        Users user = userdao.find(usersModel.getUser_id());
        if (user != null) {
            return Response.status(Response.Status.CONFLICT).entity("Unable to create a new user as the username "
                    + usersModel.getUser_id() + " already exists").type("text/plain").build();
        }

        Users userNew = userdao.save(usersModel);
        if (userNew == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to create a new user").type("text/plain").build();
        }
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Path("/find")
    @UnitOfWork
    public Response getUser(@QueryParam("user_id") String userid) {
        Users user = userdao.find(userid);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User: " + userid + " not found").type("text/plain").build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/findall")
    @UnitOfWork
    public Response getAllUsers() {
        List<Users> users = userdao.findAll();
        if (users == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Users not found").type("text/plain").build();
        }
        return Response.ok(users).build();
    }

    @PUT
    @Path("/addfriend")
    @UnitOfWork
    public Response addFriend(@Valid Users usersModel, @QueryParam("user_id") String user_id) {

        String friendEmail = usersModel.getEmail();
        // validate user with email
        Users friend = userdao.usersModelFromEmail(friendEmail);
        if (friend == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User with the email: " + friendEmail + " does not exist").type("text/plain").build();
        }

        Users user = userdao.find(user_id);
        String[] friends;
        if (user.getFriends() == null) {
            friends = new String[0];
        } else {
            friends = user.getFriends();
        }

        List<String> friendList = Arrays.asList(friends);
        List<String> arraylist = new ArrayList<>(friendList);
        arraylist.add(friend.getUser_id());

        String k[] = arraylist.toArray(new String[arraylist.size()]);
        user.setFriends(k);

        if (friend.getFriends() == null) {
            friends = new String[0];
        } else {
            friends = friend.getFriends();
        }

        friendList = Arrays.asList(friends);
        arraylist = new ArrayList<>(friendList);
        arraylist.add(user.getUser_id());

        friend.setFriends(arraylist.toArray(new String[arraylist.size()]));
        if (userdao.save(user) == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error with adding friend for user " + user_id).type("text/plain").build();
        }
        if (userdao.save(friend) == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error with adding friend for user " + user_id).type("text/plain").build();
        }

        return Response.ok(user).build();
    }

    @GET
    @Path("/getfriends")
    @UnitOfWork
    public Response getFriends(@QueryParam("user_id") String user_id) {
        Users user = userdao.find(user_id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User with " + user_id + " Not Found").type("text/plain").build();
        }
        if (user.getFriends() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No friends found for the User with " + user_id + " Not Found").type("text/plain").build();
        }

        if (user.getFriends().length < 1) {
            return Response.status(Response.Status.NOT_FOUND).entity("No friends for User " + user_id + " Found").type("text/plain").build();
        }
        return Response.ok(user.getFriends()).build();
    }

    @GET
    @Path("/getsplitsowned")
    @UnitOfWork
    public Response getOwnedSplits(@QueryParam("user_id") String user_id) {
        Users user = userdao.find(user_id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "User not found").build();
        }
        if (user.getOwned_splits() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity( "User does not have any expenses").build();
        }
        ArrayList<Splits> splits = new ArrayList<>();
        for (String split : user.getOwned_splits()) {
            Splits splitsModel = splitDao.find(UUID.fromString(split));
            splits.add(splitsModel);
        }
        if (splits.size() < 1) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "No owned splits found").build();
        }
        return Response.ok(splits).build();
    }

    @GET
    @Path("/getsplitsmember")
    @UnitOfWork
    public Response getMemberSplits(@QueryParam("user_id") String user_id) {
        Users user = userdao.find(user_id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User Not Found").type("text/plain").build();
        }
        if (user.getMember_of_splits() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity( "User does not have any expenses").build();
        }
        ArrayList<Splits> splits = new ArrayList<>();
        for (String split : user.getMember_of_splits()) {
            Splits splitsModel = splitDao.find(UUID.fromString(split));
            if (splitsModel == null) {
                continue;
            }
            splits.add(splitsModel);
        }
        if (splits.size() < 1) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Splits Found").type("text/plain").build();
        }
        return Response.ok(splits).build();
    }

    @GET
    @Path("/getnetsplit")
    @UnitOfWork
    public Response getNetSplit(@QueryParam("user_id") String user_id) {
        Users user = userdao.find(user_id);

        AtomicReference<Float> amtG = new AtomicReference<>((float) 0);
        AtomicReference<Float> amtO = new AtomicReference<>((float) 0);

        Map<String, String> amtGet;
        if (user.getAmount_get() != null) {
            amtGet = user.getAmount_get();
        } else {
            amtGet = new HashMap<>();
        }

        Map<String, String> amtOwe;
        if (user.getAmount_owe() != null) {
            amtOwe = user.getAmount_owe();
        } else {
            amtOwe = new HashMap<>();
        }

        //getting all users with a split for the given user
        Set<String> allUsers = new HashSet<>();
        amtGet.forEach((k, v) -> {
            allUsers.add(k);
            amtG.updateAndGet(v1 -> v1 + Float.parseFloat(v));
        });
        amtOwe.forEach((k, v) -> {
            allUsers.add(k);
            amtO.updateAndGet(v1 -> v1 + Float.parseFloat(v));
        });

        Map<String, String> userSplitDetails = new HashMap<>();
        for (String users : allUsers) {

            float net = Float.parseFloat(amtGet.getOrDefault(users, "0.0")) - Float.parseFloat(amtOwe.getOrDefault(users, "0.0"));
            userSplitDetails.put(users, Float.toString(net));
        }

        NetSplits netSplit = new NetSplits(amtG.get().toString(), amtO.get().toString(), userSplitDetails);

        return Response.ok(netSplit).build();

    }

    @DELETE
    @Path("/remove")
    @UnitOfWork
    public Response removeUser(@QueryParam("user_id") String user_id) {
        Users user = userdao.find(user_id);
        userdao.deleteUser(user);
        return Response.ok(user).build();
    }
}
