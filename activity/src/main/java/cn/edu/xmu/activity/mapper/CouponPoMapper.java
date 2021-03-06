package cn.edu.xmu.activity.mapper;

import cn.edu.xmu.activity.model.po.CouponPo;
import cn.edu.xmu.activity.model.po.CouponPoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    long countByExample(CouponPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int deleteByExample(CouponPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int insert(CouponPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int insertSelective(CouponPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    List<CouponPo> selectByExample(CouponPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    CouponPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") CouponPo record, @Param("example") CouponPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") CouponPo record, @Param("example") CouponPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(CouponPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table coupon
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CouponPo record);
}