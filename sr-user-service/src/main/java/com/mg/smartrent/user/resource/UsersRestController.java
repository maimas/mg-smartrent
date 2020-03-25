package com.mg.smartrent.user.resource;


import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/users")
public class UsersRestController {

    private final UserService userService;

    public UsersRestController(UserService userService) {
        this.userService = userService;
    }


    @ApiOperation(value = "Save user")
    @PostMapping
    public ResponseEntity<HttpStatus> saveUser(@RequestBody User user) throws ModelValidationException {
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
