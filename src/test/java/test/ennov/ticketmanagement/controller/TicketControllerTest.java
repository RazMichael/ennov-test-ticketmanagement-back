package test.ennov.ticketmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.TicketStatusEnum;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.service.TicketServiceImpl;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoTicketAccessException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.mapper.TicketMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TicketServiceImpl ticketService;
    @MockBean
    private TicketMapper ticketMapper;
    private ObjectMapper objectMapper;
    private Ticket ticket;
    private TicketDTO ticketDTO;
    private List<Ticket> ticketList;
    private List<TicketDTO> ticketDtoList;
    private User user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ticket = new Ticket();
        ticketDTO = new TicketDTO();
        ticketList = new ArrayList<Ticket>();
        ticketDtoList = new ArrayList<TicketDTO>();

        user = new User();
        user.setId("userTestID1");
        user.setUsername("username1");
        user.setEmail("user1@user.com");

        Ticket ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("1");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 1");
        ticketTemp.setCreatedByUser(user);
        ticketList.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("2");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 2");
        ticketTemp.setCreatedByUser(user);
        ticketList.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("3");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 3");
        ticketTemp.setCreatedByUser(user);
        ticketList.add(ticketTemp);

        user.setTickets(ticketList);
        ticketDtoList = ticketMapper.mapListToDto(ticketList);

        ticket = ticketList.getFirst();
    }

    @Test
    void getAllTickets() throws Exception {
        given(ticketService.getAllElements()).willReturn(ticketList);

        ResultActions response = mockMvc.perform(get("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDtoList)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllTickets_noDataFoundException() throws Exception {
        given(ticketService.getAllElements()).willThrow(NoDataFoundException.class);

        ResultActions response = mockMvc.perform(get("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDtoList)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getTicketById() throws Exception {
        given(ticketService.getTicketById(any(), any())).willReturn(ticketList.getFirst());

        ResultActions response = mockMvc.perform(get("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketMapper.ticketToTicketDto(ticketList.getFirst())))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getTicketById_noDataFoundException() throws Exception {
        given(ticketService.getTicketById(any(), any())).willThrow(NoDataFoundException.class);

        ResultActions response = mockMvc.perform(get("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketMapper.ticketToTicketDto(ticketList.getFirst())))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getTicketById_noTicketAccessException() throws Exception {
        given(ticketService.getTicketById(any(), any())).willThrow(NoTicketAccessException.class);

        ResultActions response = mockMvc.perform(get("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketMapper.ticketToTicketDto(ticketList.getFirst())))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createNewTicket() throws Exception {
        given(ticketService.createElement(any())).willReturn(ticketList.getFirst());

        ResultActions response = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateTicket() throws Exception {
        given(ticketService.updateTicketById(any(), any())).willReturn(ticketList.getFirst());

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateTicket_noTicketAccessException() throws Exception {
        given(ticketService.updateTicketById(any(), any())).willThrow(NoTicketAccessException.class);

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void updateTicket_noDataFoundException() throws Exception {
        given(ticketService.updateTicketById(any(), any())).willThrow(NoDataFoundException.class);

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void assignTicketToUser() throws Exception {
        given(ticketService.assignTicketToUser(any(), any(), any())).willReturn(ticketList.getFirst());

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId() + "/assign/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO))
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void assignTicketToUser_UserNotFoundException() throws Exception {
        given(ticketService.assignTicketToUser(any(), any(), any())).willThrow(UserNotFoundException.class);

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId() + "/assign/" + user.getId())
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isNotModified());
    }

    @Test
    void assignTicketToUser_NoDataFoundException() throws Exception {
        given(ticketService.assignTicketToUser(any(), any(), any())).willThrow(NoDataFoundException.class);

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId() + "/assign/" + user.getId())
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isNotModified());
    }

    @Test
    void assignTicketToUser_NoTicketAccessException() throws Exception {
        given(ticketService.assignTicketToUser(any(), any(), any())).willThrow(NoTicketAccessException.class);

        ResultActions response = mockMvc.perform(put("/tickets/" + ticket.getId() + "/assign/" + user.getId())
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteTicketById() throws Exception {
        ResultActions response = mockMvc.perform(delete("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteTicketById_NoTicketAccessException() throws Exception {
        Mockito.doThrow(NoTicketAccessException.class).when(ticketService).deleteTicketById(any(), any());

        ResultActions response = mockMvc.perform(delete("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteTicketById_EmptyResultDataAccessException() throws Exception {
        Mockito.doThrow(EmptyResultDataAccessException.class).when(ticketService).deleteTicketById(any(), any());

        ResultActions response = mockMvc.perform(delete("/tickets/" + ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("connectedUserId", user.getId()));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}