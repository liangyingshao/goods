package cn.edu.xmu.oomall;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.test.TestApplication;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Pinzhen Chen 24320182203173
 * @Date 2020/12/13 20:40
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TestApplication.class)
public class ChenPinzhenTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;
    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
        System.out.println(mallGate);
    }


    private String userLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    //200: 成功
    @Test
    @Order(11)
    public void insertFloatPrice1() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime begin = LocalDateTime.parse("2021-12-14 03:00:52",df);
        LocalDateTime end = LocalDateTime.parse("2021-12-24 03:00:52",df);
        String json = "{\n" +
                "  \"activityPrice\": 10,\n" +
                "  \"beginTime\": \""+begin+"\",\n" +
                "  \"endTime\": \""+end+"\",\n" +
                "  \"quantity\": 1\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/273/floatPrices")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
//                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    //902: 浮动价格时间冲突
    @Test
    @Order(12)
    public void insertFloatPrice2() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String json = "{\n" +
                "  \"activityPrice\": 10,\n" +
                "  \"beginTime\": \"2021-12-12T02:23:01\",\n" +
                "  \"endTime\": \"2021-12-16T02:23:01\",\n" +
                "  \"quantity\": 1\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/273/floatPrices")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SKUPRICE_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 902\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    //404: skuId不存在
    @Test
    @Order(13)
    public void insertFloatPrice3() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        String json = "{\n" +
                "  \"activityPrice\": 10,\n" +
                "  \"beginTime\": \"2022-12-12T02:23:01\",\n" +
                "  \"endTime\": \"2022-12-16T02:23:01\",\n" +
                "  \"quantity\": 1\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/2/floatPrices")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 管理员失效商品价格浮动
     *
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/14 9:35
     */

    //200: 成功
    @Test
    @Order(14)
    public void deleteFloatPrice1() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/1/floatPrices/828")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    //404: skuId不存在或浮动价格不存在
    @Test
    @Order(15)
    public void deleteFloatPrice3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/0/floatPrices/20010")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

}
