package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 获取活动所有状态
 * date: 2020/11/29 22:15
 * author: 秦楚彦 24320182203254
 * version: 1.0
 */
@Data
public class GoodsSpu {
    private Long id;

    private String name;

    private Long brandId;

    private Long categoryId;

    private Long freightId;

    private Long shopId;

    private String goodsSn;

    private String detail;

    private String imageUrl;



    /**
     * 商品SPU状态
     */
    public enum SpuState{
        OFFSHELF(0,"未上架"),
        ONSHELF(4,"上架"),
        DELETED(6,"已删除")
        ;

        private static final Map<Integer, SpuState> stateMap;

        static{//由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap=new HashMap();
            for(SpuState enum1:values()){
                stateMap.put(enum1.code,enum1);
            }
        }
        private int code;
        private String description;

        SpuState(int code, String description) {
            this.code = code;
            this.description = description;
        }
        public static SpuState getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private SpuState state= SpuState.OFFSHELF;

    private String spec;

//    private boolean disabled;//还有点问题哦

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public GoodsSpu(){

    }

    /**
     * 构造函数
     * @param po Po对象
     */
    public GoodsSpu(GoodsSpuPo po){


        this.id=po.getId();
        this.name=po.getName();
        this.brandId=po.getBrandId();
        this.categoryId=po.getCategoryId();
        this.freightId=po.getFreightId();
        this.shopId=po.getShopId();
        this.goodsSn=po.getGoodsSn();
        this.detail=po.getDetail();
        this.imageUrl=po.getImageUrl();
        if(null!=po.getState()){
            this.state=SpuState.getTypeByCode(po.getState().intValue());
        }
//        this.disabled=(po.getDisabled().intValue()==0)?false:true;
    }

    /**
     * 构造函数 用bo对象创建po对象
     * @return GoodsSpu
     * Created at 2020/12/01 00：46
     * Modified at 2020/12/02 20:13
     */
    public GoodsSpuPo createSpuPo(){
        GoodsSpuPo spuPo=new GoodsSpuPo();
        spuPo.setId(this.id);
        spuPo.setBrandId(this.brandId);
        spuPo.setCategoryId(this.categoryId);
        spuPo.setShopId(this.shopId);
        spuPo.setFreightId(this.freightId);
        spuPo.setDetail(this.detail);
//        spuPo.setDisabled(((this.disabled)?(byte)0:1));
        spuPo.setGmtCreate(this.gmtCreated);
        spuPo.setGmtModified(this.gmtModified);
        spuPo.setGoodsSn(this.goodsSn);
        spuPo.setImageUrl(this.imageUrl);
        spuPo.setName(this.name);
        spuPo.setState(state.getCode().byteValue());
        spuPo.setSpec(this.spec);
        return spuPo;
    }

}
