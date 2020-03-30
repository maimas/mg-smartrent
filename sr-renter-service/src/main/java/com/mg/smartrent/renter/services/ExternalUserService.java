package com.mg.smartrent.renter.services;


import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.renter.config.RestServicesConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Service
@Validated
public class ExternalUserService {

    private RestServicesConfig restServicesConfig;
    private RestTemplate restTemplate;

    public ExternalUserService(RestServicesConfig restServicesConfig, RestTemplate restTemplate) {
        this.restServicesConfig = restServicesConfig;
        this.restTemplate = restTemplate;
    }

    public Boolean userExists(@NotNull @NotBlank String userId) {
        URI uri = URI.create(restServicesConfig.getUsersServiceURI() + "/rest/users/" + userId);
        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);

        return response.getBody() != null;
    }

    public User getUserByEmail(@NotNull @NotBlank @Email String email) {
        URI uri = URI.create(restServicesConfig.getUsersServiceURI() + "/rest/users?email=" + email);
        ResponseEntity<User> response = restTemplate.getForEntity(uri, User.class);

        return response.getBody();
    }
}
