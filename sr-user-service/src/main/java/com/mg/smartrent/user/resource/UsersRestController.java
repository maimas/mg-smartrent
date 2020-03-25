package com.mg.smartrent.user.resource;


import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/users", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class UsersRestController {

    private final UserService userService;

    public UsersRestController(UserService userService) {
        this.userService = userService;
    }


    @ApiOperation(value = "Save user")
    @PostMapping
    public ResponseEntity<String> saveUser(@RequestBody User user) throws ModelValidationException {
        user = userService.save(user);
        return new ResponseEntity<>(user.getTrackingId(), HttpStatus.CREATED);
    }


    @ApiOperation(value = "Update user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = User.class),
            @ApiResponse(code = 400, message = "Bad Request - provided trackingId is not matching user.trackingId from the request body", response = HttpStatus.class),
            @ApiResponse(code = 404, message = "Not Found - provided user not found in the database", response = HttpStatus.class)})
    @PutMapping("/{trackingId}")
    public ResponseEntity<User> updateUser(@PathVariable String trackingId, @RequestBody User user) throws ModelValidationException {

        if (!Objects.equals(trackingId, user.getTrackingId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userService.findByTrackingId(trackingId) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.update(user));
    }


    @ApiOperation(value = "Find user by email")
    @GetMapping(params = "email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }


    @ApiOperation(value = "Find user by trackingId")
    @GetMapping("/{trackingId}")
    public ResponseEntity<User> getUserByTrackingId(@PathVariable String trackingId) {
        return new ResponseEntity<>(userService.findByTrackingId(trackingId), HttpStatus.OK);
    }
}
