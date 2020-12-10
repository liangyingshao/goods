package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.ActivityServiceApplication;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * description: 团购测试
 * date: 2020/12/9 15:15
 * author: 杨铭
 * version: 1.0
 */
@SpringBootTest(classes = ActivityServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ActivityControllerTest2 {


    @Autowired
    private MockMvc mvc;


    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void modifyGrouponofSPU1()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2020-12-20 15:55:18");
        grouponVo.setEndTime("2022-01-05 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);

        String responseString=this.mvc.perform(put("/goods/shops/1/groupons/1")
                .header("authorization",token)
                .contentType("application/json;charset=UTF-8")
                .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0, \"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        //测试是否真的改变
    }
}
