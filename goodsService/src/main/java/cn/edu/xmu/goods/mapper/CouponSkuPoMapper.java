package cn.edu.xmu.goods.mapper;

import cn.edu.xmu.goods.model.po.CouponSkuPo;
import cn.edu.xmu.goods.model.po.CouponSkuPoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponSkuPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    long countByExample(CouponSkuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int deleteByExample(CouponSkuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int insert(CouponSkuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int insertSelective(CouponSkuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    List<CouponSkuPo> selectByExample(CouponSkuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    CouponSkuPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") CouponSkuPo record, @Param("example") CouponSkuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") CouponSkuPo record, @Param("example") CouponSkuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(CouponSkuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon_sku
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CouponSkuPo record);
}