package cn.edu.xmu.activity.controller;



import cn.edu.xmu.activity.LoginVo;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 刘菲艳 24320182203234
 * @date Created at 2020/12/15 21:33
 */
@SpringBootTest(classes = Application.class)
public class LiuFeiYanTest {

    //@Value("${public-test.managementgate}")
    private String managementGate;

    //@Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    /**
     * 初始化 Client
     */
    @BeforeEach
    public void setUp(){

        managementGate="192.168.43.73:8881";
        mallGate="192.168.43.73:8880";
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    /**
     * 用户登入
     * @param userName 用户名
     * @param password 密码
     * @throws Exception
     */
    private String login(String userName, String password) {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        assert ret != null;
        return JacksonUtil.parseString(new String(ret, StandardCharsets.UTF_8), "data");
    }

    /**
     * 优惠活动模块
     */

    /**
     * 查看优惠活动详情(活动id不存在)
     * @throws Exception
     */
    @Test
    public void getCouponActivity1() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        manageClient
                .get()
                .uri("/shops/0/couponactivities/100415648632")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 查看优惠活动详情 优惠活动不属于己方
     * @throws Exception
     */
    @Test
    public void getCouponActivity2() throws Exception{
        String token=this.login("shopadmin_No1", "123456");
        manageClient
                .get()
                .uri("/shops/2/couponactivities/1582")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上线优惠活动 活动已被删除
     * @throws Exception
     */
    @Test
    public void putCouponActivityOnShelves1() throws Exception{
        //user的departId为0
        //id为5821的活动 departId=0 状态为已删除
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/couponactivities/5821/onshelves")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * 管理员上线优惠活动 活动不是己方活动
     * @throws Exception
     */
    @Test
    public void putCouponActivityOnShelves2() throws Exception{
        //userId的departId=2
        //id为2158的活动 departId=0
        String token=this.login("shopadmin_No1", "123456");
        byte[] responseString = manageClient.put().uri("/shops/2/couponactivities/2158/onshelves")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 管理员下线优惠活动 活动已被删除
     * @throws Exception
     */
    @Test
    public void putCouponActivityOffShelves1() throws Exception{
        //id为5821的活动 departId=0 状态为已删除
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/couponactivities/5821/offshelves")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * 管理员下线优惠活动 活动不是己方活动
     * @throws Exception
     */
    @Test
    public void putCouponActivityOffShelves2() throws Exception{
        //user的departId=8
        //id为1582的活动 departId=0 状态为上线
        String token=this.login("shopadmin_No1", "123456");
        byte[] responseString = manageClient.put().uri("/shops/2/couponactivities/1582/offshelves")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除优惠活动 活动处于上线状态 不可删除
     * @throws Exception
     */
    @Test
    public void deleteCouponActivity1() throws Exception{
        //id为1582的活动 departId=0 状态为已上线
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/couponactivities/1582")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 管理员修改己方优惠活动 操作的活动不是己方的优惠活动
     * @throws Exception
     */
    @Test
    public void updateCouponActivity1() throws Exception{
        //user的departId=8
        //id为2158的活动 departId=0
        String token=this.login("shopadmin_No1", "123456");
        LocalDateTime beginTime=LocalDateTime.now().plusDays(1);
        LocalDateTime endTime=LocalDateTime.now().plusDays(100);
        String json = "{\"name\":\"618大促\",\"quantity\":0,\"quantityType\":0,\"beginTime\":\""+beginTime+"\",\"endTime\":\""+endTime+"\",\"strategy\":\"优惠策略\"}";
        byte[] responseString = manageClient.put().uri("/shops/2/couponactivities/2158")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改己方优惠活动 活动是上线状态 不可修改
     * @throws Exception
     */
    @Test
    public void  updateCouponActivity2() throws Exception{
        //user的departId为0
        //id为1582的活动 departId=0 但处于上线状态 state=1
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        LocalDateTime beginTime=LocalDateTime.now().plusDays(1);
        LocalDateTime endTime=LocalDateTime.now().plusDays(100);
        String json = "{\"name\":\"618大促\",\"quantity\":0,\"quantityType\":0,\"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"strategy\":\"优惠策略\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/couponactivities/1582")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改己方优惠活动 活动是删除状态 不可修改
     * @throws Exception
     */
    @Test
    public void  updateCouponActivity3() throws Exception{
        //user的departId为0
        //id为5821的活动 departId=0 但处于删除状态 state=2
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        LocalDateTime beginTime=LocalDateTime.now().plusDays(1);
        LocalDateTime endTime=LocalDateTime.now().plusDays(100);
        String json = "{\"name\":\"618大促\",\"quantity\":0,\"quantityType\":0,\"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"strategy\":\"优惠策略\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/couponactivities/5821")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 查看优惠活动中的商品 活动id不存在
     * @throws Exception
     */
    @Test
    public void getCouponSku1() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/couponactivities/100415648632/skus")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 用户领取优惠券 优惠券对应的活动id不存在
     * @throws Exception
     */
    @Test
    public void getCoupon1() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/couponactivities/122659232/usercoupons")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 向优惠活动新增商品 操作的活动不存在
     */
    @Test
    public void  addCouponSku1() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        Long[] skuId= new Long[1];
        skuId[0]=273L;
        String json = JacksonUtil.toJson(skuId);
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities/2158888/skus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 向优惠活动新增商品 操作的商品不存在
     */
    @Test
    public void  addCouponSku2() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        Long[] skuId= new Long[1];
        skuId[0]=277773L;
        String json = JacksonUtil.toJson(skuId);
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities/2158/skus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 向优惠活动新增商品 优惠活动已被删除
     */
    @Test
    public void  addCouponSku3() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        Long[] skuId= new Long[1];
        skuId[0]=273L;
        String json = JacksonUtil.toJson(skuId);
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities/5821/skus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COUPONACT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 向优惠活动新增商品 商品不属于本店
     */
    @Test
    public void  addCouponSku4() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        Long[] skuId= new Long[1];
        skuId[0]=1275L;//此商品的shopId不为0
        String json = JacksonUtil.toJson(skuId);
        byte[] responseString = manageClient.post().uri("/shops/0/couponactivities/2158/skus")
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除优惠活动中的商品 coupon_sku的id不存在
     */
    @Test
    public void  deleteCouponSku1() throws Exception{
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE5MDI1OTI4NTBTIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDgzMjE1NjgsInVzZXJJZCI6MSwiaWF0IjoxNjA4MzE3OTY4fQ.n0Bjg03jImHGucV90XtK8UU1UjGWDPrVmykKnLd4xtA";
                //this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/couponskus/111111")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除优惠活动中的商品 操作的活动不属于本商店
     */
    @Test
    public void  deleteCouponSku2() throws Exception{
        //user的departId为8
        //coupon_sku中 id为1234的数据项 活动的所属商店id为0
        String token=this.login("shopadmin_No1", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/2/couponskus/1234")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


}

