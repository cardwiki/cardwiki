package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class OAuth2ProviderDto {
    private String id;
    private String name;

    public OAuth2ProviderDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
