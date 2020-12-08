package cn.edu.xmu.goods.controller;

import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.goods.GoodsServiceApplication;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * description: 品牌测试类
 * date: 2020/12/2 20:46
 * author: 张悦
 * version: 1.0
 */
@SpringBootTest(classes = GoodsServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoodsControllerTest1 {

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(GoodsControllerTest1.class);
    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    /**
     * description: 新增品牌 （成功） 
     * version: 1.0 
     * date: 2020/12/2 20:47 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void insertBrandTest1()throws Exception {
        BrandVo vo = new BrandVo();
        vo.setName("test");
        vo.setDetail("test");
        String token = creatTestToken(1L, 0L, 100);
        String brandJson = JacksonUtil.toJson(vo);

        System.out.print(brandJson);

        String responseString = this.mvc.perform(post("/goods/shops/0/brands").header("authorization", token).contentType("application/json;charset=UTF-8").content(brandJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"name\": \"test\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": test\n" +
                "  }\n" +
                "}";
        //auto_increment = 120
            JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * description: 新增品牌 品牌名称已存在 
     * version: 1.0 
     * date: 2020/12/2 20:47 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */  
    @Test
    public void insertBrandTest2() {
        BrandVo vo = new BrandVo();
        vo.setName("戴荣华");
        String token = creatTestToken(1L, 0L, 100);
        String brandJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/goods/shops/0/brands").header("authorization", token).contentType("application/json;charset=UTF-8").content(brandJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":990,\"errmsg\":\"品牌名称已存在：戴荣华\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * description: 新增品牌 品牌名称为空 
     * version: 1.0 
     * date: 2020/12/2 20:48 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void insertBrandTest3() throws Exception{
        BrandVo vo = new BrandVo();
        vo.setName("");
        vo.setDetail("test");
        String token = creatTestToken(1L, 0L, 100);
        String brandJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        responseString = this.mvc.perform(post("/goods/shops/0/brands").header("authorization", token).contentType("application/json;charset=UTF-8").content(brandJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503,\"errmsg\":\"品牌名称不能为空;\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 上传品牌图片（成功）
     * version: 1.0 
     * date: 2020/12/2 20:55 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void uploadFileSuccess() throws Exception{
        String token = creatTestToken(1L,0L,100);
        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/0/brands/72/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 上传品牌图片（品牌id不存在)
     * version: 1.0
     * date: 2020/12/2 20:55
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void UploadFileFail1() throws Exception{
        String token = creatTestToken(1111L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/0/brands/80/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 上传品牌图片（文件格式错误）
     * version: 1.0
     * date: 2020/12/2 20:56
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void UploadFileFail2() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"文本文件.txt");
        MockMultipartFile firstFile = new MockMultipartFile("img", "文本文件.txt" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/0/brands/72/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 上传品牌图片（文件格式错误，伪装成图片）
     * version: 1.0
     * date: 2020/12/2 20:57
     * author: 张悦
     *
     * @param
     * @return void
     */
    @Test
    public void UploadFileFail3() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"伪装的图片.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "伪装的图片.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/0/brands/72/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 上传品牌图片失败（大小超限）
     * version: 1.0 
     * date: 2020/12/2 20:57 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void UploadFileFail4() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"大小超限图片.jpg");
        MockMultipartFile firstFile = new MockMultipartFile("img", "大小超限图片.jpg" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/goods/shops/0/brands/72/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":509,\"errmsg\":\"图片大小超限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    
    /**
     * 获取所有品牌（第一页）
     * @throws Exception
     */
    @Test
    public void findAllBrands1() throws Exception{
        String responseString = this.mvc.perform(get("/goods/brands").
                queryParam("page", "1").queryParam("pageSize","2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllBrands1Success.json")));
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 获取所有品牌（第二页）
     * @throws Exception
     */
    @Test
    public  void findAllBrands2() throws Exception {
        String responseString = this.mvc.perform(get("/goods/brands").
                queryParam("page", "2").queryParam("pageSize","1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllBrands2Success.json")));
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    
    /**
     * description: 修改品牌 (查无此品牌) 
     * version: 1.0 
     * date: 2020/12/2 20:48 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void modifyNilBrand() throws Exception {
        BrandVo vo = new BrandVo();
        vo.setName("test");
        vo.setDetail("test");
        String token = creatTestToken(1L, 0L, 100);
        String goodsCategoryJson = JacksonUtil.toJson(vo);

        String responseString = this.mvc.perform(
                put("/goods/shops/0/brands/99")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(goodsCategoryJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 修改品牌(品牌名称已存在) 
     * version: 1.0 
     * date: 2020/12/2 20:48 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void modifyBrandDuplicateName() throws Exception {

        BrandVo vo = new BrandVo();
        vo.setName("范敏祺");
        String token = creatTestToken(1L, 0L, 100);
        String brandJson = JacksonUtil.toJson(vo);

        String responseString  = this.mvc.perform(
                put("/goods/shops/0/brands/71")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(brandJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse = "{\"errno\":990,\"errmsg\":\"品牌名称已存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * description: 删除品牌(成功) 
     * version: 1.0 
     * date: 2020/12/2 20:48 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void deleteBrandTest1() {
        //测试数据
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        //测试删除成功
        try {
            responseString = this.mvc.perform(delete("/goods/shops/0/brands/72").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * description: 删除品牌(id不存在)
     * version: 1.0 
     * date: 2020/12/2 20:49 
     * author: 张悦 
     * 
     * @param 
     * @return void
     */ 
    @Test
    public void deleteBrandTest2() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/goods/shops/0/brands/1").header("authorization", token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
