package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ShopStateVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * description: ShopControllerTest
 * date: 2020/12/10 13:17
 * author: 杨铭
 * version: 1.0
 */
@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class ShopControllerTest {


    @Autowired
    private MockMvc mvc;

    private WebTestClient webClient;

    @Autowired
    private ShopPoMapper shopPoMapper;

    @Autowired
    private GoodsSkuPoMapper goodsSkuPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ShopControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }


    @Test
    public void getshopState1() throws Exception {

        String responseString=this.mvc.perform(get("/shops/state"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0},{\"name\":\"未上线\",\"code\":1},{\"name\":\"上线\",\"code\":2},{\"name\":\"关闭\",\"code\":3},{\"name\":\"审核未通过\",\"code\":4}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


    @Test
    public void addShop1() throws Exception {

        String name ="testshop";
        String Json = JacksonUtil.toJson(name);
        String token = creatTestToken(1L, 0L, 100);
        byte[] responseString = webClient.post().uri("/goods/shops").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.name").isEqualTo("testshop")
                .jsonPath("$.state").isEqualTo(Shop.ShopStatus.NOT_AUDIT)
                .returnResult().getResponseBodyContent();
    }

    @Test
    public void addShop2() throws Exception {

        String name ="testshop";
        String Json = JacksonUtil.toJson(name);
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/goods/shops").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }



    @Test
    public void auditShop1() throws Exception{


        String token = creatTestToken(1L, 0L, 100);
        boolean conclusion = true;
        String Json = JacksonUtil.toJson(conclusion);
        String responseString = this.mvc.perform(put("/goods/shops/0/newshops/1/audit").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //检查是否真的修改了数据库
        ShopPo newShopPo = shopPoMapper.selectByPrimaryKey(1L);
        logger.debug("new:"+newShopPo.getState().toString());
        logger.debug("old:"+ Shop.ShopStatus.ONLINE.getCode().toString());
        Assert.state((newShopPo.getState().toString().equals(Shop.ShopStatus.ONLINE.getCode().toString())), "店铺状态未修改");//true则报错

    }

    @Test
    public void modifyshop1() throws Exception{

        //查询旧值
        ShopPo oldShopPo = shopPoMapper.selectByPrimaryKey(1L);

        String name= "xmushop";
        String Json=JacksonUtil.toJson(name);
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(put("/goods/shops/1").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //检查是否真的修改了数据库
        ShopPo newShopPo = shopPoMapper.selectByPrimaryKey(1L);
        logger.debug("旧值："+oldShopPo.getName());
        logger.debug("新值："+newShopPo.getName());
        Assert.state(!name.equals(newShopPo.getName()), "店铺名称未修改！");//true则报错

    }

    @Test
    public void modifyShop_unaudit() throws Exception{

        String token = creatTestToken(1L, 6L, 100);
        String Json = "{\"name\": \"没过审\"}";
        String responseString = this.mvc.perform(put("/goods/shops/6").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.SHOP_STATENOTALLOW.getCode()))
                .andReturn().getResponse().getContentAsString();

    }


    @Test
    public void updateShop_other() throws Exception {
        //String shopToken = this.login("shopadmin_No2", "123456");
        String token = creatTestToken(1L, 6L, 100);
        String Json = "{\"name\": \"别人的店铺\"}";
        String responseString = this.mvc.perform(put("/goods/shops/8").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode()))
                .andReturn().getResponse().getContentAsString();

    }


    /**
     * 修改审核未通过的商铺的名字
     */
    @Test
    public void updateShop_state() throws Exception {
        String token = creatTestToken(1L, 8L, 100);
        String Json = "{\"name\": \"状态不会变\",\"state\":4}";

        String responseString = this.mvc.perform(put("/goods/shops/8").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 修改商铺的名字为空
     * @author  24320182203310 Yang Lei
     * @date 2020/12/15 16:07
     */
    @Test
    public void updateShop_null() throws Exception {

        String token = creatTestToken(1L, 8L, 100);

        String Json = "{\"name\": \"  \"}";

        String responseString = this.mvc.perform(put("/goods/shops/8").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andReturn().getResponse().getContentAsString();

    }



    @Test
    public void updateShop_ID() throws Exception {

        String token = creatTestToken(1L, 8L, 100);

        String Json = "{\"name\": \"ID不会变\",\"id\":120}";

        String responseString = this.mvc.perform(put("/goods/shops/8").header("authorization",token).contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andReturn().getResponse().getContentAsString();

    }


    @Test
    public void onshelfShop1() throws Exception{


        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(put("/goods/shops/1/onshelves").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //检查是否真的修改了数据库
        ShopPo newShopPo = shopPoMapper.selectByPrimaryKey(1L);
        Assert.state((newShopPo.getState() == Shop.ShopStatus.ONLINE.getCode().byteValue()), "店铺状态未修改");//false则报错

    }


    @Test
    public void offshelfShop1() throws Exception{


        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(put("/goods/shops/1/offshelves").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //检查是否真的修改了数据库
        ShopPo newShopPo = shopPoMapper.selectByPrimaryKey(1L);
        Assert.state((newShopPo.getState() == Shop.ShopStatus.OFFLINE.getCode().byteValue()), "店铺状态未修改");//false则报错

    }


    @Test
    public void deleteshop1() throws Exception{


        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(delete("/goods/shops/1").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //检查是否真的修改了数据库
        ShopPo newShopPo = shopPoMapper.selectByPrimaryKey(1L);
        Assert.state((newShopPo.getState() == Shop.ShopStatus.CLOSED.getCode().byteValue()), "店铺状态未修改");//false则报错

    }


}
