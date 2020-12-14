package cn.edu.xmu.activity.controller;

import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;



@SpringBootTest(classes = Application.class)
@Slf4j
public class YangMingTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    public YangMingTest(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    private String login(String userName, String password) throws Exception {
//        LoginVo vo = new LoginVo();
//        vo.setUserName(userName);
//        vo.setPassword(password);
//        String requireJson = JacksonUtil.toJson(vo);
//        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo("成功")
//                .returnResult()
//                .getResponseBodyContent();
//        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        return new JwtHelper().createToken(1L, 0L, 100);
    }


    @Test
    public void createGrouponofSPU() throws Exception {

        //一条店铺，一条spu
        GrouponVo grouponVo = new GrouponVo();
        grouponVo.setBeginTime("2022-01-09 15:55:18");
        grouponVo.setEndTime("2022-01-20 15:55:18");
        grouponVo.setStrategy("teststrategy");
        String Json = JacksonUtil.toJson(grouponVo);


        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/goods/shops/1/spus/10/groupons").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }









    /**
     * 测试克隆模板功能
     * 资源不存在
     *
     * @throws Exception
     */
    @Test
    public void cloneFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/200/clone").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试克隆模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    public void cloneFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/9/clone").header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        String temp = new String(responseString, "UTF-8");
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/"+id).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(new String(queryResponseString, "UTF-8"), new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试克隆模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    public void cloneFreightModel2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/13/clone").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试定义默认模板功能
     * 操作资源不存在
     *
     * @throws Exception
     */
    @Test
    public void defineDefaultFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/200/default").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试定义默认模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    public void defineDefaultFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/9/default").header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/9").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"),false);
    }

    /**
     * 测试定义默认模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    public void defineDefaultFreightModel2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels/13/default").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }


    /**
     * 测试定义模板功能
     *
     * @throws Exception
     */
    @Test
    public void defineFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"测试名\",\"type\":0,\"unit\":500}";

        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        String temp = new String(responseString, "UTF-8");
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/"+id).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(new String(queryResponseString, "UTF-8"), new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试定义模板功能
     * 模板名重复
     *
     * @throws Exception
     */
    @Test
    public void defineFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"测试模板5\",\"type\":0,\"unit\":500}";
        byte[] responseString = manageClient.post().uri("/shops/1/freightmodels").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":802,\"errmsg\":\"运费模板名重复\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void getFreightModelSummary() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/200").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void getFreightModelSummary1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/9").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    @Test
    public void getFreightModelSummary2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/freightmodels/13").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    @Test
    public void getFreightModels() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/1/freightmodels").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":4,\"pages\":1,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void modifyFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"测试名\",\"unit\":500}";
        byte[] responseString = manageClient.put().uri("/shops/1/freightmodels/200").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    @Test
    public void modifyFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"测试模板3\",\"unit\":550}";

        byte[] responseString = manageClient.put().uri("/shops/1/freightmodels/12").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":802,\"errmsg\":\"运费模板名重复\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void modifyFreightModel2() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"模板修改测试名\",\"unit\":550}";
        byte[] responseString = manageClient.put().uri("/shops/1/freightmodels/9").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/9").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(expectedResponse,new String(queryResponseString, "UTF-8"), false);
    }

    @Test
    public void modifyFreightModel3() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{\"name\":\"模板修改测试名\",\"unit\":550}";
        byte[] responseString = manageClient.put().uri("/shops/1/freightmodels/13").header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    @Test
    public void deleteFreightModel() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/freightmodels/200").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    @Test
    public void deleteFreightModel1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/freightmodels/10").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freightmodels/10").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"), true);

    }

    @Test
    public void deleteFreightModel2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/freightmodels/13").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        var x = new String(responseString, "UTF-8");
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }


}
