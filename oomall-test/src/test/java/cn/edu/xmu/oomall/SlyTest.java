package cn.edu.xmu.oomall;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.test.TestApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(classes = TestApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SlyTest {

//    @Autowired
//    private ObjectMapper mObjectMapper;

    private WebTestClient webClient;

    private static String adminToken;
    private static String shopToken;

    public SlyTest(){
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    @BeforeAll
    private static void login(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken =jwtHelper.createToken(1L,0L, 3600);
        shopToken =jwtHelper.createToken(59L,1L, 3600);
    }

    /**
     * Flashsale001
     * @throws Exception
     */
    @Test
    public void queryTopicsByTime1() throws Exception {

    }

    /**
     * Flashsale003
     * @throws Exception
     */
    @Test
    public void getCurrentflash1() throws Exception {

    }

    /**
     * Flashsale002
     * 今天之前的秒杀-不许加
     * @throws Exception
     */
    @Test
    public void createflash() throws Exception {
        //增加昨天的秒杀
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":1}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = webClient.post().uri("/timesegments/0/flashsales").bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
//        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = 1;//jsonNode.asInt();

        //恢复数据库
        responseBuffer = webClient.delete().uri("/flashsales/"+insertId).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

}