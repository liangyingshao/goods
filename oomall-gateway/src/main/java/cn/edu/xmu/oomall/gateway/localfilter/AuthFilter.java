package cn.edu.xmu.oomall.gateway.localfilter;

import cn.edu.xmu.oomall.gateway.util.JwtHelper;
import cn.edu.xmu.oomall.gateway.util.GatewayUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wwc
 * @date Created in 2020/11/13 22:31
 **/
public class AuthFilter implements GatewayFilter, Ordered {
    private  static  final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private String tokenName;

    public AuthFilter(Config config){
        this.tokenName = config.getTokenName();
    }

    /**
     * 局部过滤器，商城网关仅判断是否登录不校验权限
     * @param exchange
     * @param chain
     * @return
     * @author wwc
     * @date 2020/12/02 17:13
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求参数
        String token = request.getHeaders().getFirst(tokenName);
        RequestPath url = request.getPath();
        HttpMethod method = request.getMethod();
        // 判断token是否为空，无需token的url在配置文件中设置
        logger.debug("filter: token = " + token);
        if (StringUtil.isNullOrEmpty(token)){
            // 设置返回消息
            JSONObject message = new JSONObject();
            message.put("errno", 403);
            message.put("errmsg", "token为空");
            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.writeWith(Mono.just(buffer));
        }
        // 判断token是否合法
        JwtHelper.UserAndDepart userAndDepart = new JwtHelper().verifyTokenAndGetClaims(token);
        if (userAndDepart == null) {
            // 若token解析不合法
            // 设置返回消息
            JSONObject message = new JSONObject();
            message.put("errno", 503);
            message.put("errmsg", "token不合法");
            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.just(buffer));
        } else {
            // 若token合法
            // 解析userid和departid和有效期
            Long userId = userAndDepart.getUserId();
            Long departId = userAndDepart.getDepartId();
            Date expireTime = userAndDepart.getExpTime();
            String jwt = token;
            // 若不为商城用户
            if (!departId.equals(-2L)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                // 设置返回消息
                JSONObject message = new JSONObject();
                message.put("errno", 503);
                message.put("errmsg", "用户类型不对");
                byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                //指定编码，否则在浏览器中会中文乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(buffer));
            }
            // 获取redis工具
            RedisTemplate redisTemplate = GatewayUtil.redis;
            // 判断该token是否被ban
            String[] banSetName = {"BanJwt_0", "BanJwt_1"};
            for (String singleBanSetName : banSetName) {
                // 若redis有该banSetname键则检查
                if (redisTemplate.hasKey(singleBanSetName)) {
                    // 若redis有该banSetname键则检查
                    // 获取全部被ban的jwt,若banjwt中有该token则拦截该请求
                    if (redisTemplate.opsForSet().isMember(singleBanSetName, token)) {
                        // 设置返回消息
                        JSONObject message = new JSONObject();
                        message.put("errno", 503);
                        message.put("errmsg", "token被ban");
                        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = response.bufferFactory().wrap(bits);
                        //指定编码，否则在浏览器中会中文乱码
                        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.writeWith(Mono.just(buffer));
                    }
                }
            }
            // 判断该用户是否被封禁
            if (redisTemplate.hasKey("banUser_" + userId)) {
                // 设置返回消息
                JSONObject message = new JSONObject();
                message.put("errno", 503);
                message.put("errmsg", "用户被ban");
                byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bits);
                //指定编码，否则在浏览器中会中文乱码
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(buffer));
            }
            // 判断该token有效期是否还长
            Long sec = expireTime.getTime() - System.currentTimeMillis();
            if (sec < GatewayUtil.getRefreshJwtTime() * 1000) {
                // 若快要过期了则重新换发token
                // TODO 旧的token未ban掉
                JwtHelper jwtHelper = new JwtHelper();
                jwt = jwtHelper.createToken(userId, departId, GatewayUtil.getJwtExpireTime());
                logger.debug("重新换发token:" + jwt);
            }
            // 将token放在返回消息头中
            response.getHeaders().set(tokenName, jwt);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        private String tokenName;

        public Config(){

        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }
    }
}
