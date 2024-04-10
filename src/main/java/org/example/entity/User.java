package org.example.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
public class User implements Serializable{

    private static final long serialVersionUID = 1l;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer  phone;

    private String password;

    private String nickName;

    private String icon;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}