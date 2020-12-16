package cn.edu.xmu.oomall;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.test.TestApplication;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SlyTest {

    private static final Logger logger = LoggerFactory.getLogger(SlyTest.class);

    @Autowired
    private ObjectMapper mObjectMapper;

    private WebTestClient webClient;

    private static String adminToken;
    private static String shopToken;

    public SlyTest(){
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://192.168.43.73:8006")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = webClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    @Test
    public void loginmy() throws Exception {
        logger.error(userLogin("8606245097", "123456"));
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
     * 正确增加三天后的秒杀
     * @throws Exception
     */
    @Test
    public void createflash1() throws Exception {
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = webClient.post().uri("/timesegments/0/flashsales").bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();
        logger.error("insertId = "+insertId);

        //恢复数据库
        responseBuffer = webClient.delete().uri("/flashsales/"+insertId).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 不允许增加过去和今天的秒杀
     * @throws Exception
     */
    @Test
    public void createflash2() throws Exception {
        //增加昨天的秒杀
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":9}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = webClient.post().uri("/timesegments/0/flashsales").bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo("需要是一个将来的时间;")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加不存在时段id的秒杀
     * @throws Exception
     */
    @Test
    public void createflash3() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":-1}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = webClient.post().uri("/timesegments/0/flashsales").bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加flashDate+segment_id已经存在的秒杀
     * @throws Exception
     */
    @Test
    public void createflash4() throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2021-12-17 11:25:58",df);
        //增加秒杀
        String requestJson = "{\"flashDate\":\""+ ldt.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = webClient.post().uri("/timesegments/0/flashsales").bodyValue(requestJson);

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = webClient.delete().uri("/flashsales/"+insertId).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale004
     * @throw Exception
     */
    @Test
    public void deleteflashsale1() throws Exception {

    }
}
