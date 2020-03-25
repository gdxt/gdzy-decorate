DROP TABLE IF EXISTS `gdzy_user`;
CREATE TABLE `gdzy_user`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `nick_name` VARCHAR(100) COMMENT'微信用户名',
  `gender` VARCHAR(2) COMMENT'性别',
  `city` varchar(32)  COMMENT '城市',
  `province` varchar(32)  COMMENT '省份',
  `country` varchar(32)  COMMENT '国家',
  `avatar_url` varchar(255)  COMMENT '头像图片',
  `wx_openid` varchar(32) COMMENT '微信小程序唯一用户标示',
  `wx_unionid` varchar(32)  COMMENT '公众平台用户唯一标示',
  `phone` varchar(11) COMMENT '电话号',
  `is_delete` varchar(1) DEFAULT '1' COMMENT '删除标记:未删除-UNDEL-0,删除-DEL-1',
  `create_time` timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_answer` int(1) DEFAULT 0 COMMENT '答题标记:未答题-ON-0,答题-YES-1',
   PRIMARY KEY (`id`),
   key(`openId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '中悦小程序用户表';