package com.resource.user;

import com.dao.split.SplitDao;
import com.dao.user.UserDao;
import static org.mockito.Mockito.mock;

import com.models.split.Splits;
import com.models.user.Users;
import com.resource.split.SplitResource;
import com.resource.user.UserResource;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Harshit Rajvaidya
 */


@ExtendWith(DropwizardExtensionsSupport.class)
public class UsersTest {

    private static final UserDao userDao = mock(UserDao.class);
    private static final SplitDao splitDao = mock(SplitDao.class);
    private static final ResourceExtension ext = ResourceExtension.builder()
            .addResource(new UserResource(userDao))
            .addResource(new SplitResource(splitDao))
            .build();

    private Users user;
    private Splits splits;
    private List<Users> usersList = new ArrayList<>();
    private String[] friends = {"u2"};

    @BeforeEach
    void setup(){
        user = new Users();
        user.setUser_id("u1");
        user.setFriends(friends);
        usersList.add(user);
    }

    @AfterEach
    void tearDown(){
        reset(userDao);
        reset(splitDao);
    }

    @Test
    public void getUserSuccess(){
        when(userDao.find("u1")).thenReturn(user);

        Users found = ext.target("/users/find")
                .queryParam("user_id", "u1")
                .request()
                .get(Users.class);

        assertThat(found.getUser_id()).isEqualTo(user.getUser_id());
        verify(userDao).find("u1");
    }

    @Test
    public void getUserNotFound(){
        when(userDao.find("user")).thenReturn(null);
        final Response response = ext.target("/users/find")
                .queryParam("user_id", "user")
                .request()
                .get();

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(userDao).find("user");

    }

    @Test
    public void getAllUsersSuccess(){
        when(userDao.findAll()).thenReturn(usersList);

        List<Users> users = ext.target("/users/findall")
                .request()
                .get(new GenericType<List<Users>>(){});

        assertThat(users.equals(usersList));
        verify(userDao).findAll();
    }




}
