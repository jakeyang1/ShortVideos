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


@Data//automatic generation get or set
@EqualsAndHashCode(callSuper = false)//automatic generation equals or hashcode
@Accessors(chain = true) //
@TableName("tb-blog")//linked database table
public class Blog implements Serializable {

       private static final long serialVersionUID = 1L;

       @TableId(value = "id", type = IdType.AUTO)
       private  Long  id;

       private  Long shopid;

       private  Long userid;

       @TableField(exist = false)
       private  String icon;

       @TableField(exist = false)
       private  String name;

       @TableField(exist = false)
       private  Boolean islike;

       private  String title;

       private  String images;

       private  String content;

       private  Integer liked;

       private  Integer comments;

       private LocalDateTime createTime;

       private LocalDateTime updateTime;









}
