package cn.edu.xmu.flashsale.mapper;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.po.FlashSalePoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FlashSalePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    long countByExample(FlashSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int deleteByExample(FlashSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int insert(FlashSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int insertSelective(FlashSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    List<FlashSalePo> selectByExample(FlashSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    FlashSalePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") FlashSalePo record, @Param("example") FlashSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") FlashSalePo record, @Param("example") FlashSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(FlashSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table flash_sale
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(FlashSalePo record);
}