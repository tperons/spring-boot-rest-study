package com.tperons.controller.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.dto.UploadFileResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File", description = "Endpoints to Manage Files")
@ApiResponses({
        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
        @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content) })
public interface FileControllerDocs {

    @Operation(summary = "Downloads a File", description = "Downloads a specific file by its name.", tags = {
            "File" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content) })
    ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Name of the file to be downloaded") String fileName,
            @Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "Uploads a File", description = "Uploads a single file to the server.", tags = {
            "File" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UploadFileResponseDTO.class))) })
    UploadFileResponseDTO uploadFile(
            @Parameter(description = "File to be uploaded") MultipartFile file);

    @Operation(summary = "Uploads Multiple Files", description = "Uploads multiple files at once to the server.", tags = {
            "File" }, responses = { @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UploadFileResponseDTO.class))) }) })
    List<UploadFileResponseDTO> uploadMultipleFiles(
            @Parameter(description = "Array of files to be uploaded") MultipartFile[] files);

}
