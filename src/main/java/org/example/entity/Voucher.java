package org.example.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher")
public class Voucher implements Serializable {

    private static  final  long  serialVerionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private  Long id;

    private Long ShopId;

    private String title;

    private String rules;

    private Long payValue;

    private Long ActualValue;

    //优惠劵类型
    private Integer type;

    //优惠劵类型
    private Integer status;

    @TableField(exist = false)
    private Integer stock;

    @TableField(exist = false)
    private LocalDateTime beginTime;

    @TableField(exist = false)
    private LocalDateTime endtime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
