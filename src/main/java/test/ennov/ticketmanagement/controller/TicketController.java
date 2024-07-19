package test.ennov.ticketmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.service.TicketService;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoTicketAccessException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.mapper.TicketMapper;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Ticket API")
public class TicketController {

    Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketMapper ticketMapper;

    /**
     * Get the full list of tickets
     * @return List<TicketDTO> : full list of tickets
     */
    @Operation(
            description = "Get all tickets in database",
            summary = "Get all tickets",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "No ticket(s) Found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        List<TicketDTO> tickets = null;
        try {
            tickets = ticketMapper.mapListToDto(ticketService.getAllElements());
            return new ResponseEntity<List<TicketDTO>>(tickets, HttpStatus.OK);
        } catch (NoDataFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<List<TicketDTO>>(tickets, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            description = "Get a ticket by ID - Only the ticket creator or the assignee can request",
            summary = "Get a ticket by ID",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "No ticket Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Forbidden - Only assignee and creator can access it",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable("id") String ticketId, @RequestParam("connectedUserId") String connectedUserId) {
        Ticket ticket = null;
        try {
            ticket = ticketService.getTicketById(ticketId, connectedUserId);
            return new ResponseEntity<TicketDTO>(ticketMapper.ticketToTicketDto(ticket), HttpStatus.OK);
        } catch (NoDataFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NoTicketAccessException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Create new ticket
     * @param ticketDto ticket to create
     * @return TicketDTO : created ticket
     */
    @Operation(
            description = "Create a new ticket",
            summary = "Create a new ticket",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Internal server error - Not created",
                            responseCode = "500"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TicketDTO> createNewTicket(@RequestBody TicketDTO ticketDto) {
        try {
            Ticket newTicket = ticketMapper.ticketDtoToTicket(ticketDto);
            newTicket = ticketService.createElement(newTicket);
            return new ResponseEntity<TicketDTO>(ticketMapper.ticketToTicketDto(newTicket), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Ticket not created : {}", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing ticket
     * @param ticketId ticket id to update
     * @param ticketDto new ticket infos for update
     * @param connectedUserId id of the user performing the request
     * @return TicketDTO : ticket updated
     */
    @Operation(
            description = "Update a ticket by ID - Only the ticket creator or the assignee can edit it",
            summary = "Update a ticket by ID",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "New assignee not found or ticket not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Forbidden - Only assignee and creator can access it",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(
            @PathVariable("id") String ticketId,
            @RequestBody TicketDTO ticketDto,
            @RequestParam("connectedUserId") String connectedUserId
    ) {
        ticketDto.setId(ticketId);
        Ticket ticket = null;
        try {
            ticket = ticketMapper.ticketDtoToTicket(ticketDto);
            ticket = ticketService.updateTicketById(ticket, connectedUserId);
            return new ResponseEntity<TicketDTO>(ticketMapper.ticketToTicketDto(ticket), HttpStatus.OK);
        } catch (NoDataFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NoTicketAccessException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Assign a ticket to another user
     * @param ticketId id of the ticket to assign
     * @param userId new ticket assignee
     * @param connectedUserId id of the user performing the request
     * @return TicketDTO : the ticket assigned
     */
    @Operation(
            description = "Assign a ticket to a user - Only the ticket creator or the assignee can assign",
            summary = "Assign a ticket to a user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "New assignee not found or ticket not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Forbidden - Only assignee and creator can access it",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<?> assignTicketToUser(
            @PathVariable("id") String ticketId,
            @PathVariable("userId") String userId,
            @RequestParam("connectedUserId") String connectedUserId
    ) {
        try {
            Ticket ticketUpdated = ticketService.assignTicketToUser(ticketId, userId, connectedUserId);
            return new ResponseEntity<TicketDTO>(ticketMapper.ticketToTicketDto(ticketUpdated), HttpStatus.OK);
        } catch (UserNotFoundException | NoDataFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (NoTicketAccessException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Delete a ticket by its ID
     * @param ticketId the ID of the ticket to delete
     * @param connectedUserId id of the user performing the request
     * @return
     */
    @Operation(
            description = "Delete a ticket by ID - Only the ticket creator or the assignee can delete it",
            summary = "Delete a ticket by ID",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal server error",
                            responseCode = "500"
                    ),
                    @ApiResponse(
                            description = "Forbidden - Only assignee and creator can access it",
                            responseCode = "403"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTicketById(
            @PathVariable("id") String ticketId,
            @RequestParam("connectedUserId") String connectedUserId
    ) {
        try {
            ticketService.deleteTicketById(ticketId, connectedUserId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoTicketAccessException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<Boolean>(false, HttpStatus.FORBIDDEN);
        }
    }
}
