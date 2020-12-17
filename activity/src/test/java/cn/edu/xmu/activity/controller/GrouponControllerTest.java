package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.Groupon;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * description: GrouponControllerTest
 * date: 2020/12/10 16:31
 * author: 杨铭
 * version: 1.0
 */
@SpringBootTest(classes = ActivityServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
//@Transactional
class GrouponControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private GrouponActivityPoMapper grouponActivityPoMapper;

    private WebTestClient webClient;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void getgrouponState()throws Exception{

        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/groupons/states")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"已下线\"},{\"code\":1,\"name\":\"已上线\"},{\"code\":2,\"name\":\"已删除\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 成功
     */
    @Test
    public void createGrouponofSPU()throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2022-01-09 15:55:18");
        grouponVo.setEndTime("2022-01-20 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/shops/1/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":32,\"name\":null,\"goodsSpuPo\":{\"id\":1,\"name\":\"testspu\",\"brandId\":103,\"categoryId\":1,\"freightId\":null,\"shopId\":null,\"goodsSn\":\"hhh\",\"detail\":null,\"imageUrl\":null,\"state\":null,\"spec\":null,\"disabled\":null,\"gmtCreate\":\"2020-12-06T19:22:02\",\"gmtModified\":null},\"shop\":{\"id\":2,\"name\":\"\\\"testshop\\\"\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 成功
     */
    @Test
    public void modifyGrouponofSPU()throws Exception{

        //读出旧的groupon
        GrouponActivityPo oldPo = grouponActivityPoMapper.selectByPrimaryKey(2L);

        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/100")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

        //检测数据库是否真的发生修改
        GrouponActivityPo newPo = grouponActivityPoMapper.selectByPrimaryKey(2L);
        Assert.state(newPo.getStrategy().equals(oldPo.getStrategy()), "strategy未修改！");//is true 则断
        Assert.state(newPo.getBeginTime() == oldPo.getBeginTime(), "beginTime未修改！");
        Assert.state(newPo.getEndTime() == oldPo.getEndTime(), "endTime未修改！");
    }

    /**
     * 成功
     */
    @Test
    public void putGrouponOnShelves()throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/101/onshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        GrouponActivityPo newPo = grouponActivityPoMapper.selectByPrimaryKey(2L);
        Assert.state(newPo.getState()== ActivityStatus.ON_SHELVES.getCode().byteValue(), "状态未修改！");
    }

    /**
     * 成功
     */
    @Test
    public void cancelGrouponofSPU()throws Exception{
        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/goods/shops/1/groupons/101")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        GrouponActivityPo newPo = grouponActivityPoMapper.selectByPrimaryKey(12L);
        Assert.state(newPo.getState()== ActivityStatus.DELETED.getCode().byteValue(), "状态未修改！");
    }

    /**
     * 成功
     */
    @Test
    public void putGrouponOffShelves()throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/103/offshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //查看是否真的发生修改
        GrouponActivityPo newPo = grouponActivityPoMapper.selectByPrimaryKey(2L);
        Assert.state(newPo.getState()== ActivityStatus.OFF_SHELVES.getCode().byteValue(), "状态未修改");
    }

    /**
     * 无条件 成功
     */
    @Test
    public void customerQueryGroupons1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/groupons")
                .header("authorization",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name").value("b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name").value("e"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 无条件 成功
     */
    @Test
    public void adminQueryGroupons1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/shops/1/groupons")
                .header("authorization",token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name").value("b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[2].name").value("c"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[3].name").value("d"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[4].name").value("e"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[5].name").value("f"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[6].name").value("g"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[7].name").value("h"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[8].name").value("i"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 条件：beginTime,endTime
     */
    @Test
    public void adminQueryGroupons2() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(MockMvcRequestBuilders.get("/goods/shops/1/groupons")
                .header("authorization",token)
                .queryParam("beginTime", "2020-12-02 01:25:25")
                .queryParam("endTime", "2021-02-20 01:24:31"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg").value("成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name").value("b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name").value("d"))
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * beginTime > EndTime
     */
    @Test
    public void createGrouponofSPU1() throws Exception {

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2022-01-20 15:55:18");
        grouponVo.setEndTime("2022-01-09 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);//{"beginTime":"2022-01-20 15:55:18","endTime":"2022-01-09 15:55:18","strategy":"teststrategy"}

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/1/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString= "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);
    }

    /**
     * EndTime < now
     */
    @Test
    public void createGrouponofSPU2() throws Exception {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2018-01-20 15:55:18");
        grouponVo.setEndTime("2019-01-09 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);//{"beginTime":"2018-01-20 15:55:18","endTime":"2019-01-09 15:55:18","strategy":"teststrategy"}

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/1/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString= "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);
    }

    /**
     * spuId不存在
     */
    @Test
    public void createGrouponofSPU3() throws Exception {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2021-01-20 15:55:18");
        grouponVo.setEndTime("2021-01-09 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);//{"beginTime":"2021-01-20 15:55:18","endTime":"2021-01-09 15:55:18","strategy":"teststrategy"}

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/1/spus/1001/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * skuId存在，但不在此shop中
     */
    @Test
    public void createGrouponofSPU4() throws Exception {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2021-01-20 15:55:18");
        grouponVo.setEndTime("2021-01-31 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);//{"beginTime":"2021-01-20 15:55:18","endTime":"2021-01-09 15:55:18","strategy":"teststrategy"}

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/2/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 此spu当前已经参与了其他团购活动
     */
    @Test
    public void createGrouponofSPU5() throws Exception {
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2021-01-20 15:55:18");
        grouponVo.setEndTime("2021-01-31 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);//{"beginTime":"2021-01-20 15:55:18","endTime":"2021-01-09 15:55:18","strategy":"teststrategy"}

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/1/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }



    /**
     * state = 已上线，则活动状态禁止修改
     */
    @Test
    public void modifyGrouponofSPU1() throws Exception {


        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/105")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

}

    /**
     * shopId 无权限操作此 presaleId
     */
    @Test
    public void modifyGrouponofSPU2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/2/groupons/106")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 此团购已被逻辑删除
     */
    @Test
    public void modifyGrouponofSPU3() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/107")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 修改的beginTime > endTime
     */
    @Test
    public void modifyGrouponofSPU4() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/108")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString= "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);
    }

    /**
     * 修改的endTime早于now
     */
    @Test
    public void modifyGrouponofSPU5() throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        String strategy = "teststrategy";
        String beginTime = "2020-12-20 15:55:18";
        String endTime = "2022-01-05 15:55:18";

        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime(beginTime);
        grouponVo.setEndTime(endTime);
        grouponVo.setStrategy(strategy);
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/109")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString= "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);
    }


    /**
     * state不为已下线，则无法删除
     */
    @Test
    public void cancelGrouponofSPU1() throws Exception {
        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/goods/shops/1/groupons/110")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 此shopId无权操作此grouponId
     */
    @Test
    public void cancelGrouponofSPU2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(delete("/goods/shops/2/groupons/111")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }

    /**
     * 若预售状态不为已下线（已上线、已删除），则预售状态不允许上线
     */
    @Test
    public void putGrouponOnShelves1() throws Exception {
        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/112/onshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putGrouponOnShelves2() throws Exception {
        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/2/groupons/113/onshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse= "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 若预售状态不为已上线（已下线、已删除），则预售状态不允许下线
     */
    @Test
    public void putGrouponOffShelves1() throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/114/offshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putGrouponOffShelves2() throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/2/groupons/115/offshelves")
                .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":907,\"errmsg\":\"团购活动状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);

    }

}