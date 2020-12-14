package cn.edu.xmu.activity.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest   //标识本类是一个SpringBootTest
@AutoConfigureWebTestClient(timeout = "100000")//100 seconds
class CouponTest {

    @Autowired
    private ObjectMapper mObjectMapper;

    private WebTestClient webClient;

    private static String adminToken;
    private static String shopToken;

    public CouponTest(){
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8092")
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
     * 获取优惠券状态
     * @throws Exception
     */
    @Test
    void getCouponState() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer = webClient.get().uri("/coupons/states").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(0)
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(4)
                .jsonPath("$.data[0].code").isNumber()
                .jsonPath("$.data[0].name").isNotEmpty()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * 查询活动中的SKU
     * @throws Exception
     */
    @Test
    void getCouponSkuList() throws Exception{
        byte[] responseBuffer = null;
        /* 查询第一页 */
        responseBuffer = webClient.get().uri("/couponactivities/1/skus?page=1&pageSize=2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(0)
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.total").isEqualTo(3)
                .jsonPath("$.data.page").isEqualTo(1)
                .jsonPath("$.data.pageSize").isEqualTo(2)
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.list[?(@.id == 301)].name").isEqualTo((String)"+")
                .jsonPath("$.data.list[?(@.id == 301)].inventory").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == 301)].originalPrice").isEqualTo(250000)
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        /* 查询第二页 */
        responseBuffer = webClient.get().uri("/couponactivities/1/skus?page=2&pageSize=2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(0)
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.total").isEqualTo(3)
                .jsonPath("$.data.page").isEqualTo(2)
                .jsonPath("$.data.pageSize").isEqualTo(2)
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.list[?(@.id == 290)].name").isEqualTo("+")
                .jsonPath("$.data.list[?(@.id == 290)].inventory").exists()
                .jsonPath("$.data.list[?(@.id == 290)].originalPrice").exists()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        responseBuffer=webClient.get().uri("/couponactivities/1/skus").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(0)
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.total").isEqualTo(3)
                .jsonPath("$.data.page").isEqualTo(1)
                .jsonPath("$.data.pageSize").isNumber()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.list[?(@.id == 300)].name").isEqualTo("+")
                .jsonPath("$.data.list[?(@.id == 300)].inventory").exists()
                .jsonPath("$.data.list[?(@.id == 300)].originalPrice").exists()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * 向活动中添加SKU
     * @throws Exception
     */
    @Test
    void createCouponSkus() throws Exception{
        List<Long> body=new ArrayList<>();
        body.add((long)275);
        body.add((long)276);

        /* 加入SKU */
        byte[] responseBuffer = null;
        responseBuffer = webClient.post().uri("/shops/0/couponactivities/6/skus").header("authorization",adminToken).bodyValue(body.toString()).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        /* 重复加入SKU */
        responseBuffer = webClient.post().uri("/shops/0/couponactivities/5/skus").header("authorization",adminToken).bodyValue(body.toString()).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        /* SKU不是自己的 */
        responseBuffer=webClient.post().uri("/shops/1/couponactivities/4/skus").header("authorization",shopToken).bodyValue(body.toString()).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        /* 活动上线中 */
        responseBuffer = webClient.post().uri("/shops/0/couponactivities/7/skus").header("authorization",adminToken).bodyValue(body.toString()).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        /* 活动不是自己的 */
        responseBuffer=webClient.post().uri("/shops/1/couponactivities/6/skus").header("authorization",shopToken).bodyValue(body.toString()).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

//        responseBuffer = webClient.post().uri("/shops/0/couponactivities/6/skus").header("authorization",adminToken).bodyValue(body.toString()).exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBody();
//
//        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
//        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);
//
//        List<Long> body1=new ArrayList<>();
//        body1.add((long)1);
//        responseBuffer = webClient.post().uri("/shops/0/couponactivities/6/skus").header("authorization",adminToken).bodyValue(body1.toString()).exchange()
//                .expectStatus().is4xxClientError()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBody();
//
//        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
//        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
//        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }

    @Test
    void deleteCouponSku() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer=webClient.delete().uri("/shops/0/couponskus/5").header("authorization",shopToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.delete().uri("/shops/1/couponskus/5").header("authorization",adminToken).exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.delete().uri("/shops/1/couponskus/1").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"errno\":904,\"errmsg\":\"优惠活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.delete().uri("/shops/2/couponskus/5").header("authorization",adminToken).exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.delete().uri("/shops/2/couponskus/5").header("authorization",adminToken).exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void showCoupons() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer = webClient.post().uri("/couponactivities/5/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        responseBuffer = webClient.post().uri("/couponactivities/5/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        responseBuffer=webClient.get().uri("/coupons?state=1&page=1&pageSize=2").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);

        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"list\":[{\"id\":28,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060145267JZ\"},{\"id\":29,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"2020120601452690X\"}],\"pageNum\":1,\"pageSize\":2,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseBuffer=webClient.get().uri("/coupons?state=1&page=2&pageSize=2").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":0,\"list\":[],\"pageNum\":1,\"pageSize\":0,\"size\":0,\"startRow\":0,\"endRow\":0,\"pages\":0,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[],\"navigateFirstPage\":0,\"navigateLastPage\":0}}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseBuffer=webClient.get().uri("/coupons?state=1&page=2&pageSize=1").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":1,\"list\":[{\"id\":33,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060149509QZ\"}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseBuffer=webClient.get().uri("/coupons?state=1").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"list\":[{\"id\":28,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060145267JZ\"},{\"id\":29,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"2020120601452690X\"}],\"pageNum\":1,\"pageSize\":2,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    //从这里才导入了新的测试数据Coupon.sql，所以一开始导入了前面的测试应该过不了
    @Test
    void useCoupon() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer = webClient.put().uri("/coupons/36").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer = webClient.put().uri("/coupons/36").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"COUPON_STATENOTALLOW\",\"errmsg\":\"优惠卷状态禁止\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer= webClient.put().uri("/coupons/360").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer = webClient.put().uri("/coupons/37").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void getCoupon() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer=webClient.post().uri("/couponactivities/1/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":15,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"imageUrl\":null,\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"customerId\":1,\"name\":\"foodsale\",\"couponSn\":\"202012060130335ST\",\"state\":1,\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"gmtCreate\":\"2020-12-06T01:30:34\",\"gmtModified\":\"2020-12-06T01:30:34\"}}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer = webClient.post().uri("/couponactivities/1/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseBuffer=webClient.post().uri("/couponactivities/1/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"COUPON_FINISH\",\"errmsg\":\"优惠卷领罄\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.post().uri("/couponactivities/2/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":20,\"activity\":{\"id\":2,\"name\":\"chipsSale\",\"imageUrl\":null,\"beginTime\":\"2020-12-04T15:49:02\",\"endTime\":\"2020-12-11T15:49:05\",\"quantity\":2500,\"couponTime\":\"2020-12-04T17:49:38\"},\"customerId\":1,\"name\":\"chipsSale\",\"couponSn\":\"202012060134493SB\",\"state\":1,\"beginTime\":\"2020-12-04T15:49:02\",\"endTime\":\"2020-12-11T15:49:05\",\"gmtCreate\":\"2020-12-06T01:34:49\",\"gmtModified\":\"2020-12-06T01:34:49\"}}";

        responseBuffer=webClient.post().uri("/couponactivities/2/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"COUPON_FINISH\",\"errmsg\":\"优惠卷领罄\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.post().uri("/couponactivities/3/usercoupons").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"COUPON_NOTBEGIN\",\"errmsg\":\"未到优惠卷领取时间\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void returnCoupon() throws Exception
    {
        byte[] responseBuffer = null;
        responseBuffer=webClient.put().uri("/shops/100/coupons/38").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);


        responseBuffer=webClient.put().uri("/shops/1/coupons/38").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse ="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.put().uri("/shops/1/coupons/36").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"COUPON_STATENOTALLOW\",\"errmsg\":\"优惠卷状态禁止\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.put().uri("/shops/1/coupons/3600").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseBuffer=webClient.put().uri("/shops/100/coupons/38").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        expectedResponse="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    @Test
    public void modifyGrouponofSPU()throws Exception{
        byte[] responseBuffer = null;
        String requestJson = "{\"beginTime\": \"2020-12-20 15:55:18\", \"endTime\": \"2022-01-05 15:55:18\",\"strategy\": \"teststrategy\"}";

        responseBuffer = webClient.put().uri("/shops/1/groupons/1").header("authorization",adminToken).bodyValue(requestJson).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //测试是否真的改变
    }
}