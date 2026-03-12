package com.tperons.controller.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tperons.dto.PersonDTO;
import com.tperons.file.exporter.MediaTypes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "People", description = "Endpoints to Manage People")
@ApiResponses({
        @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
        @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
        @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
        @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content) })
public interface PersonControllerDocs {

    @Operation(summary = "Finds all People", description = "Returns a list with all people.", tags = {
            "People" }, responses = { @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class))) }), })
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
            @Parameter(description = "Page number") Integer page,
            @Parameter(description = "Page size") Integer size,
            @Parameter(description = "Ordering direction") String direction,
            @Parameter(hidden = true) PagedResourcesAssembler<PersonDTO> assembler);

    @Operation(summary = "Finds a Person by ID", description = "Returns a specific person by their ID.", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content), })
    ResponseEntity<PersonDTO> findById(@Parameter(description = "ID of person to be found") Long id);

    @Operation(summary = "Finds People by First Name", description = "Returns a list of people by their first name.", tags = {
            "People" }, responses = { @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class))) }), })
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
            @Parameter(description = "First Name") String firstName,
            @Parameter(description = "Page number") Integer page,
            @Parameter(description = "Page size") Integer size,
            @Parameter(description = "Ordering direction") String direction,
            @Parameter(hidden = true) PagedResourcesAssembler<PersonDTO> assembler);

    @Operation(summary = "Export Person", description = "Export a  specific person data as PDF by their ID.", tags = {
            "People" }, responses = { @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE) }),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content) })
    ResponseEntity<Resource> exportPdf(@Parameter(description = "ID of person to be found") Long id, @Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "Export People", description = "Export a page of people in CSV, PDF and XLSX format.", tags = {
            "People" }, responses = { @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE),
                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE),
                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE) }), })
    ResponseEntity<Resource> exportPage(
            @Parameter(description = "Page number") Integer page,
            @Parameter(description = "Page size") Integer size,
            @Parameter(description = "Ordering direction") String direction,
            @Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "Adds a New Person", description = "Adds a new person by passing in a JSON, XML or YML representation of the person.", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = PersonDTO.class))), })
    ResponseEntity<PersonDTO> create(@RequestBody(description = "Person data to create") PersonDTO obj);

    @Operation(summary = "Massive People Creation", description = "Massive people creation with upload of XLSX or CSV", tags = {
            "People" }, responses = { @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = PersonDTO.class)) }), })
    ResponseEntity<List<PersonDTO>> massCreation(
            @Parameter(description = "XLSX or CSV file containing people data") MultipartFile file);

    @Operation(summary = "Updates a Person's Information", description = "Updates a person's information by passing in a JSON, XML or YML representation of the updated person.", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content), })
    ResponseEntity<PersonDTO> update(@Parameter(description = "ID of the person to be updated") Long id,
            @RequestBody(description = "Updated person data") PersonDTO obj);

    @Operation(summary = "Deletes a Person", description = "Deletes a specific person by their ID", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content), })
    ResponseEntity<Void> delete(@Parameter(description = "ID of the person to be deleted") Long id);

    @Operation(summary = "Disables a Person", description = "Disable a Specific Person by their ID", tags = {
            "People" }, responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content), })
    ResponseEntity<PersonDTO> disablePerson(@Parameter(description = "ID of the person to be disabled") Long id);

}
