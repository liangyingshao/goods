package cn.edu.xmu.goods.mapper;

import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodsSpuPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    long countByExample(GoodsSpuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int deleteByExample(GoodsSpuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int insert(GoodsSpuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int insertSelective(GoodsSpuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    List<GoodsSpuPo> selectByExample(GoodsSpuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    GoodsSpuPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") GoodsSpuPo record, @Param("example") GoodsSpuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") GoodsSpuPo record, @Param("example") GoodsSpuPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(GoodsSpuPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_spu
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(GoodsSpuPo record);
}