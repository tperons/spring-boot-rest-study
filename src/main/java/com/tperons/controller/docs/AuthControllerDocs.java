package com.tperons.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tperons.dto.security.AccountCredentialsDTO;
import com.tperons.dto.security.TokenDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authorization", description = "Endpoints to manage authorizations")
@ApiResponses({
        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
        @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content) })
public interface AuthControllerDocs {

    @Operation(summary = "Adds a New User", description = "Creates a new user account.", tags = {
            "Authorization" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = AccountCredentialsDTO.class))), })
    ResponseEntity<AccountCredentialsDTO> create(@RequestBody AccountCredentialsDTO credentials);

    @Operation(summary = "Authenticates a User", description = "Authenticates a user by username and password, returning a JWT access token and a refresh token.", tags = {
            "Authorization" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TokenDTO.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content) })
    ResponseEntity<?> signin(
            @RequestBody(description = "User credentials containing username and password") AccountCredentialsDTO credentialsDTO);

    @Operation(summary = "Refreshes a JWT Token", description = "Generates a new access token for the given username using a valid Bearer refresh token.", tags = {
            "Authorization" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TokenDTO.class))),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content) })
    ResponseEntity<?> refreshtoken(
            @Parameter(description = "Bearer refresh token", example = "Bearer eyJ...") @RequestHeader(value = "Authorization") String refreshToken);

}
