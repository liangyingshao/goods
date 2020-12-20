package cn.edu.xmu.oomall;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.test.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = TestApplication.class)
public class XiangSuXianTest {

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

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    //查询评论状态
    @Test
    @Order(5)
    public void getCommentStates() throws Exception {
        byte[] responseString=mallClient.get().uri("/comments/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"未审核\",\n" +
                "      \"code\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"评论成功\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未通过\",\n" +
                "      \"code\": 2\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    //查看sku的评价列表（已通过审核）
    @Test
    @Order(7)
    public void getComments() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus/273/comments")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"customer\": {\n" +
                "          \"id\": 1,\n" +
                "          \"userName\": \"8606245097\",\n" +
                "          \"name\": \"16886485849\"\n" +
                "        },\n" +
                "        \"goodsSkuId\": 273,\n" +
                "        \"type\": 2,\n" +
                "        \"content\": \"挺好的\",\n" +
                "        \"state\": 1,\n" +
                "        \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "        \"gmtModified\": \"2020-12-10T22:36:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"customer\": {\n" +
                "          \"id\": 1,\n" +
                "          \"userName\": \"8606245097\",\n" +
                "          \"name\": \"16886485849\"\n" +
                "        },\n" +
                "        \"goodsSkuId\": 273,\n" +
                "        \"type\": 1,\n" +
                "        \"content\": \"哇偶\",\n" +
                "        \"state\": 1,\n" +
                "        \"gmtCreate\": \"2020-12-10T22:36:01\",\n" +
                "        \"gmtModified\": \"2020-12-10T22:36:01\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
}
