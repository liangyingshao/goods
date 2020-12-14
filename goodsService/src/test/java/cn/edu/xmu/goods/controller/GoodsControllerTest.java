package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
@Slf4j
class GoodsControllerTest {

//    @Autowired
//    private MockMvc mvc;

    private final WebTestClient webClient;

    public GoodsControllerTest() {
        this.webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8090")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void modifyshop1() throws Exception{

        String token = creatTestToken(1L, 0L, 100);
        byte[] response = webClient.put().uri("/shops/1").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"成功\"}";
        
    }

    @Test
    void getSkuList() throws Exception{
        byte[] response =webClient.get().uri("/skus?spuId=273&spuSn=drh-d0001&page=1&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .jsonPath("$.data.pageNum").isEqualTo(1)
                .jsonPath("$.data.pageSize").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";

        response =webClient.get().uri("/skus?page=1&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isEqualTo(5)
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .jsonPath("$.data.pageNum").isEqualTo(1)
                .jsonPath("$.data.pageSize").isEqualTo(5)
                .returnResult()
                .getResponseBodyContent();
        
        expectedResponse="{\"errno\":0,\"data\":{\"total\":5,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disabled\":4,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disabled\":4,\"price\":16200}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";

        response =webClient.get().uri("/skus?page=2&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isEqualTo(5)
                .jsonPath("$.data.list[?(@.id==278)].name").isEqualTo("+")
                .jsonPath("$.data.pageNum").isEqualTo(2)
                .jsonPath("$.data.pageSize").isEqualTo(5)
                .returnResult()
                .getResponseBodyContent();
        
        expectedResponse="{\"errno\":0,\"data\":{\"total\":5,\"list\":[{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disabled\":4,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disabled\":4,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disabled\":4,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disabled\":4,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disabled\":4,\"price\":120000}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";

        response =webClient.get().uri("/skus")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isEqualTo(10)
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .jsonPath("$.data.pageNum").isEqualTo(1)
                .jsonPath("$.data.pageSize").isNumber()
                .returnResult()
                .getResponseBodyContent();
        
        expectedResponse="{\"errno\":0,\"data\":{\"total\":10,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disabled\":4,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disabled\":4,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disabled\":4,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disabled\":4,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disabled\":4,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disabled\":4,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disabled\":4,\"price\":120000}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":0,\"endRow\":9,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}}";
    }

    @Test
    void getSku() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        byte[] response =webClient.get().uri("/skus/273")

                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(273)
                .returnResult()
                .getResponseBodyContent();
        
//        String expectedResponse="{\"errno\":0,\"data\":{\"id\":273,\"goodsSpuId\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"detail\":null,\"disabled\":4,\"gmtCreated\":\"2020-11-28T17:42:17\",\"gmtModified\":\"2020-11-28T17:42:17\",\"goodsSkuPo\":{\"id\":273,\"goodsSpuId\":null,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"configuration\":null,\"weight\":10,\"imageUrl\":null,\"inventory\":1,\"detail\":null,\"disabled\":null,\"gmtCreate\":null,\"gmtModified\":null}},\"errmsg\":\"成功\"}";

        response =webClient.get().uri("/skus/1")
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();

    }

    @Test
    void deleteSku() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        byte[] response =webClient.delete().uri("/shops/1/skus/273").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(505)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";


        response =webClient.delete().uri("/shops/0/skus/273").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";

        response =webClient.delete().uri("/shops/0/skus/273").header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";


        response =webClient.delete().uri("/shops/0/skus/1").header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

    }

    @Test
    void modifySKU() throws Exception{
        String requireJson="{\n    \"name\": \"name\",\n    \"originalPrice\": \"100\",\n    \"configuration\": \"configuration\",\n    \"weight\": \"100\",\n    \"inventory\": \"9999\",\n    \"detail\": \"detail\"\n}";
        String token = creatTestToken(1L, 0L, 100);
        byte[] response =webClient.put().uri("/shops/0/skus/683")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";

        response =webClient.put().uri("/shops/0/skus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(901)
                .returnResult()
                .getResponseBodyContent();

        response =webClient.put().uri("/shops/1/skus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(505)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";

        response =webClient.put().uri("/shops/0/skus/1")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

    }

    @Test
    void add_floating_price() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,20,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"100\"\n}";
        String token = creatTestToken(1L, 0L, 100);
        byte[] response =webClient.post().uri("/shops/0/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.activityPrice").isEqualTo(100)
                .jsonPath("$.data.createdBy.id").isEqualTo(1)
                .jsonPath("$.data.modifiedBy.id").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse ="{\"errno\":0,\"data\":{\"id\":21,\"activityPrice\":100,\"quantity\":100,\"beginTime\":\"2020-12-12T10:00:00\",\"endTime\":\"2020-12-30T10:00:00\",\"createdBy\":{\"id\":1,\"username\":\"createUser\"},\"modifiedBy\":{\"id\":1,\"username\":\"testUser\"}},\"errmsg\":\"成功\"}";

        response =webClient.post().uri("/shops/0/skus/273/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(900)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":900,\"errmsg\":\"库存不足：273\"}";


        response =webClient.post().uri("/shops/1/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";


        LocalDateTime beginTime1=LocalDateTime.of(2019,12,12,10,0,0);
        requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime1.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"100\"\n}";
        response =webClient.post().uri("/shops/0/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":503,\"errmsg\":\"must be a future date;\"}";


        LocalDateTime endTime1=LocalDateTime.of(2020,12,11,20,0,0);
        requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime1.toString()+"\",\n    \"quantity\": \"100\"\n}";
        response =webClient.post().uri("/shops/0/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(610)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":610,\"errmsg\":\"开始时间大于结束时间\"}";


        requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"-100\"\n}";
        response =webClient.post().uri("/shops/0/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":503,\"errmsg\":\"must be greater than or equal to 0;\"}";

    }

    @Test
    void createSKU() throws Exception
    {
        String requireJson="{\n" +
                "  \"sn\": \"newSkuSn\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"originalPrice\": 100,\n" +
                "  \"configuration\": \"configuration\",\n" +
                "  \"weight\": 100,\n" +
                "  \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "  \"inventory\": 100,\n" +
                "  \"detail\": \"detail\"\n" +
                "}";
        String token = creatTestToken(1L, 0L, 100);
        byte[] response =webClient.post().uri("/shops/0/spus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.skuSn").isEqualTo("newSkuSn")
                .jsonPath("$.data.inventory").isEqualTo(100)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse
                //="{\"errno\":0,\"data\":{\"id\":696,\"name\":\"name\",\"skuSn\":\"newSkuSn\",\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":100,\"originalPrice\":100,\"price\":100,\"disabled\":0},\"errmsg\":\"成功\"}"
        ;

        response =webClient.post().uri("/shops/0/spus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(901)
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"errno\":901,\"errmsg\":\"SKU规格重复：name\"}";


        response =webClient.post().uri("/shops/1/spus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";

    }

    @Test
    void getgoodskustate() throws Exception
    {
        byte[] response  = webClient.get().uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(3)
                .jsonPath("$.data[0].code").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";
        
    }

    @Test
    void getShareSku() throws Exception
    {
        String token = creatTestToken(0L, 0L, 100);
        byte[] response  = webClient.get().uri("/share/0/skus/273")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"price\":null,\"disabled\":4}}";
        

        token = creatTestToken(1L, 0L, 100);
        response  = webClient.get().uri("/share/0/skus/273")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
        

        response  = webClient.get().uri("/share/0/skus/274")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
        
    }

    @Test
    void putGoodsOnSale() {
        String token = creatTestToken(0L, 0L, 100);
        byte[] response  = webClient.put().uri("/shops/0/skus/683/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/0/skus/683/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/1/skus/683/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/0/skus/1/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        token = creatTestToken(0L, 1L, 100);
        response  = webClient.put().uri("/shops/1/skus/683/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    void putOffGoodsOnSale() {
        String token = creatTestToken(0L, 0L, 100);
        byte[] response  = webClient.put().uri("/shops/0/skus/683/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/0/skus/683/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/1/skus/683/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        response  = webClient.put().uri("/shops/0/skus/1/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        token = creatTestToken(0L, 1L, 100);
        response  = webClient.put().uri("/shops/1/skus/683/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}