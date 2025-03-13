-- auto-generated definition
create table t_captcha_collect
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    image       text                                not null comment '验证码图片，base64编码',
    code        varchar(10)                         not null comment '验证码结果',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '验证码采集表';