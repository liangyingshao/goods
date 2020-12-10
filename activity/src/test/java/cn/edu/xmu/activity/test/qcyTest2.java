package cn.edu.xmu.activity.test;


import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.model.vo.CouponActivityCreateVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * description: SPU相关api测试类
 * date: 2020/11/30 0:38
 * author: 秦楚彦 24320182203254
 * version: 1.0
 */
@SpringBootTest(classes = ActivityServiceApplication.class)
@AutoConfigureMockMvc
//@Transactional
public class qcyTest2 {
    @Autowired
    MockMvc mvc;

    /**
     * description: 创建测试用token
     * date: 2020/12/01 8:37
     * author: 秦楚彦 24320182203254
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    /**
     * description: 查看优惠活动详情 (成功)
     * date: 2020/12/04 20：27
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */

    @Test
    public void showCouponActivity1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/1/couponactivities/1")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看优惠活动详情 (活动不存在)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/1/couponactivities/2")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看优惠活动详情 (shopId不匹配)
     * date: 2020/12/04 20：48
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void showCouponActivity3() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(get("/goods/shops/3/couponactivities/1")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 新建己方优惠活动 (成功)
     * date: 2020/12/05 15:32
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void addCouponActivity1() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("appleSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(10000);
        vo.setQuantityType(1);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(post("/goods/shops/1/couponactivities")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 修改己方优惠活动 (成功)
     * date: 2020/12/05 20:04
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity1() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("colaSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(100);
        vo.setQuantityType(0);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 修改己方优惠活动 (优惠活动id不存在)
     * date: 2020/12/05 20:16
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void modifyCouponActivity2() throws JSONException {
        CouponActivityCreateVo vo=new CouponActivityCreateVo();
        vo.setName("appleSale");
        vo.setBeginTime(LocalDateTime.now().toString());
        vo.setEndTime(LocalDateTime.of(2020,12,31,10,0,0).toString());
        vo.setQuantity(100);
        vo.setQuantityType(0);
        vo.setStrategy("{\"id\":1,\"name\":\"couponstrategy\", \"shresholds\":{\"type\":\"满减\",\"value\":\"200\",\"discount\":\"30\"}");
        vo.setValidTerm(0);
        String activityJson=JacksonUtil.toJson(vo);
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(put("/goods/shops/1/couponactivities/100")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8").content(activityJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (成功)
     * date: 2020/12/05 21:30
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity1() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (优惠活动已下线)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity2() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.errno").value(ResponseCode.ACTIVITYALTER_INVALID.getCode()))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (不发优惠券类型的优惠活动 成功)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity3() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/10")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 下线己方优惠活动 (优惠活动无状态为【可用】优惠券)
     * date: 2020/12/05 21:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    public void offlineCouponActivity4() throws JSONException {
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        try{
            responseString=this.mvc.perform(delete("/goods/shops/1/couponactivities/5")
                    .header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * description: 查看上线的活动列表 (成功)
     * date: 2020/12/6 18:50
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    void getCouponActivityList() throws Exception{
        String responseString=this.mvc.perform(get("/goods/couponactivities")
                .queryParam("page", "1").queryParam("pageSize", "3").queryParam("timeline","1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);

//        responseString=this.mvc.perform(get("/goods/couponactivities")
//                .queryParam("page", "2").queryParam("pageSize", "2").queryParam("shopId","1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
//
//        responseString=this.mvc.perform(get("/goods/couponactivities")
//                .queryParam("page", "2").queryParam("pageSize", "3"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
//
//        responseString=this.mvc.perform(get("/goods/couponactivities")
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
        String token = creatTestToken(1L,1L,100);
        String responseString=null;
        //成功
        responseString=this.mvc.perform(get("/goods/shops/1/couponactivities/invalid")
                .header("authorization",token).contentType("application/json;charset=UTF-8")
                .queryParam("page", "1").queryParam("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    /**
     * description: 查看下线的活动列表 (成功)
     * date: 2020/12/6 22:46
     * author: 秦楚彦 24320182203254
     * version: 1.0
     */
    @Test
    void getInvalidCouponActivityList2() throws Exception{
        String token = creatTestToken(1L,2L,100);
        String responseString=null;
        //该店铺不存在invalid活动
        responseString=this.mvc.perform(get("/goods/shops/2/couponactivities/invalid")
                .header("authorization",token).contentType("application/json;charset=UTF-8")
                .queryParam("page", "1").queryParam("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }
}

