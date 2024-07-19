package test.ennov.ticketmanagement.model.dto;

import lombok.Data;
import test.ennov.ticketmanagement.model.entity.TicketStatusEnum;

@Data
public class TicketDTO {
    private String id;
    private String title;
    private String description;
    private String userId;
    private TicketStatusEnum status;
}
