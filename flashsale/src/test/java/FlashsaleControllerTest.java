import cn.edu.xmu.flashsale.FlashsaleServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FlashsaleServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class FlashsaleControllerTest {
    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    public void createflash() {
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        String str = "2021-01-07";
        try {
            responseString = this.mvc.perform(post("/flashsale/timesegments/1/flashsales?flashDate="+str).header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"flashDate\":\"2021-01-07T00:00:00\",\"timeDTO\":{\"id\":null,\"type\":null,\"beginTime\":null,\"endTime\":null},\"gmtCreate\":\"2020-12-09T19:53:01\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteflashsale() {
        String responseString = null;
        String token = creatTestToken(1L,0L,100);
        try {
            responseString = this.mvc.perform(delete("/flashsale/flashsales/1").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}