package com.mg.smartrent.user.resource;


import com.mg.smartrent.domain.models.BizItem;
import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/users", produces = APPLICATION_JSON_VALUE)
public class UsersRestController {

    private final UserService userService;

    public UsersRestController(UserService userService) {
        this.userService = userService;
    }


    @ApiOperation(value = "Create new user")
    @PostMapping
    public ResponseEntity<HashMap<String, Object>> createUser(@RequestBody User user) throws ModelValidationException {
        user = userService.create(user);

        HashMap<String, Object> body = new HashMap<>();
        body.put(BizItem.Fields.id, user.getId());
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Update existing user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = User.class),
            @ApiResponse(code = 404, message = "Not Found - provided user not found.", response = HttpStatus.class)})
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) throws ModelValidationException {
        if (!userService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.update(user));
    }


    @ApiOperation(value = "Find user by email")
    @GetMapping(params = "email")
    public ResponseEntity<Optional<User>> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }


    @ApiOperation(value = "Find user by Id")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable String id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }
}
