package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.time.format.DateTimeFormatter;

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

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    /**
     * description: 此api涉及内部其他模块的调用，暂时无法测过
     * version: 1.0
     * date: 2020/12/11 8:17
     * author: 杨铭
     *
     * @param
     * @return void
     */
    @Test
    public void createGrouponofSPU()throws Exception {
        String token = creatTestToken(1L, 0L, 100);
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2022-01-09 15:55:18");
        grouponVo.setEndTime("2022-01-20 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(MockMvcRequestBuilders.post("/goods/shops/1/spus/10/groupons")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":32,\"name\":null,\"goodsSpuPo\":{\"id\":1,\"name\":\"testspu\",\"brandId\":103,\"categoryId\":1,\"freightId\":null,\"shopId\":null,\"goodsSn\":\"hhh\",\"detail\":null,\"imageUrl\":null,\"state\":null,\"spec\":null,\"disabled\":null,\"gmtCreate\":\"2020-12-06T19:22:02\",\"gmtModified\":null},\"shop\":{\"id\":2,\"name\":\"\\\"testshop\\\"\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }


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

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/2")
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

    @Test
    public void putGrouponOnShelves()throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/2/onshelves")
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

    @Test
    public void putGrouponOffShelves()throws Exception {

        String token = creatTestToken(1L, 0L, 100);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/2/offshelves")
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
}