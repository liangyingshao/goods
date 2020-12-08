package cn.edu.xmu.activity.model.vo;

import lombok.Data;

@Data
public class ModifiedBy {
    private Long id;
    private String username;
    public void set(Long id,String username)
    {
        this.id = id;
        this.username=username;
    }
}
