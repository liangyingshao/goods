package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityControllerTest {

    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    void getcouponState() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(get("/goods/coupons/states")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"不可用\"},{\"code\":1,\"name\":\"可用\"},{\"code\":2,\"name\":\"已使用\"},{\"code\":3,\"name\":\"失效\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void getCouponSkuList() throws Exception{
        String responseString=this.mvc.perform(get("/goods/couponactivities/1/skus")
                        .queryParam("page", "1").queryParam("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"list\":[{\"name\":\"+\",\"id\":300},{\"name\":\"+\",\"id\":301}],\"pageNum\":1,\"pageSize\":2,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(get("/goods/couponactivities/1/skus")
                .queryParam("page", "2").queryParam("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":1,\"list\":[{\"name\":\"+\",\"id\":290}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(get("/goods/couponactivities/1/skus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":3,\"list\":[{\"name\":\"+\",\"id\":300},{\"name\":\"+\",\"id\":301},{\"name\":\"+\",\"id\":290}],\"pageNum\":1,\"pageSize\":3,\"size\":3,\"startRow\":0,\"endRow\":2,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void createCouponSkus() throws Exception{
//        List<Long> body=new ArrayList<>();
//        body.add((long)273);
//        body.add((long)274);
//        String requireJson="[\n    273\n]";
//        String token = creatTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(post("/shops/0/couponactivities/3/skus")
//                .header("authorization",token)
//                .contentType("application/json;charset=UTF-8")
//                .content(requireJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
    }

    @Test
    void deleteCouponSku() throws Exception
    {
        String token = creatTestToken(1L, 1L, 100);
        String responseString=this.mvc.perform(delete("/goods/shops/1/couponskus/5")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(delete("/goods/shops/1/couponskus/5")
                .header("authorization",token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(delete("/goods/shops/1/couponskus/1")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":904,\"errmsg\":\"优惠活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(delete("/goods/shops/2/couponskus/5")
                .header("authorization",token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        token = creatTestToken(1L, 2L, 100);
        responseString=this.mvc.perform(delete("/goods/shops/2/couponskus/5")
                .header("authorization",token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void showCoupons() throws Exception
    {

        String token = creatTestToken(1L, 0L, 100);
        this.mvc.perform(post("/goods/couponactivities/1/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        this.mvc.perform(post("/goods/couponactivities/1/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String responseString=this.mvc.perform(get("/goods/coupons")
                .header("authorization",token)
                .queryParam("state","1")
                .queryParam("page", "1")
                .queryParam("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"list\":[{\"id\":28,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060145267JZ\"},{\"id\":29,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"2020120601452690X\"}],\"pageNum\":1,\"pageSize\":2,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        //JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseString=this.mvc.perform(get("/goods/coupons")
                .header("authorization",token)
                .queryParam("state","1")
                .queryParam("page", "2")
                .queryParam("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":0,\"list\":[],\"pageNum\":1,\"pageSize\":0,\"size\":0,\"startRow\":0,\"endRow\":0,\"pages\":0,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[],\"navigateFirstPage\":0,\"navigateLastPage\":0}}";
        //JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseString=this.mvc.perform(get("/goods/coupons")
                .header("authorization",token)
                .queryParam("state","1")
                .queryParam("page", "2")
                .queryParam("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":1,\"list\":[{\"id\":33,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060149509QZ\"}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        //JSONAssert.assertEquals(expectedResponse,responseString,false);

        responseString=this.mvc.perform(get("/goods/coupons")
                .header("authorization",token)
                .queryParam("state","1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":2,\"list\":[{\"id\":28,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"202012060145267JZ\"},{\"id\":29,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"name\":\"foodsale\",\"couponSn\":\"2020120601452690X\"}],\"pageNum\":1,\"pageSize\":2,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1}}";
        //JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    @Test
    void useCoupon() {
    }

    @Test
    void deleteCoupon() {
    }

    @Test
    void getCoupon() throws Exception
    {
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(post("/goods/couponactivities/1/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":15,\"activity\":{\"id\":1,\"name\":\"foodsale\",\"imageUrl\":null,\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"quantity\":2,\"couponTime\":\"2020-12-03T02:00:00\"},\"customerId\":1,\"name\":\"foodsale\",\"couponSn\":\"202012060130335ST\",\"state\":1,\"beginTime\":\"2020-12-02T20:18:43\",\"endTime\":\"2020-12-07T20:18:46\",\"gmtCreate\":\"2020-12-06T01:30:34\",\"gmtModified\":\"2020-12-06T01:30:34\"}}";
        //JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(post("/goods/couponactivities/1/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        responseString=this.mvc.perform(post("/goods/couponactivities/1/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"code\":\"COUPON_FINISH\",\"errmsg\":\"优惠卷领罄\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(post("/goods/couponactivities/2/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":20,\"activity\":{\"id\":2,\"name\":\"chipsSale\",\"imageUrl\":null,\"beginTime\":\"2020-12-04T15:49:02\",\"endTime\":\"2020-12-11T15:49:05\",\"quantity\":2500,\"couponTime\":\"2020-12-04T17:49:38\"},\"customerId\":1,\"name\":\"chipsSale\",\"couponSn\":\"202012060134493SB\",\"state\":1,\"beginTime\":\"2020-12-04T15:49:02\",\"endTime\":\"2020-12-11T15:49:05\",\"gmtCreate\":\"2020-12-06T01:34:49\",\"gmtModified\":\"2020-12-06T01:34:49\"}}";

        responseString=this.mvc.perform(post("/goods/couponactivities/2/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"code\":\"COUPON_FINISH\",\"errmsg\":\"优惠卷领罄\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(post("/goods/couponactivities/3/usercoupons")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"code\":\"COUPON_NOTBEGIN\",\"errmsg\":\"未到优惠卷领取时间\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void returnCoupon() {
    }
}