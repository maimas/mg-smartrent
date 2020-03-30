package com.mg.smartrent.user.service;

import com.mg.smartrent.domain.models.BizItem;
import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.leangen.graphql.spqr.spring.annotations.WithResolverBuilder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Optional;

@Service
@GraphQLApi
@WithResolverBuilder(AnnotatedResolverBuilder.class)
public class UserGraphQLService {

    private UserService userService;

    public UserGraphQLService(UserService userService) {
        this.userService = userService;
    }


    @GraphQLMutation
    public User create(@GraphQLArgument(name = "user") @NotNull User user) throws ModelValidationException {
        return userService.create(user);
    }

    @GraphQLMutation
    public User update(@GraphQLArgument(name = "user") @NotNull User user) throws ModelValidationException {
        return userService.update(user);
    }

    @GraphQLMutation
    public User enable(@GraphQLArgument(name = "id") @NotEmpty String id,
                       @GraphQLArgument(name = "enable") Boolean enable) throws ModelValidationException {
        return userService.enable(id, enable);
    }

    @GraphQLMutation
    public boolean resetPassword(@GraphQLArgument(name = "id") @NotEmpty String id,
                                 @GraphQLArgument(name = "rawPassword") String rawPassword) throws ModelValidationException {
        return userService.resetPassword(id, rawPassword) != null;
    }

    @GraphQLQuery
    public Optional<User> findById(@GraphQLArgument(name = BizItem.Fields.id) @NotNull String id) {
        return userService.findById(id);
    }

    @GraphQLQuery
    public Optional<User> findByEmail(@GraphQLArgument(name = User.Fields.email) @NotNull String email) {
        return userService.findByEmail(email);
    }
}
