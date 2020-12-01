#---drop tables------------------------------------------------------------
drop table if exists DEVICETYPE_ATTR_REL_T;
drop table if exists DEVICE_ACCESS_REL_T;
drop table if exists DEVICE_ACCESS_T;
drop table if exists DEVICE_ATTRIBUTE_T;
drop table if exists DEVICE_INFO_T;
drop table if exists DEVICE_MANUFACTURER_T;
drop table if exists DEVICE_METHOD_T;
drop table if exists DEVICE_NAMEDMAPPING_T;
drop table if exists DEVICE_TYPE_T;
drop table if exists DEVICE_ERROR_T;
drop table if exists DEVICE_SIGNAL_T;
#------drop table---------------------------------------------------------------

#############        设备类型与属性关联关系表          ####################
create table if not exists DEVICETYPE_ATTR_REL_T
(
    TYPE_ID  VARCHAR(400) comment '设备型号ID',
    ATTR_KEY VARCHAR(400) comment '设备属性key',
    DOMAIN_ID int(8)     null comment '设备大类ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment ='设备类型与属性关联关系表';

create index IDX_DEVICE_ATTR_TYPE_REL933
    on DEVICETYPE_ATTR_REL_T (TYPE_ID);
create index IDX_DE_TYPE_ATTR_REL_AAAFA84
    on DEVICETYPE_ATTR_REL_T (ATTR_KEY);
###############################################################################

###############        设备安装从属关系表          ###########################
create table if not exists DEVICE_ACCESS_REL_T
(
    POLE_ID         VARCHAR(200) comment '灯杆ID',
    EDGE_ID         VARCHAR(200) comment '盒子ID',
    TERMINAL_ID     VARCHAR(200) comment '终端设备ID',
    TERMINAL_DOMAIN DECIMAL(3) comment '终端设备大类'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '设备安装从属关系表';
######################################################################

#############           设备安装接入状态表            ###################
create table if not exists DEVICE_ACCESS_T
(
    DEVICE_ID        VARCHAR(200) not null comment '设备ID'
        primary key,
    DEVICE_DOMAIN    INTEGER      not null comment '设备大类',
    REGISTRATION     DECIMAL(1)   not null comment '在网状态(1.注册/2.在网/0.注销)',
    PROTOCOL         VARCHAR(200) comment '协议(实际使用)',
    PROTOCOL_VERSION VARCHAR(200) comment '协议版本(实际使用)',
    DEVICE_IPV4      VARCHAR(400) comment 'IPv4',
    DEVICE_IPV6      VARCHAR(100) comment 'IPv6地址',
    DEVICE_PORT      VARCHAR(200) comment '接口(实际使用)',
    LONGITUDE        VARCHAR(200) comment '经度',
    LATITUDE         VARCHAR(200) comment '纬度',
    LOCATION_TYPE    VARCHAR(200) comment '地图格式',
    ACCESS_TIME      datetime     NULL DEFAULT NULL comment '注册激活时间',
    LEAVE_TIME       datetime     NULL DEFAULT NULL comment '离网注销时间',
    CREATE_TIME      TIMESTAMP    NULL DEFAULT NULL comment '首次触网时间',
    GATEWAY_ID       VARCHAR(100) comment '所属网关ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '设备安装接入状态表';
######################################################################

############          设备属性表               ######################
create table if not exists DEVICE_ATTRIBUTE_T
(
    ATTR_KEY   VARCHAR(200) not null comment '属性键 (产品注册用的标准商品名)'
        primary key,
    ATTR_NAME  VARCHAR(200) comment '属性名',
    VALUE_TYPE VARCHAR(100) comment '数据类型 java类名',
    UNIT       VARCHAR(100) comment '单位',
    STDUNIT    varchar(100) comment '标准单位',
    RATIO      int default 1 comment '衍生单位/标准单位比率，正为乘数，负为商数',
    METADATA   VARCHAR(200) comment '属性元数据 (描述性元数据,JSON格式)'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment ='设备属性表';
############################################################################

###############           设备表                 ###########################
create table if not exists DEVICE_INFO_T
(
    DEVICE_ID         VARCHAR(200) comment '设备ID 主键'
        primary key,
    SERIAL_NUMBER     VARCHAR(200) not null comment '设备SN 设备硬件序列号',
    DEVICE_NAME       VARCHAR(200) comment '设备名称 设备逻辑名',
    DEVICE_TYPE       VARCHAR(200) not null comment '设备型号',
    TYPE_DOMAIN       INT(8)       not null comment '设备大类',
    PRODUCT_DATE      datetime comment '生产日期',
    EXPIRE_DATE       datetime comment '到期时间 有效期（过期时间）',
    BATCH_NUMBER      VARCHAR(200) comment '生产批号 生产批号',
    BAR_CODE          VARCHAR(200) comment '商品条形码',
    METADATA          VARCHAR(2000) comment '扩展元数据',
    MAC_ADDR          VARCHAR(100) comment 'MAC地址',
    MANUFACTURER_CODE INT(8) comment '生厂商'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '设备表';

create index IDX_DEVICE_O_T_DEVICE_YPE9AFB
    on DEVICE_INFO_T (DEVICE_TYPE);
create index IDX_DEVICE_O_T_SERIAL_BERDC2D
    on DEVICE_INFO_T (SERIAL_NUMBER);
############################################################################

###################            生厂商表             ########################
create table if not exists DEVICE_MANUFACTURER_T
(
    MANUFACTURER_CODE INT(8)       not null comment '生产商编号'
        primary key,
    MANUFACTURER_NAME VARCHAR(200) not null comment '生产商名称 (企业名称)',
    ADDRESS           VARCHAR(200) comment '企业地址',
    CONTACT_INFO      VARCHAR(200) comment '企业联系方式',
    ATTR1             VARCHAR(200) comment '预留扩展',
    ATTR2             VARCHAR(200) comment '预留扩展',
    ATTR3             VARCHAR(200) comment '预留扩展'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '生厂商表';

create index IDX_DEVICE_R_T_MANUFACAMEF698
    on DEVICE_MANUFACTURER_T (MANUFACTURER_NAME);
############################################################################

#################        设备方法表           #########################
create table if not exists DEVICE_METHOD_T
(
    METHOD_ID          BIGINT not null comment '主键ID'
        primary key,
    METHOD_NAME        VARCHAR(200) comment '方法名',
    METHOD_IN          VARCHAR(200) comment '方法输入属性集',
    METHOD_OUT         VARCHAR(200) comment '方法输出属性集',
    METHOD_DESCRIPTION VARCHAR(200) comment '方法描述',
    DEVICE_TYPE        VARCHAR(8) comment '设备型号',
    DEVICE_DOMAIN      int         comment '设备大类'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '设备方法表';

create index IDX_DEVICE_D_T_DEVICE_YPEB704
    on DEVICE_METHOD_T (DEVICE_TYPE);
############################################################################

#################        字段映射表           #########################
create table if not exists DEVICE_NAMEDMAPPING_T
(
    ID            BIGINT       not null comment 'ID 条目ID'
        primary key,
    ATTR_KEY      VARCHAR(200) not null comment '属性键 产品注册用的标准商品名',
    KEY_ALIAS     VARCHAR(200) not null comment '属性别名 与标准模型中含义相同但是名称不同的定义',
    INFORMATION   VARCHAR(200) comment '必要描述 (描述与备注)',
    DEVICE_DOMAIN INT(8)       not null comment '设备大类 设备域(大类)',
    DEVICE_TYPE   VARCHAR(200) not null comment '设备类型编号 外键'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '字段映射表';

create index IDX_DEVICE_G_T_ATTR_IDD4F6
    on DEVICE_NAMEDMAPPING_T (ATTR_KEY);
############################################################################

#################        设备类型表           #########################
create table if not exists DEVICE_TYPE_T
(
    TYPE_CODE           VARCHAR(200) not null comment '设备型号编号 设备类型编号',
    TYPE_NAME           VARCHAR(200) comment '设备型号名称 (产品注册用的标准商品名)',
    DEVICE_DOMAIN       INT(8)       not null comment '设备域（大类） 设备大类',
    MANUFACTURER_CODE   INT(8)       not null comment '生产商编号 (企业代码，注意不是企业名称)',
    HARDWARE_VERSION    VARCHAR(200) comment '硬件版本',
    SOFTWARE_VERSION    VARCHAR(200) comment '软件版本',
    COMMUNICATION_PORTS VARCHAR(200) comment '支持的通信接口',
    SUPPORT_PROTOCOLS   VARCHAR(200) comment '支持的协议集合',
#     DEVICE_TYPE         VARCHAR(200) comment '设备型号-大类编号-厂商编号拼接',
    META                VARCHAR(200) comment '预留',
    primary key (TYPE_CODE, DEVICE_DOMAIN, MANUFACTURER_CODE)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment = '设备类型表';

create index IDX_DEVICE_E_T_DEVICE_AIND4B7
    on DEVICE_TYPE_T (DEVICE_DOMAIN);
create index IDX_DEVICE_E_T_MANUFACODE69EF
    on DEVICE_TYPE_T (MANUFACTURER_CODE);
create index IDX_DEVICE_E_T_TYPE_CODE32AA
    on DEVICE_TYPE_T (TYPE_CODE);
# create trigger TRG_DEVICE_E_T_I_CONCAT_TYPE
#     after insert
#     on DEVICE_TYPE_T
#     for each row
#     update DEVICE_TYPE_T
#     set DEVICE_TYPE=CONCAT(TYPE_CODE, '-', DEVICE_DOMAIN, '-', MANUFACTURER_CODE);
#########################################################################

#################        设备错误码表         #########################
create table if not exists DEVICE_ERROR_T
(
    code int(10)      not null comment '错误编码'
        primary key,
    name varchar(100) null comment '错误名称',
    msg  varchar(100) null comment '错误描述',
    level int(2)      null comment '信号等级' default -1
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment '设备错误码';
##########################################################################

#################        信号码表         #########################
create table if not exists DEVICE_SIGNAL_T
(
    code  int(10)      not null comment '信号编码'
        primary key,
    name  varchar(100) null comment '信号名称',
    msg   varchar(100) null comment '信号描述',
    level int(2)       null comment '信号等级' default 99
    # 0-50 0保留（置顶信号），1SOS（求救，灾难等），2关键信息(业务)，3关键信息（系统），4异常信息，5系统恢复信号，6重置/复位，
    # 7安全与审计类告警，8用户关注的信息，9-15预留，16触发式信号，17逻辑关联信号，18标记类信号，19-20预留，21记录类信号，。。>50背景信号
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 comment '设备信号码';
##########################################################################

#################        规则表         #########################
create table if not exists iotdb.RULE_INFO_T
(
    id               bigint auto_increment
        primary key,
    content          varchar(2048) not null comment '规则内容',
    create_time      datetime(6)   not null comment '创建时间',
    last_modify_time datetime(6)   null comment '修改时间',
    rule_key         varchar(255)  not null comment '规则key',
    rule_name        varchar(255)  null comment '规则名称',
    version          varchar(255)  null comment '版本',
    isenable         int(1)        null comment '是否启用',
    constraint UK_rule_rule_key
        unique (rule_key),
    constraint UK_rule_last_modify_time
        unique (last_modify_time),
    constraint UK_rule_version
        unique (version)
)
    comment '规则表'  charset = utf8;
##########################################################################

#######################  设备大类表   #################################
create table if not exists  iotdb.DEVICE_DOMAIN__T
(
    DEVICE_DOMAIN int(8)       not null comment '设备大类ID'
        primary key,
    DOMAIN_NAME   varchar(200) null comment '设备大类名称'
)
    comment '设备大类表' charset = utf8;
####　END　#################################################################
