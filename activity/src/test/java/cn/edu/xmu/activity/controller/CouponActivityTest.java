package cn.edu.xmu.activity.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是其他组的代码，请勿上传，谢谢！！！
 * description: SPU相关api测试类
 * date: 2020/11/30 0:38
 * author: 秦楚彦 24320182203254
 * version: 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CouponActivityTest {
    @Autowired
    private ObjectMapper mObjectMapper;

    private WebTestClient webClient;

    private static String adminToken;
    private static String shopToken;

    public CouponActivityTest(){
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
     * description: 查看优惠活动详情 (成功)
     * date: 2020/12/04 20：27
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */

    @Test
    public void showCouponActivity1() throws Exception {
        byte[] responseBuffer = null;

        responseBuffer = webClient.get().uri("/shops/1/couponactivities/1").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 查看优惠活动详情 (活动不存在)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity2() throws Exception {
        byte[] responseBuffer = null;

        responseBuffer = webClient.get().uri("/shops/1/couponactivities/2").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 查看优惠活动详情 (shopId不匹配)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity3() throws Exception {
        byte[] responseBuffer = null;

        responseBuffer= webClient.get().uri("/shops/3/couponactivities/1").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();
        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 新建己方优惠活动 (成功)
     * date: 2020/12/05 15:32
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addCouponActivity1() throws Exception {
        Map<String,Object> vo = new HashMap<>();
        vo.put("name", "lipstickSale");
        vo.put("beginTime", LocalDateTime.now().toString());
        vo.put("endTime", LocalDateTime.of(2020,12,31,8,0,0).toString());
        vo.put("quantity", 30000);
        vo.put("quantityType", 1);
        vo.put("strategy", "{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.put("validTerm", 0);
        String activityJson=JacksonUtil.toJson(vo);

        byte[] responseBuffer = null;
        responseBuffer=webClient.post().uri("/shops/1/couponactivities").header("authorization",adminToken).bodyValue(activityJson).exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/json;charset=UTF-8")
                    .expectBody()
                    .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 修改己方优惠活动 (成功)
     * date: 2020/12/05 20:04
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity1() throws Exception {
        Map<String,Object> vo = new HashMap<>();
        vo.put("name", "colaSale");
        vo.put("beginTime", LocalDateTime.now().toString());
        vo.put("endTime", LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.put("quantity", 100);
        vo.put("quantityType", 0);
        vo.put("strategy", "{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.put("validTerm", 0);
        String activityJson=JacksonUtil.toJson(vo);

        byte[] responseBuffer = null;
        responseBuffer=webClient.put().uri("/shops/1/couponactivities/5").header("authorization",adminToken).bodyValue(activityJson).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 修改己方优惠活动 (优惠活动id不存在)
     * date: 2020/12/05 20:16
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity2() throws Exception {
        Map<String,Object> vo = new HashMap<>();
        vo.put("name", "appleSale");
        vo.put("beginTime", LocalDateTime.now().toString());
        vo.put("endTime", LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.put("quantity", 100);
        vo.put("quantityType", 0);
        vo.put("strategy", "{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.put("validTerm", 0);
        String activityJson=JacksonUtil.toJson(vo);

        byte[] responseBuffer = null;
        responseBuffer=webClient.put().uri("/shops/1/couponactivities/100").header("authorization",adminToken).bodyValue(activityJson).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();
        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 下线己方优惠活动 (成功)
     * date: 2020/12/05 21:30
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity1() throws Exception {
        byte[] responseBuffer = null;
        responseBuffer=webClient.delete().uri("/shops/1/couponactivities/5").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 下线己方优惠活动 (优惠活动已下线)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity2() throws Exception {
        byte[] responseBuffer = null;
        responseBuffer=webClient.delete().uri("/shops/1/couponactivities/5").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();
        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 下线己方优惠活动 (不发优惠券类型的优惠活动 成功)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity3() throws Exception {
        byte[] responseBuffer = null;
        responseBuffer=webClient.delete().uri("/shops/1/couponactivities/10")
                .header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 下线己方优惠活动 (优惠活动无状态为【可用】优惠券)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity4() throws Exception {
        byte[] responseBuffer = null;
        responseBuffer = webClient.delete().uri("/shops/1/couponactivities/5").header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();
        
        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 查看上线的活动列表 (成功)
     * date: 2020/12/6 18:50
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    void getCouponActivityList() throws Exception{
        byte[] responseBuffer = null;
        responseBuffer=webClient.get().uri("/couponactivities?page=1&pageSize=3&timeline=1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");

//        responseString=webClient.get().uri("/couponactivities")
//                .queryParam("page", "2").queryParam("pageSize", "2").queryParam("shopId","1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
//
//        responseString=webClient.get().uri("/couponactivities")
//                .queryParam("page", "2").queryParam("pageSize", "3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
//
//        responseString=webClient.get().uri("/couponactivities")
//                .queryParam("page", "2").queryParam("pageSize", "2").queryParam("shopId","3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);


    }

    /**
     * description: 查看下线的活动列表 (成功)
     * date: 2020/12/6 22:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    void getInvalidCouponActivityList1() throws Exception{
        byte[] responseBuffer = null;
        responseBuffer=webClient.get().uri("/shops/1/couponactivities/invalid?page=1&pageSize=3").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }

    /**
     * description: 查看下线的活动列表 (成功)
     * date: 2020/12/6 22:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    void getInvalidCouponActivityList2() throws Exception{
        //该店铺不存在invalid活动
        byte[] responseBuffer = null;
        responseBuffer=webClient.get().uri("/shops/2/couponactivities/invalid?page=1&pageSize=3")
                .header("authorization",adminToken).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

        String responseString = new String(responseBuffer, "utf-8");
    }
}

