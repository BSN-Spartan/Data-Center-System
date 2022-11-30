
alter table dc_payment_order modify `gas_tx_time` datetime DEFAULT NULL COMMENT 'Gas Credit top-up time';

UPDATE `dc_system_conf` SET `conf_value`='BSN Spartan Data Center is a lightweight software platform that provides access to the Spartan Network. In the data center, the operator can install and access nodes of the public chain, the mission of the Spartan. By removing cryptocurrency from the first layer of the public chain, the mission of the Spartan Network is to provide all IT systems around the world with non-cryptocurrency public chain services, so as to to gain the benefits of a public IT infrastructure.' WHERE (`conf_code`='introduce');


UPDATE `sys_message_template` SET `msg_title`='BSN Spartan Data Center Notification: ${captcha_} is your verification code', `msg_content`='Thank you for choosing BSN Spartan Network!<br/>\r\n<b>${captcha_}</b> is your verification code! This code is only valid for ${minutes_} minutes, if you request a new code, this code will no longer be valid.\r\n' WHERE (`msg_code`='user_join_captcha_');
UPDATE `sys_message_template` SET `msg_title`='BSN Spartan Data Center Notification: ${captcha_} is your verification code', `msg_content`='Thank you for choosing BSN Spartan Network!<br/>\r\n<b>${captcha_}</b> is your verification code! This code is only valid for ${minutes_} minutes, if you request a new code, this code will no longer be valid.\r\n' WHERE (`msg_code`='gas_recharge_captcha_');
UPDATE `sys_message_template` SET `msg_content`='We are pleased to inform you that we have received your order payment and we will recharge your Gas Credit as soon as possible, we will send the Gas Credit recharge result to your email.<br/>Your order information is as follows:<br/>Order Number: ${trade_no_}<br/>Wallet: ${account_address_}<br/>Gas Credit Amount: ${gas_count_}<br/>Order Total: US$ ${pay_amount_}<br/>Payment Date: ${pay_time_}<br/> ${tx_hash_}\r\n' WHERE (`msg_code`='payment_success_');
UPDATE `sys_message_template` SET `msg_code`='keystorePasswordReset', `msg_title`='Data Center System Notification: Re-configure Keystore Password', `msg_content`='After restarting the service of Data Center Management System, please configure the keystore password again. Otherwise, the system cannot submit transactions to the chain.<br/><br/>\r\n<b>Operation step</b>：Click \"Configuration\" button and enter the keystore password in Keystore Password section.' WHERE (`msg_code`='keystorePassword');

INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`, `update_time`, `create_time`,
                                    `state`)
VALUES ('keyStorePasswordConfig', 'Data Center System Notification: Configure Keystore Password',
        'The system did not detect your keystore password and cannot submit transactions to the chain. Please log in the Data Center Management System and configure the keystore password in Keystore Password section.',
        '2', now(), now(), '1');

INSERT INTO `sys_message_template` (`msg_code`, `msg_title`, `msg_content`, `msg_type`,
                                    `update_time`, `create_time`, `state`)
VALUES ('gas_recharge_success_', 'BSN Spartan Data Center Notification: Emergency Gas Credit Top-up Arrived',
        'We are pleased to inform you that your recent Gas Credit top-up has arrived,and you can check the balance here:${address_link_}<br/>Your transaction information is as follows:<br/>Order Number:${trade_no_}<br/>Wallet:${account_address_}<br/>Amount:${gas_count_}<br/>Order Total:US$ ${pay_amount_}<br/>Top-Up Time:${tx_time_}<br/>Transaction Hash:${tx_hash_}<br/><br/>Please check your Gas Credit balance,and you can get more information via your Data Center Management System.',
        2, now(), now(), 1);


