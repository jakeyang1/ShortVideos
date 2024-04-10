package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop_type")
public class ShopType  implements Serializable {

       private static final  long serialVersion = 1L;

       @TableId(value = "id", type = IdType.AUTO)
       private Long id;

       private String name;

       private String icon;

       //顺序
       private Integer sort;

       @JsonIgnore
       private LocalDateTime createTime;

       @JsonIgnore
       private LocalDateTime updateTime;


}
