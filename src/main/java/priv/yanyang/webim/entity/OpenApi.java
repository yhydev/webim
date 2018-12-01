package priv.yanyang.webim.entity;

public class OpenApi {

    private String openApiId;
    private String apiSecret;
    private String apiKey;
    //private Integer createTime;


    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getOpenApiId() {
        return openApiId;
    }

    public void setOpenApiId(String openApiId) {
        this.openApiId = openApiId;
    }

    @Override
    public String toString() {
        return "OpenApi{" +
                "openApiId='" + openApiId + '\'' +
                ", apiSecret='" + apiSecret + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
