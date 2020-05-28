package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class WhoAmIDto {
    private String authId;
    private boolean hasAccount;
    private boolean isAdmin;
    private Long userId;
}
