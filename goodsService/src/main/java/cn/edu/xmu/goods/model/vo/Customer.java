package cn.edu.xmu.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "根据customerid查出的customer信息")
public class Customer {
    @NotNull(message = "id不得为空")
    private Long id;

    @NotNull(message = "userName不得为空")
    private String userName;

    @NotNull(message = "name不得为空")
    private String name;
}
