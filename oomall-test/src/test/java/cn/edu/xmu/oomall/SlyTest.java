package cn.edu.xmu.oomall;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.test.TestApplication;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SlyTest {

    private static final Logger logger = LoggerFactory.getLogger(SlyTest.class);

    private WebTestClient manageClient;//发给权限的网关

    private WebTestClient mallClient;

    private static  String adminToken = "";
    private static String userToken;

    @Autowired
    private ObjectMapper mObjectMapper;

    public SlyTest(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://127.0.0.1:8091")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    @BeforeAll
    private static void login(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L,0L, 3600);
        userToken =jwtHelper.createToken(59L,1L, 3600);
    }

    /**
     * Flashsale002
     * 正确增加三天后的秒杀
     * @throws Exception
     */
    @Test
    public void createflash1() throws Exception {
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 不允许增加过去和今天的秒杀
     * @throws Exception
     */
    @Test
    public void createflash2() throws Exception {
        //增加昨天的秒杀
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加不存在时段id的秒杀
     * @throws Exception
     */
    @Test
    public void createflash3() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/-1/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加flashDate+segment_id已经存在的秒杀
     * @throws Exception
     */
    @Test
    public void createflash4() throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2021-12-17 0:25:58",df);
        //增加秒杀
        String requestJson = "{\"flashDate\":\""+ ldt.toString() +"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale004
     * 删除不存在的秒杀
     * @throw Exception
     */
    @Test
    public void deleteflashsale1() throws Exception {
        byte[] responseBuffer = null;
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/-1").exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale005
     * 不允许将秒杀时间修改为过去时间
     */
    @Test
    public void updateflashsale1() throws Exception {
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2019-12-17 00:00:00",df);
        //修改秒杀
        requestJson = "{\"flashDate\":\""+ ldt.toString()+"}";
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale005
     * 不允许修改过去的秒杀活动
     */
    @Test
    public void updateflashsale2() throws Exception {
        //运行sql增加一条过去的记录

        int pastId = 99999;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2021-12-17 00:00:00",df);
        String requestJson = "{\"flashDate\":\""+ ldt.toString()+"}";
        byte[] responseBuffer = null;

        //修改秒杀
        requestJson = "{\"flashDate\":\""+ ldt.toString()+"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/0/flashsales/"+pastId).header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 正常上线秒杀活动
     */
    public void flashsaleState1() throws Exception {
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //将创建的秒杀活动的状态设置为上线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 不允许修改被删除的秒杀的状态
     */
    public void flashsaleState2() throws Exception {
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //删除该下线状态的秒杀
        res = manageClient.delete().uri("/shops/0/flashsales/{id}"+insertId).header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        //修改被删除的秒杀的状态
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.DELETE_CHANGAE_NOTALLOW.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 不允许删除已上线的活动,只能删除下线状态的活动
     */
    public void flashsaleState3() throws Exception {
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加秒杀
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //将创建的秒杀活动的状态设置为已上线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        //删除已上线的秒杀活动
        res = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.DELETE_ONLINE_NOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        //将创建的秒杀活动的状态设置为下线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/offshelves").header("authorization",adminToken);
        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        //删除该活动
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 向已存在的秒杀活动中增加一个秒杀商品
     */
    public void addSKUofTopic1() throws Exception {
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();


    }

    /**
     * 向不存在的秒杀活动中增加一个秒杀商品
     */

    /**
     * 向已存在的秒杀活动中增加一个不存在的商品
     */

    /**
     * 向已存在的秒杀活动中增加一个秒杀商品，错误的参数校验
     */


    /**
     * 成功新增商品评论
     * @throws Exception
     */
    @Test
    public void addSkuComment1() throws Exception{
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/828/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，该订单条目已评论
     * @throws Exception
     */
    @Test
    public void addSkuComment2() throws Exception {
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/829/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();

        res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COMMENT_EXISTED.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，订单条目不存在
     * @throws Exception
     */
    @Test
    public void addSkuComment3() throws Exception {
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/8888/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，参数错误
     * @throws Exception
     */
    @Test
    public void addSkuComment4() throws Exception {
        String requestJson = "{\"type\":\"6\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/830/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核评论，通过评论
     * @throws Exception
     */
    @Test
    public void auditComment1() throws Exception {
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/830/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        requestJson = "{\"conclusion\":\"true\"}";
        res = manageClient.put().uri("/shops/0/comments/"+insertId+"/confirm").header("authorization",adminToken).bodyValue(requestJson);
        //审核
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核评论，不通过评论
     * @throws Exception
     */
    @Test
    public void auditComment2() throws Exception {
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/goods/orderitems/831/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        requestJson = "{\"conclusion\":\"true\"}";
        res = manageClient.put().uri("/shops/0/comments/"+insertId+"/confirm").header("authorization",adminToken).bodyValue(requestJson);
        //审核
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,店铺id不存在
     * @throws Exception
     */
    @Test
    public void add_floating_price1() throws Exception{
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\"2020-12-28 17:42:20\",\"endTime\":\"2021-1-28 17:42:20\",\"quantity\": \"1000\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/goods/shops/23333/skus/626/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,SKU_ID不存在
     * @throws Exception
     */
    @Test
    public void add_floating_price2() throws Exception{
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\"2020-12-28 17:42:20\",\"endTime\":\"2021-1-28 17:42:20\",\"quantity\": \"1000\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/goods/shops/828/skus/23333/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,时间段冲突
     * @throws Exception
     */
    @Test
    public void add_floating_price3() throws Exception{
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\"2020-12-28 17:42:20\",\"endTime\":\"2021-1-28 17:42:20\",\"quantity\": \"1000\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/goods/shops/828/skus/626/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();

        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 失效不存在的价格浮动项 1217
     * @throws Exception
     */
    @Test
    public void invalidFloatPrice1() throws Exception{
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/goods/shops/0/floatPrices/1217").header("authorization",adminToken);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 失效价格浮动项
     * @throws Exception
     */
    @Test
    public void invalidFloatPrice2() throws Exception{
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/goods/shops/0/floatPrices/828").header("authorization",adminToken);
        //增加
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$data").isEmpty()
                .returnResult()
                .getResponseBodyContent();
    }
}
