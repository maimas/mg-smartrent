package com.mg.smartrent.renter.service;


import com.mg.smartrent.renter.config.RestServicesConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Service
@Validated
public class UserService {

    private RestServicesConfig restServicesConfig;
    private RestTemplate restTemplate;

    public UserService(RestServicesConfig restServicesConfig, RestTemplate restTemplate) {
        this.restServicesConfig = restServicesConfig;
        this.restTemplate = restTemplate;
    }

    public boolean userExists(@NotNull @NotBlank String userTID) {
        URI uri = URI.create(restServicesConfig.getUsersServiceURI() + "/rest/users/exists/trackingId=" + userTID);
        ResponseEntity response = restTemplate.getForEntity(uri, ResponseEntity.class);

        return response.getStatusCode() == HttpStatus.OK;
    }
}
