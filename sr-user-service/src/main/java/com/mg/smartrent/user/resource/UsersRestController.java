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
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(params = "email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }

    @GetMapping(params = "trackingId")
    public ResponseEntity<User> getUserByTrackingId(@RequestParam String trackingId) {
        return new ResponseEntity<>(userService.findByTrackingId(trackingId), HttpStatus.OK);
    }

    /**
     * Check is the user exists by TrackingID.
     *
     * @param trackingId - user TrackingID
     * @return - HttpStatus.OK (200) with empty body if the user is present else HttpStatus.NOT_FOUND (404).
     */
    @GetMapping(value = "/exists/{trackingId}")
    public ResponseEntity existsByTrackingId(@PathVariable String trackingId) {

        if (userService.findByTrackingId(trackingId) == null) {
            return new ResponseEntity<>("No such user.", NOT_FOUND);
        }
        return ResponseEntity.ok(null);
    }


}
