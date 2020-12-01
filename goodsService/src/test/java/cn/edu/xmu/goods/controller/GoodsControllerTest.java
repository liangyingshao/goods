package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GoodsControllerTest {

    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void modifyshop1() throws Exception{

        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(put("/goods/shops/1").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    void getSkuList() throws Exception{
        String responseString=this.mvc.perform(get("/goods/skus")
                .queryParam("spuId", "273").queryParam("spuSn","drh-d0001").queryParam("page", "1").queryParam("pageSize", "5")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"list\":[{\"id\":273,\"goodsSpuId\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":273,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(get("/goods/skus")
                .queryParam("page", "1").queryParam("pageSize", "5")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
        expectedResponse="{\"errno\":0,\"data\":{\"total\":406,\"list\":[{\"id\":273,\"goodsSpuId\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":273,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":274,\"goodsSpuId\":274,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"originalPrice\":850,\"configuration\":null,\"weight\":4,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":274,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"configuration\":null,\"weight\":4,\"imageUrl\":null,\"inventory\":99,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":275,\"goodsSpuId\":275,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"originalPrice\":4028,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":275,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":276,\"goodsSpuId\":276,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"originalPrice\":6225,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":276,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":277,\"goodsSpuId\":277,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"originalPrice\":16200,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":277,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":82,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(get("/goods/skus")
                .queryParam("page", "2").queryParam("pageSize", "5")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
        expectedResponse="{\"errno\":0,\"data\":{\"total\":406,\"list\":[{\"id\":278,\"goodsSpuId\":278,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":278,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"imageUrl\":null,\"inventory\":46100,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":279,\"goodsSpuId\":279,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":279,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"imageUrl\":null,\"inventory\":500,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":280,\"goodsSpuId\":280,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"originalPrice\":2399,\"configuration\":null,\"weight\":5,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":280,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"configuration\":null,\"weight\":5,\"imageUrl\":null,\"inventory\":1834,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":281,\"goodsSpuId\":281,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"originalPrice\":1380000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":281,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":282,\"goodsSpuId\":282,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"originalPrice\":120000,\"configuration\":null,\"weight\":20,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":282,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"configuration\":null,\"weight\":20,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}}],\"pageNum\":2,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":82,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(get("/goods/skus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
        expectedResponse="{\"errno\":0,\"data\":{\"total\":406,\"list\":[{\"id\":273,\"goodsSpuId\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":273,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":274,\"goodsSpuId\":274,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"originalPrice\":850,\"configuration\":null,\"weight\":4,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":274,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"configuration\":null,\"weight\":4,\"imageUrl\":null,\"inventory\":99,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":275,\"goodsSpuId\":275,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"originalPrice\":4028,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":275,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":276,\"goodsSpuId\":276,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"originalPrice\":6225,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":276,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":277,\"goodsSpuId\":277,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"originalPrice\":16200,\"configuration\":null,\"weight\":3,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":277,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"configuration\":null,\"weight\":3,\"imageUrl\":null,\"inventory\":10,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":278,\"goodsSpuId\":278,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":278,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"imageUrl\":null,\"inventory\":46100,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":279,\"goodsSpuId\":279,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":279,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"configuration\":null,\"weight\":2,\"imageUrl\":null,\"inventory\":500,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":280,\"goodsSpuId\":280,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"originalPrice\":2399,\"configuration\":null,\"weight\":5,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":280,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"configuration\":null,\"weight\":5,\"imageUrl\":null,\"inventory\":1834,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":281,\"goodsSpuId\":281,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"originalPrice\":1380000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":281,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},{\"id\":282,\"goodsSpuId\":282,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"originalPrice\":120000,\"configuration\":null,\"weight\":20,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":282,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"configuration\":null,\"weight\":20,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":0,\"endRow\":9,\"pages\":41,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void getSku() throws Exception{
        String responseString=this.mvc.perform(get("/goods/skus/273"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":273,\"goodsSpuId\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":273,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void deleteSku() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(delete("/goods/shops/0/skus/273").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(delete("/goods/shops/1/skus/273").header("authorization",token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(delete("/goods/shops/0/skus/1").header("authorization",token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    void modifySKU() throws Exception{
        String requireJson="{\n    \"name\": \"name\",\n    \"originalPrice\": \"100\",\n    \"configuration\": \"configuration\",\n    \"weight\": \"100\",\n    \"inventory\": \"9999\",\n    \"detail\": \"detail\"\n}";
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(put("/goods/shops/0/skus/273")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        responseString=this.mvc.perform(put("/goods/shops/1/skus/273")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    @Test
    public void addSkuComment1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/goods/orderitems/1/comments").header("authorization",token).contentType("application/json;charset=UTF-8").queryParam("type", "0").queryParam("content", "购物体验良好"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        //一要检测返回值是否符合预期
        //二要检查数据库的值是否符合预期
    }

    @Test
    public void getcommentState1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/goods/comments/states").header("authorization",token).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"待审核\",\"code\":0},{\"name\":\"审核通过\",\"code\":1},{\"name\":\"审核不通过\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }
}