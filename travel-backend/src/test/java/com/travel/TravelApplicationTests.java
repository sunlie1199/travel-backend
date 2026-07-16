package com.travel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.module.auth.dto.LoginRequest;
import com.travel.module.destination.dto.DestinationRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TravelApplicationTests {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("travel")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> mysql.getJdbcUrl() + "?allowMultiQueries=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:db/schema.sql");
        registry.add("spring.sql.init.continue-on-error", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static String destinationId;

    @Test
    @Order(1)
    @DisplayName("登录接口 - 使用默认账号登录成功")
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("data").get("token").asText();
        Assertions.assertNotNull(token, "Token不应为空");
    }

    @Test
    @Order(2)
    @DisplayName("登录接口 - 错误密码登录失败")
    void testLoginFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(3)
    @DisplayName("新增目的地 - 需要认证")
    void testCreateDestination() throws Exception {
        DestinationRequest request = buildDestinationRequest("male");

        String response = mockMvc.perform(post("/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("昆明"))
                .andExpect(jsonPath("$.data.region").value("云南"))
                .andReturn().getResponse().getContentAsString();

        destinationId = objectMapper.readTree(response).get("data").get("id").asText();
    }

    @Test
    @Order(4)
    @DisplayName("新增目的地 - 未认证拒绝访问")
    void testCreateDestinationUnauthorized() throws Exception {
        DestinationRequest request = buildDestinationRequest("male");

        mockMvc.perform(post("/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @DisplayName("获取所有目的地")
    void testGetAllDestinations() throws Exception {
        mockMvc.perform(get("/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("昆明"))
                .andExpect(jsonPath("$.data[0].coordinates").exists());
    }

    @Test
    @Order(6)
    @DisplayName("按地区筛选目的地")
    void testGetDestinationsByRegion() throws Exception {
        mockMvc.perform(get("/destinations?region=云南"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].region").value("云南"));
    }

    @Test
    @Order(7)
    @DisplayName("获取单个目的地")
    void testGetDestinationById() throws Exception {
        mockMvc.perform(get("/destinations/" + destinationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("昆明"));
    }

    @Test
    @Order(8)
    @DisplayName("获取不存在的目的地")
    void testGetDestinationNotFound() throws Exception {
        mockMvc.perform(get("/destinations/nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(9)
    @DisplayName("更新目的地")
    void testUpdateDestination() throws Exception {
        DestinationRequest request = buildDestinationRequest("both");
        request.setName("昆明（更新）");

        mockMvc.perform(put("/destinations/" + destinationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("昆明（更新）"));
    }

    @Test
    @Order(10)
    @DisplayName("获取旅行者资料")
    void testGetTravelerProfile() throws Exception {
        mockMvc.perform(get("/travelers/male"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("male"))
                .andExpect(jsonPath("$.data.destinations").isArray());
    }

    @Test
    @Order(11)
    @DisplayName("获取旅行统计数据")
    void testGetStats() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sharedCount").exists())
                .andExpect(jsonPath("$.data.totalRegions").exists());
    }

    @Test
    @Order(12)
    @DisplayName("获取共同足迹")
    void testGetSharedFootprints() throws Exception {
        mockMvc.perform(get("/shared"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(13)
    @DisplayName("删除目的地")
    void testDeleteDestination() throws Exception {
        mockMvc.perform(delete("/destinations/" + destinationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private DestinationRequest buildDestinationRequest(String owner) {
        DestinationRequest request = new DestinationRequest();
        request.setName("昆明");
        request.setRegion("云南");
        request.setCountry("中国");
        request.setStatus("visited");
        request.setDescription("春城昆明，四季如春，是中国云南省的省会城市。");
        request.setHighlights(Arrays.asList("滇池观鸥", "西山龙门", "翠湖公园"));
        request.setBestSeason("3月–10月");
        request.setLatitude(new BigDecimal("25.0389"));
        request.setLongitude(new BigDecimal("102.7183"));
        request.setRating(new BigDecimal("4.5"));
        request.setTags(Arrays.asList("春城", "少数民族文化", "自然风光"));
        request.setOwner(owner);
        return request;
    }
}
