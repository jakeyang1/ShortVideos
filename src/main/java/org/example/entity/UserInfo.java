package org.example.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_info")
public class UserInfo implements Serializable {

    private static final  long serialVersionUID = 1l;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String city;

    private String introduce;

    private Long fans;

    private Integer followee;

    private Boolean gender;

    private LocalDate birthday;

    private Long credits;

    //表示两种状态，一种是0级别，一种是1-9级，
    private Boolean level;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
