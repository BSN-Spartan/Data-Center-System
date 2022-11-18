create table dc_payment_order
(
    `order_id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `chain_id`           bigint(20) DEFAULT NULL,
    `sale_price_id`      bigint(20) DEFAULT NULL,
    `payment_type_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT 'Payment method',
    `trade_no`           varchar(50)     NOT NULL DEFAULT '' COMMENT 'Transaction No. (created by the sytem)',
    `other_trade_no`     varchar(50)     NOT NULL DEFAULT '' COMMENT 'Third-party transaction No.',
    `payment_intent`     varchar(50)              DEFAULT '' COMMENT 'payment information',
    `account_address`    varchar(100)    NOT NULL DEFAULT '' COMMENT 'wallet address',
    `email`              varchar(50)     NOT NULL DEFAULT '' COMMENT 'email address',
    `currency`           varchar(20)     NOT NULL DEFAULT '' COMMENT 'Currency type when pay by fiat currency: USD......； when pay by stablecoins: USDC......',
    `ex_rates`           varchar(50)     NOT NULL DEFAULT '' COMMENT 'Gas Credit ratio',
    `gas_count`          decimal(56, 0)  NOT NULL DEFAULT '0' COMMENT 'Amount of Gas Credit',
    `pay_account`        varchar(100)    NOT NULL DEFAULT '' COMMENT 'Address for payment',
    `pay_amount`         decimal(56, 12) NOT NULL DEFAULT '0.000000000000' COMMENT 'Payment amount',
    `pay_state`          smallint(6) NOT NULL DEFAULT '0' COMMENT 'Payment status     0: payment in progress   1: payment successful 2: payment failed',
    `pay_time`           datetime                 DEFAULT NULL COMMENT 'Payment time',
    `tx_hash`            varchar(100)    NOT NULL DEFAULT '' COMMENT 'Transaction hash',
    `tx_time`            datetime                 DEFAULT NULL COMMENT 'Transaction time',
    `gas_tx_hash`        varchar(100)    NOT NULL DEFAULT '' COMMENT 'Gas Credit top-up transaction hash',
    `gas_tx_time`        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Gas Credit top-up time',
    `gas_recharge_state` smallint(6) NOT NULL DEFAULT '3' COMMENT 'Top-up status     0: Top-up in progress   1: Top-up successful 2: Top-up failed   3: Pending',
    `is_refund`          smallint(6) NOT NULL DEFAULT '0' COMMENT 'Refund 0: Not refunded 1: Refunded',
    `refund_amount`      decimal(56, 12) NOT NULL DEFAULT '0.000000000000' COMMENT 'Refund amount',
    `remarks`            varchar(200)    NOT NULL DEFAULT '' COMMENT 'Remarks',
    `update_time`        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update time',
    `create_time`        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    primary key (order_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'Gas payment order table';


create table dc_payment_refund
(
    refund_id       bigint          not null auto_increment,
    order_id        bigint,
    trade_no        varchar(50)     not null default '' comment 'Transaction No. (created by the sytem)',
    other_trade_no  varchar(50)     not null default '' comment 'Third-party transaction No.',
    account_address varchar(100)    not null default '' comment 'wallet address',
    refund_amount   decimal(56, 12) not null default 0 comment 'Refund amount',
    refund_state    smallint        not null default 0 comment 'Refund status     0: Refund in progress   1: Refund successful 2: Refund failed',
    refund_time     datetime comment 'Refund time',
    remarks         varchar(200)    not null default '' comment 'Remarks',
    refund_receipt  varchar(200)             DEFAULT '' COMMENT 'Refund receipt',
    operator        bigint          not null default 0 comment 'Operator',
    update_time     datetime        not null default CURRENT_TIMESTAMP comment 'Update time',
    create_time     datetime        not null default CURRENT_TIMESTAMP comment 'Created time',
    primary key (refund_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'GAS refund table';


create table dc_payment_type
(
    payment_type_id  bigint       not null auto_increment,
    pay_type         smallint     not null default 0 comment 'Payment type 1: Fiat currency 2: stablecoins   3: remittance',
    pay_channel_name varchar(50)  not null default '' comment 'Payment channel    stripe 、coinbase',
    channel_code     varchar(10)  not null default '' comment 'channel ID',
    private_key      varchar(300) not null default '' comment 'Private key',
    endpoint_secret  varchar(100) not null default '' comment 'Callback signature key',
    api_key          varchar(50)  not null default '' comment 'api_key',
    api_version      varchar(20)  not null default '' comment 'api_version',
    bank_name        varchar(50)  not null default '' comment 'Bank name',
    bank_account     varchar(50)  not null default '' comment 'Bank account',
    bank_address     varchar(50)  not null default '' comment 'Bank address',
    swift_code       varchar(50)  not null default '' comment 'swift code',
    enable_status    smallint(1) not null default 1 comment 'Enablement status 0:enabled 1:disabled',
    create_time      datetime     not null default CURRENT_TIMESTAMP comment 'Created time',
    update_time      datetime     not null default CURRENT_TIMESTAMP comment 'Update time',
    primary key (payment_type_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'Payment method table';


create table dc_system_conf
(
    conf_id     bigint        not null auto_increment,
    type        smallint      not null default 1 comment 'Type   1: Portal information   2: Technical support 3: contact us   4: chain access information',
    conf_code   varchar(50)   not null default '' comment 'conf_code',
    conf_value  varchar(1000) not null default '' comment 'conf_value',
    update_time datetime comment 'Update time',
    primary key (conf_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'Configuration information table';



create table chain_sale_price
(
    sale_price_id  bigint       not null auto_increment,
    chain_price_id bigint,
    chain_id       bigint,
    gas            decimal(56)  not null default 1 comment 'Gas Credit amount',
    sale_price     decimal(56)  not null default 0 comment 'Gas Credit Price (USD cent)',
    state          smallint     not null default 0 comment 'Status 1: pending review 2: approved 3: not approved',
    start_time     datetime comment 'Effective date',
    end_time       datetime comment 'Expiry date',
    create_user_id bigint       not null default 0 comment 'Created by',
    create_time    datetime     not null default CURRENT_TIMESTAMP comment 'Created time',
    check_user_id  bigint       not null default 0 comment 'Reviewed by',
    check_time     datetime     not null default CURRENT_TIMESTAMP comment 'Review time',
    check_remark   varchar(200) not null default "" comment 'Comment',
    primary key (sale_price_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'Price information table';



create table dc_chain_access
(
    chain_access_id    bigint      not null auto_increment,
    access_key         varchar(32) not null comment 'Access key   UUID',
    contacts_email     varchar(60) not null default '' comment 'email address',
    state              smallint             default 1 comment 'Status    1:enabled(default)，2 disabled，3 deleted',
    tps                int         not null default -1 comment 'TPS  -1 means no limits',
    tpd                int         not null default -1 comment 'TPD  -1 means no limits',
    stripe_customer_id varchar(50) not null default '' comment 'Stripe user id',
    create_time        datetime    not null default CURRENT_TIMESTAMP comment 'Created time',
    update_time        datetime    not null default CURRENT_TIMESTAMP comment 'Update time',
    notify_state       smallint             default 1 comment 'Notify Gateway Status 1: success 2: failed',
    primary key (chain_access_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 comment  'User access record table';

CREATE TABLE `sys_message_template`
(
    `template_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `msg_code`    varchar(50)   NOT NULL DEFAULT '' COMMENT 'Template ID',
    `msg_title`   varchar(100)           DEFAULT NULL COMMENT 'Template title',
    `msg_content` varchar(2000) NOT NULL DEFAULT '' COMMENT 'Template content',
    `msg_type`    smallint(6) NOT NULL DEFAULT '0' COMMENT 'Message type    1: Station letter 2: email  ',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update time',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `state`       smallint(6) NOT NULL DEFAULT '1' COMMENT 'Status，1: enabled       0: disabled',
    PRIMARY KEY (`template_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8 COMMENT='Email template table';

CREATE TABLE `sys_mail_record`
(
    `record_id`    bigint(20) NOT NULL AUTO_INCREMENT,
    `template_id`  bigint(20) DEFAULT NULL,
    `sender`       varchar(50)   NOT NULL DEFAULT '' COMMENT 'Sender',
    `receiver`     varchar(200)  NOT NULL DEFAULT '' COMMENT 'Recipient',
    `cc_people`    varchar(200)           DEFAULT NULL COMMENT 'CC',
    `mail_title`   varchar(100)  NOT NULL DEFAULT '' COMMENT 'Email title',
    `mail_content` varchar(1000) NOT NULL DEFAULT '' COMMENT 'Email content',
    `state`        smallint(6) NOT NULL DEFAULT '1' COMMENT 'Sending Status，1: successful 0: failed',
    `send_time`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Send time',
    PRIMARY KEY (`record_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8 COMMENT='Send email record table';

alter table dc_gas_recharge_record
    add order_id bigint after chain_id;


alter table dc_chain
    add chain_code varchar(20) not null default '' comment 'Chain request ID   unique:  After the business system is successfully configured, the gateway needs to be modified' after recharge_unit;
alter table dc_chain
    add gateway_type smallint not null default 0 comment 'whether support gateway    0: Not supported    1: supported' after chain_code;
alter table dc_chain
    add gateway_url varchar(200) not null default '' comment 'Gateway URL' after gateway_type;
ALTER TABLE dc_chain
    ADD COLUMN `ws_gateway_url` varchar(200) NOT NULL DEFAULT '' COMMENT 'websocketgateway URL' AFTER `gateway_url`;
ALTER TABLE dc_chain
    ADD COLUMN grpc_gateway_url varchar(200) NOT NULL DEFAULT '' COMMENT 'grpcgateway URL' AFTER `ws_gateway_url`;
alter table dc_chain
    add json_rpc smallint not null default 0 comment 'Json RPC  0: Not supported 1: Supported' after gateway_url;
alter table dc_chain
    add websocket smallint not null default 0 comment 'WebSocket 0: Not supported 1: Supported' after json_rpc;
alter table dc_chain
    add grpc smallint not null default 0 comment 'gRPC 0: Not supported 1: Supported' after websocket;

alter table sys_data_center
    add logo mediumtext comment 'logo' after dc_code;


INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (1, 'title', 'BSN Spartan Network Data Center Portal', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (1, 'headline', 'BSN Spartan Network Data Center Portal', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (1, 'introduce',
        'BSN Spartan Data Center is a lightweight software platform based on the Spartan Network. In the data center, the operator can install and access nodes of the public chain, the mission of the Spartan. By removing cryptocurrency from the first layer of the public chain, the mission of Spartan Network is to provide all IT systems around the world with public chain services for non-cryptocurrency, so as to gain the benefits of the infrastructure of this public IT system.',
        now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (1, 'copyright', '', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (2, 'Telegram', 'https://t.me/bsnsupport', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (2, 'Whatsapp', 'https://chat.whatsapp.com/H9wENt0lw4dFTVopaILFxh', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (2, 'Reddit', 'https://www.reddit.com/r/BSNSpartanSupport/', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (2, 'Discord', 'https://discord.gg/HEpnKTUyAX', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (2, 'Twitter', 'https://twitter.com/bsnbase', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (3, 'address', '36/F, PCCW Tower, Taikoo Place, 979 King’s Road, Quarry Bay, HK', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (3, 'email', 'BSN_Spartan@reddatetech.com.hk', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (3, 'phone', '+852 3978-1407  (UTC+8:00) 9:00 am to 5:00 pm (Mon–Fri)', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (4, 'tps', '', now());
INSERT INTO `dc_system_conf` (`type`, `conf_code`, `conf_value`, `update_time`)
VALUES (4, 'tpd', '', now());



INSERT INTO `dc_payment_type` (`pay_type`, `pay_channel_name`, `channel_code`, `private_key`, `endpoint_secret`,
                               `api_key`, `api_version`, `bank_name`, `bank_account`, `bank_address`, `swift_code`,
                               `enable_status`, `create_time`, `update_time`)
VALUES (1, 'Stripe', 'STRIPE', '', '', '', '', '', '', '', '', 0, now(), now());
INSERT INTO `dc_payment_type` (`pay_type`, `pay_channel_name`, `channel_code`, `private_key`, `endpoint_secret`,
                               `api_key`, `api_version`, `bank_name`, `bank_account`, `bank_address`, `swift_code`,
                               `enable_status`, `create_time`, `update_time`)
VALUES (2, 'Coinbase', 'COINBASE', '', '', '', '', '', '', '', '', 0, now(), now());
INSERT INTO `dc_payment_type` (`pay_type`, `pay_channel_name`, `channel_code`, `private_key`, `endpoint_secret`,
                               `api_key`, `api_version`, `bank_name`, `bank_account`, `bank_address`, `swift_code`,
                               `enable_status`, `create_time`, `update_time`)
VALUES (3, 'Remittance', 'OFFLINE', '', '', '', '', '', '', '', '', 0, now(), now());

INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('email_template', 'Email template',
        '<blockquote style=\"margin-top: 0px; margin-bottom: 0px; margin-left: 0.5em;\"><div><div><table align=\"center\" cellspacing=\"0\" cellpadding=\"0\" style=\"width: 600px;border: 1px solid #9B9B9B;\"><tbody><tr><td style=\"background-color:#013893;height:60px;\"><img src=\"${logo}\" style=\"margin:10px; height:36px;\"></td></tr><tr><td><table width=\"600\" style=\"padding-left: 24px;padding-right: 24px;\"><tbody><tr><td style=\"padding-top: 18px; font-size: 14px; color: #000000;\"><span>Hello there,</span></td></tr><tr><td style=\"padding-top: 24px; font-size: 14px; color: #000000;word-break:break-word;overflow: hidden;\"><span style=\"line-height:200%;\">${text}</span></td></tr>${contact}</tbody></table></td></tr></tbody></table><p style=\"text-align: center;font-size: 12px;line-height: 20px;color: #4A4A4A;\">This email is automatically sent by the system, please do not reply, and if you are not using BSN Spartan Network, please ignore.</p></div></div></blockquote>',
        2, now(), now(), 1);
INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('contact_template_', 'Contact information email template',
        '<tr><td style=\"font-size: 14px; color: #000000;padding-top: 15px;padding-bottom: 15px;\"><span>Thank you for using BSN Spartan Network!</span></td></tr><tr><td style=\"font-size: 14px; color: #000000;\"><span>If you have any questions, please contact us by the following ways:</span></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">-Telegram:&nbsp;<a target=\"_blank\" href=\"https://t.me/bsnsupport\">https://t.me/bsnsupport</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">-Whatsapp:&nbsp;<a target=\"_blank\" href=\"https://chat.whatsapp.com/H9wENt0lw4dFTVopaILFxh\">https://chat.whatsapp.com/H9wENt0lw4dFTVopaILFxh</td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">-Reddit:&nbsp;<a target=\"_blank\" href=\"https://www.reddit.com/r/BSNSpartanSupport/\">https://www.reddit.com/r/BSNSpartanSupport/</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">-Discord:&nbsp;<a target=\"_blank\" href=\"https://discord.gg/HEpnKTUyAX\">https://discord.gg/HEpnKTUyAX</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">-Email:&nbsp;<a href=\"mailto:BSN_Spartan_support@reddatetech.com.hk\">BSN_Spartan_support@reddatetech.com.hk</td></tr><tr><td>&nbsp;</td></tr>',
        2, now(), now(), 1);
INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('user_join_captcha_', 'BSN Spartan Network: your verification code is ${captcha_}',
        'Welcome to BSN Spartan Network!<br/>Your verification code is <b>${captcha_}</b>! This code is only valid for ${minutes_} minutes, if you request a new code, this code will no longer be valid.',
        2, now(), now(), 1);
INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('gas_recharge_captcha_', 'BSN Spartan Network: your verification code is ${captcha_}',
        'Welcome to BSN Spartan Network!<br/>Your verification code is <b>${captcha_}</b>! This code is only valid for ${minutes_} minutes, if you request a new code, this code will no longer be valid.',
        2, now(), now(), 1);
INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('user_join_email_', 'BSN Spartan Data Center Notification: Nodes Information',
        'Welcome to BSN Spartan Network! <br/>\r\nYou can access to BSN Spartan Network via these nodes:<br/><br/>\r\n\r\nSpartan-I(Powered by NC Ethereum)<br/>\r\nAccess Key:${accessKey_}<br/>\r\nAccess Url Example:${accessDemo_}<br/>\r\nJson RPC Access URL:${ethRpc_}<br/>\r\nWebSocket Access URL:${ethWs_}<br/><br/>\r\n\r\nSpartan-II(Powered by NC Cosmos)\r\nAccess Key:${accessKey_}<br/>\r\nJson RPC:<br/>EVM module:${cosmosEvmRpc_}<br/>\r\n         Native module:${cosmosRpc_}<br/>\r\nWebSocket:<br/>EVM module:${cosmosEvmWs_}<br/>\r\n          Native module:${cosmosWs_}<br/>\r\ngRPC:${cosmosGrpc_}<br/><br/>\r\n\r\n\r\nSpartan-III(Powered by NC PolygonEdge)\r\nAccess Key:${accessKey_}<br/>\r\nJson RPC Access URL:${poloygonRpc_}<br/>\r\nWebSocket Access URL:${polygonWs_}<br/><br/>',
        2, now(), now(), 1);
INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,