package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
import cn.edu.xmu.goods.model.vo.CommentSimpleRetVo;
import cn.edu.xmu.goods.model.vo.CommentStateRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Comment implements VoObject {

    /**
     * 业务: 构造函数
     * @param po
     * @return
     * @author: 24320182203259 邵良颖
     * Created at: 2020-12-01 15:03
     * version: 1.0
     */
    public Comment(CommentPo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.orderitemId = po.getOrderitemId();
        this.type = po.getType();
        this.content = po.getContent();
        this.state = po.getState();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public Comment() {

    }

    @Override
    public Object createVo() {
        return new CommentRetVo(this);
    }

    @Override
    public CommentSimpleRetVo createSimpleVo() {
        return new CommentSimpleRetVo(this);
    }

    /**
     * 评论状态
     */
    public enum State {
        TOAUDIT(0, "未审核"),
        AUDITPASS(1, "评论成功"),
        AUDITFAIL(2, "未通过");
        // DELETE(3, "废弃");

        private static final Map<Integer, Comment.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Comment.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Comment.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 评论类型
     */
    public enum Type {
        GOOD(0, "好评"),
        MEDIUM(1, "中评"),
        BAD(2, "差评");

        private static final Map<Integer, Comment.Type> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Comment.Type enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Comment.Type getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private Long orderitemId;

    private Byte type;

    private String content;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}