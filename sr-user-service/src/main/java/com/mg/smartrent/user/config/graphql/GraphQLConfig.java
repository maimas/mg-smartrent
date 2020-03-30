//package com.mg.smartrent.user.config.graphql;
//
//import com.mg.smartrent.user.service.UserGraphQLService;
//import com.mg.smartrent.user.service.UserService;
//import graphql.GraphQL;
//import graphql.analysis.MaxQueryComplexityInstrumentation;
//import graphql.analysis.MaxQueryDepthInstrumentation;
//import graphql.execution.AsyncExecutionStrategy;
//import graphql.execution.instrumentation.ChainedInstrumentation;
//import graphql.schema.GraphQLSchema;
//import io.leangen.graphql.GraphQLSchemaGenerator;
//import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
//import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//
//@Configuration
//@Log4j2
//public class GraphQLConfig {
//
//    @Bean
//    public GraphQL graphQL(UserGraphQLService userService) {
//        log.info("Initializing GraphQL schemas...");
//
//        GraphQLSchema schema = new GraphQLSchemaGenerator()
//                .withResolverBuilders(new AnnotatedResolverBuilder())
//                .withOperationsFromSingleton(userService, UserService.class)
//                .withValueMapperFactory(new JacksonValueMapperFactory())
//                .generate();
//
//        return GraphQL.newGraphQL(schema)
//                .queryExecutionStrategy(
//                        new AsyncExecutionStrategy()).instrumentation(new ChainedInstrumentation(Arrays.asList(
//                        new MaxQueryComplexityInstrumentation(200),
//                        new MaxQueryDepthInstrumentation(20)))
//                )
//                .mutationExecutionStrategy(
//                        new AsyncExecutionStrategy()).instrumentation(new ChainedInstrumentation(Arrays.asList(
//                        new MaxQueryComplexityInstrumentation(200),
//                        new MaxQueryDepthInstrumentation(20)))
//                )
//                .build();
//    }
//}
