package ru.netology.cloudstorage.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthToken {

    private String authToken;

    @JsonCreator
    public AuthToken(@JsonProperty("auth-token") String authToken) {
        this.authToken = authToken;
    }

    @JsonGetter
    public String getAuthToken() {
        return authToken;
    }

}
