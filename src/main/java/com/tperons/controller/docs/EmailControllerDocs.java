package com.tperons.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.dto.request.EmailRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "e-Mail", description = "Endpoints to Manage e-Mails")
@ApiResponses({
        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
        @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content) })
public interface EmailControllerDocs {

    @Operation(summary = "Send an e-Mail", description = "Sends an e-mail by providing datails, subject and body.", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = { @Content() }),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content) })
    ResponseEntity<String> sendEmail(EmailRequestDTO emailRequestDTO);

    @Operation(summary = "Send an e-Mail with Attachment", description = "Sends an e-mail with attachment by providing datails, subject and body.", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = { @Content() }),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content) })
    ResponseEntity<String> sendEmailWithAttachment(String emailRequestJson, MultipartFile multipartFile);

}
