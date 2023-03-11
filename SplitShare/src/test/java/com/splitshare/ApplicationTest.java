package com.splitshare;

import com.config.SplitShareConfiguration;
import com.google.gson.Gson;
import com.models.split.Splits;
import com.models.user.Users;
import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author Harshit Rajvaidya
 */

@ExtendWith(DropwizardExtensionsSupport.class)
public class ApplicationTest {

    private static Users user1 = new Users();
    private static Users user2 = new Users();
    private static Splits split = new Splits();

    @BeforeEach
    void setup(){
        user1.setUser_id("user1");
        user1.setFirst_name("user");
        user1.setLast_name("1");
        user1.setEmail("user1@user.com");

        user2.setUser_id("user2");
        user2.setFirst_name("user");
        user2.setLast_name("2");
        user2.setEmail("user2@user.com");

        split.setOwner("user1");
        split.setAmount("25");
        split.setMembers(new String[]{"user2"});


    }


    @Test
    void UserCreationSuccess() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user1)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://127.0.0.1:8080/users/new"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(201);

        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user2)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://127.0.0.1:8080/users/new"))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertThat(response1.statusCode()).isEqualTo(201);
    }

    @Test
    void UserCreationFailure() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user1)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://127.0.0.1:8080/users/new"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(409);
    }

    @Test
    void GetUserSuccess() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/users/find?user_id=user1"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

    }

    @Test
    void GetUserFailure() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/users/find?user_id=user3"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    void SplitCreationSuccess() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(split)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/split/new"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void SplitCreationFailure() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        split.setOwner("user3");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(split)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/split/new"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    void NetSplitGetSuccess() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/users/getnetsplit?user_id=user1"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void NetSplitGetFailure() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/users/getnetsplit?user_id=1"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    void SettleUpSuccess() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();
        Splits s = new Splits();
        s.setOwner("user1");
        s.setAmount("12.5");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(s)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/split/payment?user_id=user2"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void SettleUpFailure() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(1000))
                .build();
        Splits s = new Splits();
        s.setOwner("user1");
        s.setAmount("12.5");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(s)))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/split/payment?user_id=u1"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
    }


}
