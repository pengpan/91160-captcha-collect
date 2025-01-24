package com.github.pengpan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 验证码采集表
 * </p>
 *
 * @author pengpan
 * @since 2025-01-23 22:50:49
 */
@Getter
@Setter
@ToString
@TableName("t_captcha_collect")
public class CaptchaCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 验证码图片，base64编码
     */
    private String image;

    /**
     * 验证码结果
     */
    private String code;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
