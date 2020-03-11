package com.mg.smartrent.user.resource;


import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(value = "/rest/users")
public class UsersRestController {

    private final UserService userService;

    public UsersRestController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity saveUser(@RequestBody User user) throws ModelValidationException {
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(params = "email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<User> getUserByTrackingId(@PathVariable String trackingId) {
        return new ResponseEntity<>(userService.findByTrackingId(trackingId), HttpStatus.OK);
    }

    @GetMapping(params = "exists")
    public ResponseEntity<Boolean> existsByTrackingId(@RequestParam("exists") String trackingId) {
        return ResponseEntity.ok(userService.findByTrackingId(trackingId) != null);
    }


}
