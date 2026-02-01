-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- 主机： 127.0.0.1
-- 生成日期： 2026-02-01 07:13:14
-- 服务器版本： 10.4.27-MariaDB
-- PHP 版本： 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `akcustomer`
--

-- --------------------------------------------------------

--
-- 表的结构 `sys_contact`
--

CREATE TABLE `sys_contact` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sex` int(2) DEFAULT NULL,
  `tel` varchar(50) DEFAULT NULL COMMENT '手机或固话',
  `phone` varchar(11) NOT NULL,
  `weixin` varchar(50) DEFAULT NULL,
  `qq` varchar(12) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `position` varchar(20) DEFAULT NULL COMMENT '职务',
  `tid` int(12) NOT NULL COMMENT '所属客户',
  `creatDate` datetime NOT NULL COMMENT '创建时间',
  `lastTime` datetime DEFAULT NULL COMMENT '最后一次跟进时间',
  `nextTime` datetime DEFAULT NULL COMMENT '下次跟进时间',
  `decisionMaker` int(2) NOT NULL COMMENT '是否决策人1是2否3未知',
  `address` varchar(50) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='tid';

--
-- 转存表中的数据 `sys_contact`
--

INSERT INTO `sys_contact` (`id`, `name`, `sex`, `tel`, `phone`, `weixin`, `qq`, `email`, `position`, `tid`, `creatDate`, `lastTime`, `nextTime`, `decisionMaker`, `address`, `birthday`, `remark`) VALUES
(12, '李总', 1, NULL, '13800138000', NULL, NULL, NULL, NULL, 8, '2025-08-16 13:08:14', '2025-08-16 14:12:02', NULL, 1, NULL, NULL, NULL),
(13, '张总', 1, NULL, '13800138000', NULL, NULL, NULL, NULL, 9, '2025-08-16 14:26:44', '2025-08-16 19:36:59', NULL, 1, NULL, NULL, NULL),
(14, '张三总', 1, NULL, '13800138000', NULL, NULL, NULL, NULL, 8, '2025-08-16 20:34:32', NULL, NULL, 1, NULL, NULL, NULL),
(15, '王哥', 1, NULL, '13800138000', NULL, NULL, NULL, NULL, 10, '2025-08-18 19:04:31', '2025-08-18 19:05:05', NULL, 1, NULL, NULL, NULL),
(16, '张姐', 1, NULL, '13800138000', NULL, NULL, NULL, '总经理', 12, '2025-08-21 10:31:22', '2025-08-21 10:33:54', '2025-09-04 00:00:00', 1, NULL, '1990-04-01 00:00:00', NULL),
(17, 'Ａ总', 0, '020-47856897', '13668949753', '微信号', '4344324324', 'fsfdsfsdf@qq.com', '职务', 14, '2025-11-21 20:07:51', NULL, '2025-11-21 20:07:00', 1, '详细地址', '2025-11-21 00:00:00', '微信号12121');

-- --------------------------------------------------------

--
-- 表的结构 `sys_contract`
--

CREATE TABLE `sys_contract` (
  `id` int(12) NOT NULL,
  `customerId` int(11) NOT NULL COMMENT '客户id',
  `name` varchar(50) NOT NULL COMMENT '合同名称',
  `code` varchar(50) NOT NULL COMMENT '合同编号',
  `creatDate` datetime NOT NULL COMMENT '签订日期',
  `userId` int(12) NOT NULL COMMENT '签约人',
  `contactId` int(11) NOT NULL COMMENT '公司签约人',
  `money` varchar(50) NOT NULL COMMENT '合同金额',
  `remark` varchar(200) DEFAULT NULL,
  `files` varchar(200) DEFAULT NULL COMMENT '合同附件',
  `startDate` datetime NOT NULL COMMENT '有效期开始',
  `endDate` datetime NOT NULL COMMENT '有效期结束',
  `status` int(4) NOT NULL COMMENT '1待确认2领导已确认3没通过'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='合同信息';

--
-- 转存表中的数据 `sys_contract`
--

INSERT INTO `sys_contract` (`id`, `customerId`, `name`, `code`, `creatDate`, `userId`, `contactId`, `money`, `remark`, `files`, `startDate`, `endDate`, `status`) VALUES
(5, 8, 'a合同名称', 'afew4546s4f', '2025-08-16 20:57:40', 1, 14, '1000', NULL, '/api/upload/1-77cd33aa-3ee4-4ac7-b64d-8850d73c49dd.png', '2025-08-17 00:00:00', '2025-08-22 00:00:00', 2),
(6, 9, 'u合同', 'ufdsfsdfdsf', '2025-08-16 20:58:35', 2, 13, '5000', NULL, '/api/upload/2-184a7d20-d482-46a7-9c1b-7a6c57caa086.png', '2025-08-16 00:00:00', '2025-08-21 00:00:00', 1),
(7, 12, '合同测试', 'test', '2025-12-05 14:32:16', 1, 16, '100', '备注', '/api/upload/1-6dbc4397-7851-4557-9add-40b14a61c3cc.png', '2025-12-05 00:00:00', '2025-12-05 00:00:00', 1);

-- --------------------------------------------------------

--
-- 表的结构 `sys_contract_payment`
--

CREATE TABLE `sys_contract_payment` (
  `id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL COMMENT '回款编号',
  `contractId` int(11) NOT NULL,
  `money` varchar(50) NOT NULL COMMENT '回款金额',
  `datetime` datetime NOT NULL COMMENT '回款时间',
  `remark` varchar(300) DEFAULT NULL,
  `creatDate` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `account` int(4) NOT NULL COMMENT '回款账户',
  `status` int(2) DEFAULT 0 COMMENT '1待确认2领导已确认3没通过	',
  `files` text DEFAULT NULL COMMENT '附件'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='合同回款';

--
-- 转存表中的数据 `sys_contract_payment`
--

INSERT INTO `sys_contract_payment` (`id`, `code`, `contractId`, `money`, `datetime`, `remark`, `creatDate`, `account`, `status`, `files`) VALUES
(6, 'a56565大是大非', 5, '1000', '2025-08-04 00:00:00', NULL, '2025-08-16 22:06:13', 1, 1, NULL),
(7, 'ufdsfsdfdf', 6, '1000', '2025-08-13 00:00:00', NULL, '2025-08-16 22:12:04', 1, 1, NULL);

-- --------------------------------------------------------

--
-- 表的结构 `sys_customer`
--

CREATE TABLE `sys_customer` (
  `id` int(12) NOT NULL,
  `company` varchar(50) NOT NULL COMMENT '公司名称',
  `brandName` varchar(50) DEFAULT NULL COMMENT '产品品牌名称',
  `type` int(4) DEFAULT NULL COMMENT '合作类型',
  `area` varchar(50) DEFAULT NULL COMMENT '所属区域',
  `address` varchar(100) DEFAULT NULL COMMENT '公司地址',
  `industry` int(10) DEFAULT NULL COMMENT '行业分类',
  `code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `files` varchar(200) DEFAULT NULL COMMENT '相关附件',
  `source` int(11) DEFAULT NULL COMMENT '来源',
  `tel` varchar(50) DEFAULT NULL COMMENT '电话',
  `web` varchar(50) DEFAULT NULL COMMENT '网址',
  `userId` int(12) DEFAULT NULL COMMENT '所属人员',
  `creatTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL,
  `status` int(2) NOT NULL COMMENT '1正常2公海3无效4删除',
  `shareUserId` varchar(50) DEFAULT NULL COMMENT '分享给指定用户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 转存表中的数据 `sys_customer`
--

INSERT INTO `sys_customer` (`id`, `company`, `brandName`, `type`, `area`, `address`, `industry`, `code`, `files`, `source`, `tel`, `web`, `userId`, `creatTime`, `updateTime`, `remark`, `status`, `shareUserId`) VALUES
(8, '广州未来发展科技有限公司', NULL, 1, '11,110101', '珠江新城', 1, NULL, NULL, 1, '020-3659874', 'http://www.baidu.com', 1, '2025-08-14 14:19:47', '2025-11-12 12:06:18', NULL, 1, ''),
(9, 'user有限公司', NULL, 1, '44,4401,440103', NULL, 1, NULL, NULL, 1, NULL, NULL, 2, '2025-08-14 20:56:46', NULL, NULL, 1, ''),
(10, '广州蛋扯虾集团有限公司', NULL, 3, '44,4401,440111', NULL, 2, NULL, NULL, 2, NULL, NULL, 1, '2025-08-18 19:03:19', '2025-08-21 09:17:27', NULL, 1, ''),
(11, '广州小鱼科技创新有限公司', NULL, 2, '44,4401,440105', NULL, 1, NULL, NULL, 4, NULL, NULL, 1, '2025-08-21 09:18:49', NULL, NULL, 1, '5,6,4'),
(12, '广州摸鱼股份有限公司', NULL, 1, '44,4401,440103', NULL, 2, NULL, NULL, 5, NULL, NULL, 1, '2025-08-21 09:21:16', '2025-08-21 10:37:22', NULL, 1, ''),
(13, '深圳小虾科技有限公司', NULL, 4, '44,4403,440305', NULL, 1, NULL, NULL, 6, NULL, NULL, 2, '2025-08-21 11:29:00', NULL, NULL, 1, NULL),
(14, '新增测试', '品牌', 1, '11,110101', '详细地址', 1, '14141414', '/api/upload/1-fe776782-3db3-459b-9a42-d0fc856931f6.png,/api/upload/1-bbb93210-bc24-49df-b7a3-cbb6ef34cfa2.png', 1, '020-47856897', 'http://www.baidu.com', NULL, '2025-11-18 15:35:46', '2025-11-18 15:46:20', '备注', 3, '');

-- --------------------------------------------------------

--
-- 表的结构 `sys_customer_operate_records`
--

CREATE TABLE `sys_customer_operate_records` (
  `id` int(12) NOT NULL,
  `tid` int(12) NOT NULL COMMENT '客户id',
  `userId` int(12) NOT NULL COMMENT '操作人id',
  `dataTime` datetime NOT NULL,
  `content` varchar(50) NOT NULL,
  `userName` varchar(50) DEFAULT NULL COMMENT '操作人名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='客户操作记录';

--
-- 转存表中的数据 `sys_customer_operate_records`
--

INSERT INTO `sys_customer_operate_records` (`id`, `tid`, `userId`, `dataTime`, `content`, `userName`) VALUES
(34, 8, 1, '2025-08-14 14:19:47', '创建了该数据记录', 'admin'),
(35, 9, 2, '2025-08-14 20:56:46', '创建了该数据记录', 'user'),
(36, 10, 1, '2025-08-18 19:03:19', '创建了该数据记录', 'admin'),
(37, 9, 2, '2025-08-18 22:16:36', '将客户转入公海', 'user'),
(38, 11, 1, '2025-08-21 09:18:49', '创建了该数据记录', 'admin'),
(39, 12, 1, '2025-08-21 09:21:16', '创建了该数据记录', 'admin'),
(40, 10, 1, '2025-08-21 09:39:44', '将客户转入公海', 'admin'),
(41, 13, 2, '2025-08-21 11:29:00', '创建了该数据记录', 'user'),
(42, 12, 1, '2025-11-15 13:38:19', '将客户移交给admin(1)', 'admin'),
(43, 12, 1, '2025-11-15 13:40:30', '将客户移交给admin(1)', 'admin'),
(44, 12, 1, '2025-11-15 13:41:05', '将客户移交给admin(1)', 'admin'),
(45, 12, 1, '2025-11-15 14:00:49', '将客户移交给admin(1)', 'admin'),
(46, 12, 1, '2025-11-15 14:00:56', '将客户转入公海', 'admin'),
(47, 12, 1, '2025-11-15 14:02:49', '将客户转入公海', 'admin'),
(48, 8, 1, '2025-11-15 16:49:19', '将客户移交给admin(1)', 'admin'),
(49, 8, 1, '2025-11-15 16:51:08', '将客户转入公海', 'admin'),
(50, 8, 1, '2025-11-15 16:51:32', '从公海或无效客户转入跟进', 'admin'),
(51, 8, 1, '2025-11-15 19:28:11', '将客户设为无效客户', 'admin'),
(52, 8, 1, '2025-11-15 19:28:24', '从公海或无效客户转入跟进', 'admin'),
(53, 14, 1, '2025-11-18 15:35:46', '创建了该数据记录', 'admin'),
(54, 10, 1, '2025-11-28 09:27:33', '从公海或无效客户转入跟进', 'admin'),
(55, 9, 1, '2025-11-28 09:31:03', '从公海或无效客户转入跟进', 'admin'),
(56, 9, 1, '2025-11-28 09:31:37', '将客户转入公海', 'admin'),
(57, 14, 1, '2025-11-28 09:35:31', '将客户设为无效客户', 'admin');

-- --------------------------------------------------------

--
-- 表的结构 `sys_department`
--

CREATE TABLE `sys_department` (
  `id` int(10) NOT NULL,
  `tid` int(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` int(2) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `userId` int(12) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 转存表中的数据 `sys_department`
--

INSERT INTO `sys_department` (`id`, `tid`, `name`, `status`, `remark`, `userId`) VALUES
(1, 0, '广州业务部', 1, NULL, 5),
(2, 1, '业务部', 1, NULL, 1),
(3, 0, '北京业务部', 1, NULL, NULL),
(4, 1, '业务二部', 1, NULL, NULL);

-- --------------------------------------------------------

--
-- 表的结构 `sys_dict`
--

CREATE TABLE `sys_dict` (
  `id` int(12) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL COMMENT '唯一标识',
  `status` int(2) NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `isSys` int(2) DEFAULT 0 COMMENT '1为系统类型不能删',
  `dateTime` datetime DEFAULT current_timestamp() COMMENT '修改时间',
  `children` varchar(500) DEFAULT NULL COMMENT '字典列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='字典';

--
-- 转存表中的数据 `sys_dict`
--

INSERT INTO `sys_dict` (`id`, `name`, `type`, `status`, `remark`, `isSys`, `dateTime`, `children`) VALUES
(1, '状态', 'status', 1, NULL, 1, '2025-07-08 20:18:55', '[{\"label\":\"启用\",\"value\":\"1\",\"field\":\"1\"},{\"label\":\"禁用\",\"value\":\"0\",\"field\":\"0\"}]'),
(3, '行业分类', 'industryType', 1, NULL, 1, '2025-08-21 09:12:48', '[{\"label\":\"分类1\",\"value\":\"1\"},{\"label\":\"分类2\",\"value\":\"2\"}]'),
(4, '合作类型', 'cooperationType', 1, NULL, 1, '2025-08-21 09:12:20', '[{\"label\":\"潜在客户\",\"value\":\"1\",\"field\":\"1\"},{\"label\":\"意向客户\",\"value\":\"2\",\"field\":\"0\"},{\"label\":\"成交客户\",\"value\":\"3\"},{\"label\":\"忠诚客户\",\"value\":\"4\"},{\"label\":\"流失客户\",\"value\":\"5\"}]'),
(5, '来源', 'source', 1, NULL, 1, '2025-07-10 18:18:37', '[{\"label\":\"线上渠道\",\"value\":\"1\"},{\"label\":\"线下渠道\",\"value\":\"2\"},{\"label\":\"内部推荐与转介绍\",\"value\":\"3\"},{\"label\":\"行业合作与活动\",\"value\":\"4\"},{\"label\":\"口碑与品牌传播\",\"value\":\"5\"},{\"label\":\"其他特殊渠道\",\"value\":\"6\"}]'),
(6, '性别', 'sex', 1, NULL, 1, '2025-07-22 18:12:24', '[{\"label\":\"男\",\"value\":\"1\"},{\"label\":\"女\",\"value\":\"0\"},{\"label\":\"未知\",\"value\":\"2\"}]'),
(7, '跟进方式', 'followType', 1, NULL, 1, '2025-10-28 10:50:41', '[{\"label\":\"电话\",\"value\":\"1\"},{\"label\":\"微信\",\"value\":\"2\"},{\"label\":\"上门拜访\",\"value\":\"3\"},{\"label\":\"其他\",\"value\":\"4\"}]'),
(8, '回款账户类型', 'accountType', 1, NULL, 1, '2025-10-27 16:38:51', '[{\"label\":\"现金\",\"value\":\"1\"},{\"label\":\"微信\",\"value\":\"2\"},{\"label\":\"支付宝\",\"value\":\"3\"},{\"label\":\"对公账户\",\"value\":\"4\"},{\"label\":\"其他\",\"value\":\"5\"}]');

-- --------------------------------------------------------

--
-- 表的结构 `sys_follow_records`
--

CREATE TABLE `sys_follow_records` (
  `id` int(12) NOT NULL,
  `customerId` int(12) DEFAULT NULL COMMENT '客户id',
  `contactId` int(12) NOT NULL COMMENT '联系人',
  `contactName` varchar(20) DEFAULT NULL,
  `userId` int(12) NOT NULL COMMENT '跟进人用户id',
  `userName` varchar(50) NOT NULL COMMENT '跟进人用户名称',
  `dateTime` datetime NOT NULL COMMENT '跟进时间',
  `type` int(4) NOT NULL COMMENT '跟进类型',
  `remark` varchar(300) NOT NULL COMMENT '跟进记录客户反馈'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='跟进记录';

--
-- 转存表中的数据 `sys_follow_records`
--

INSERT INTO `sys_follow_records` (`id`, `customerId`, `contactId`, `contactName`, `userId`, `userName`, `dateTime`, `type`, `remark`) VALUES
(13, 8, 12, '李总', 1, 'admin', '2025-08-16 14:12:02', 2, '超大规模'),
(14, 9, 13, '张总', 2, 'user', '2025-08-16 14:28:35', 2, '闲聊'),
(15, 9, 13, '张总', 1, 'admin', '2025-08-16 19:36:59', 2, '偶遇，闲聊'),
(16, 10, 15, '王哥', 1, 'admin', '2025-08-18 19:05:05', 1, '闲聊'),
(17, 12, 16, '张姐', 1, 'admin', '2025-08-21 10:33:54', 4, '京城大型交流会上偶遇张驵，相约下次上门拜访');

-- --------------------------------------------------------

--
-- 表的结构 `sys_login_log`
--

CREATE TABLE `sys_login_log` (
  `id` int(10) NOT NULL,
  `userName` varchar(50) NOT NULL,
  `userId` int(10) NOT NULL,
  `loginIp` varchar(20) NOT NULL COMMENT '登录ip',
  `dateTime` datetime NOT NULL COMMENT '登录时间',
  `remark` varchar(50) DEFAULT NULL,
  `status` int(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 转存表中的数据 `sys_login_log`
--

INSERT INTO `sys_login_log` (`id`, `userName`, `userId`, `loginIp`, `dateTime`, `remark`, `status`) VALUES
(145, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-08-21 11:24:47', NULL, 1),
(146, 'user', 2, '0:0:0:0:0:0:0:1', '2025-08-21 11:25:19', NULL, 1),
(147, 'user', 2, '0:0:0:0:0:0:0:1', '2025-08-21 11:28:12', NULL, 1),
(148, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-08-22 16:24:32', NULL, 1),
(149, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-12 11:26:55', NULL, 1),
(150, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-12 12:26:28', NULL, 1),
(151, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-12 12:40:27', NULL, 1),
(152, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-18 14:46:01', NULL, 1),
(153, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 12:01:23', NULL, 1),
(154, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 12:04:31', NULL, 1),
(155, 'admin', 0, '0:0:0:0:0:0:0:1', '2025-09-19 12:45:47', '密码:1234', 0),
(156, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 17:37:58', '密码:123456', 0),
(157, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 17:42:02', NULL, 1),
(158, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 17:42:22', '密码:12345', 0),
(159, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 20:17:46', '密码:12345', 0),
(160, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 20:17:58', '密码:12345', 0),
(161, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-19 20:18:45', NULL, 1),
(162, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-20 20:13:40', NULL, 1),
(163, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-21 11:32:03', NULL, 1),
(164, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-21 11:33:07', NULL, 1),
(165, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-22 15:25:21', NULL, 1),
(166, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-22 16:29:04', NULL, 1),
(167, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-22 19:18:58', NULL, 1),
(168, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-23 08:24:06', NULL, 1),
(169, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-23 09:58:35', NULL, 1),
(170, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-23 10:04:59', NULL, 1),
(171, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-23 10:06:00', NULL, 1),
(172, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-23 10:53:37', NULL, 1),
(173, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-24 09:01:52', NULL, 1),
(174, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-24 11:42:16', NULL, 1),
(175, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-30 11:06:26', NULL, 1),
(176, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-09-30 11:06:50', NULL, 1),
(177, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-24 14:24:12', NULL, 1),
(178, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-26 09:34:54', NULL, 1),
(179, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-27 09:42:39', NULL, 1),
(180, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-28 11:32:41', NULL, 1),
(181, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-28 17:05:03', NULL, 1),
(182, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-29 12:24:44', NULL, 1),
(183, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-29 19:50:31', NULL, 1),
(184, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-30 15:12:27', NULL, 1),
(185, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-30 20:02:36', NULL, 1),
(186, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-10-31 10:46:23', NULL, 1),
(187, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-01 21:23:56', NULL, 1),
(188, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-03 15:28:35', NULL, 1),
(189, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-04 08:52:32', NULL, 1),
(190, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-04 08:52:55', NULL, 1),
(191, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-08 09:52:05', NULL, 1),
(192, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-10 15:31:04', NULL, 1),
(193, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-11 08:40:41', NULL, 1),
(194, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-11 11:24:42', NULL, 1),
(195, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-11 11:24:43', NULL, 1),
(196, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-13 07:41:54', NULL, 1),
(197, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-15 13:23:32', NULL, 1),
(198, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-15 16:33:40', NULL, 1),
(199, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-16 09:32:48', NULL, 1),
(200, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-16 09:34:04', NULL, 1),
(201, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-17 08:47:16', NULL, 1),
(202, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-17 09:08:36', NULL, 1),
(203, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-18 19:31:34', NULL, 1),
(204, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-18 19:31:35', NULL, 1),
(205, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-21 20:29:01', NULL, 1),
(206, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-22 14:22:37', NULL, 1),
(207, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-25 08:00:38', NULL, 1),
(208, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-25 08:27:54', NULL, 1),
(209, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-26 08:32:52', NULL, 1),
(210, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-27 11:04:15', NULL, 1),
(211, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-28 09:16:44', NULL, 1),
(212, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-29 19:03:16', NULL, 1),
(213, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-11-29 19:04:08', NULL, 1),
(214, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-01 17:41:06', NULL, 1),
(215, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-01 18:54:18', NULL, 1),
(216, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-02 09:23:10', NULL, 1),
(217, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-03 19:11:20', NULL, 1),
(218, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-04 17:02:22', NULL, 1),
(219, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-05 13:41:29', NULL, 1),
(220, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-05 19:09:17', NULL, 1),
(221, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-06 12:44:12', NULL, 1),
(222, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-06 12:45:14', NULL, 1),
(223, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-08 16:18:41', NULL, 1),
(224, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-08 16:36:00', NULL, 1),
(225, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-09 09:05:24', NULL, 1),
(226, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-10 12:49:31', NULL, 1),
(227, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-10 18:56:20', NULL, 1),
(228, 'admin', 1, '0:0:0:0:0:0:0:1', '2025-12-11 08:25:09', NULL, 1),
(229, 'admin', 1, '0:0:0:0:0:0:0:1', '2026-02-01 12:06:40', NULL, 1),
(230, 'admin', 1, '0:0:0:0:0:0:0:1', '2026-02-01 12:07:30', NULL, 1);

-- --------------------------------------------------------

--
-- 表的结构 `sys_role`
--

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` int(2) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `content` text DEFAULT NULL COMMENT '权限列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 转存表中的数据 `sys_role`
--

INSERT INTO `sys_role` (`id`, `name`, `status`, `remark`, `content`) VALUES
(1, '管理员', 1, NULL, ',/analysis/type,/analysis/source,/analysis/industry,/analysis/area,/analysis/customer,/analysis/follow,/analysis/contract,,/customer/check,/customer/list,/customer/contact,/customer/follow,/customer/list-comm,/customer/list-invalid,/customer/ocr,delCustomer,,/contract/contract,/contract/payment,,/system/user,/system/role,/system/dept,/system/log,/system/dict,/system/cache'),
(2, '普通用户', 1, NULL, '/analysis/type,/analysis/source,/analysis/industry,/analysis/area,/customer/check,/customer/list,/customer/contact,/customer/follow,/customer/list-comm,/customer/list-invalid,/customer/ocr,,/contract/contract,/contract/payment');

-- --------------------------------------------------------

--
-- 表的结构 `sys_user`
--

CREATE TABLE `sys_user` (
  `id` int(10) NOT NULL,
  `userName` varchar(50) NOT NULL,
  `password` varchar(65) NOT NULL,
  `status` int(2) NOT NULL COMMENT '0禁用1正常',
  `creatTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '信息修改时间',
  `lastLogin` datetime DEFAULT NULL COMMENT '最后登录时间',
  `loginTimer` int(11) DEFAULT 0 COMMENT '登录次数',
  `ip` varchar(20) DEFAULT NULL COMMENT '最后登录ip地址',
  `remark` varchar(200) DEFAULT NULL,
  `departmentId` int(11) DEFAULT NULL,
  `tid` int(10) DEFAULT NULL COMMENT '直属上级id',
  `phone` varchar(12) DEFAULT NULL,
  `weixin` varchar(50) DEFAULT NULL,
  `qq` int(13) DEFAULT NULL,
  `sex` int(2) DEFAULT NULL,
  `roleId` varchar(50) DEFAULT NULL COMMENT '角色',
  `bindWX` varchar(50) DEFAULT NULL COMMENT '绑定微信用于登录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- 转存表中的数据 `sys_user`
--

INSERT INTO `sys_user` (`id`, `userName`, `password`, `status`, `creatTime`, `updateTime`, `lastLogin`, `loginTimer`, `ip`, `remark`, `departmentId`, `tid`, `phone`, `weixin`, `qq`, `sex`, `roleId`, `bindWX`) VALUES
(1, 'admin', '$2a$10$pyzEpYb3HcgkKC4t3yZM0.YRb7hiI3QUYxcFffN4cRAQHqcmqeWLm', 1, '2025-07-09 06:24:03', '2025-07-30 11:07:47', '2026-02-01 12:07:31', 226, '0:0:0:0:0:0:0:1', NULL, 1, NULL, '13800138000', NULL, NULL, 1, '1,2', NULL),
(2, 'user', '$2a$10$ksP/JbPV0Gf0xAGztY7uG.mNj/11wtHFj6ojq.77S1tKo4P4hQxV6', 1, '2025-07-07 06:24:03', '2025-07-30 11:08:27', '2025-08-21 11:28:12', 17, '0:0:0:0:0:0:0:1', NULL, 4, 1, '13800138000', NULL, NULL, 1, '2', NULL),
(4, 'user2', '$2a$10$ksP/JbPV0Gf0xAGztY7uG.mNj/11wtHFj6ojq.77S1tKo4P4hQxV6', 1, '2025-08-10 07:41:05', NULL, NULL, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL),
(5, '测试', '$2a$10$Jo6sadUgrrol58NMHnS87OJIN/WqIxXP4xUFtjmBPBAse/E9IoXTC', 1, '2025-11-07 16:50:38', NULL, NULL, 0, NULL, '备注', 2, 4, '13668949543', 'wx', 55555, 0, '2', NULL),
(6, '新增测试', '$2a$10$bgk28O/ho8YSFf2cQwWP.uykSj9WoJRNgKs/K9lLuHneHaFrZSZZ.', 1, '2025-11-09 20:38:00', '2025-11-10 13:05:00', NULL, 0, NULL, 'remark', 2, 4, '13580487806', '3333', 14141, 0, '2,1', NULL);

--
-- 转储表的索引
--

--
-- 表的索引 `sys_contact`
--
ALTER TABLE `sys_contact`
  ADD PRIMARY KEY (`id`),
  ADD KEY `tid` (`tid`);

--
-- 表的索引 `sys_contract`
--
ALTER TABLE `sys_contract`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_contract_payment`
--
ALTER TABLE `sys_contract_payment`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_customer`
--
ALTER TABLE `sys_customer`
  ADD PRIMARY KEY (`id`),
  ADD KEY `company` (`company`),
  ADD KEY `id_userId_status` (`id`,`userId`,`status`) USING BTREE;

--
-- 表的索引 `sys_customer_operate_records`
--
ALTER TABLE `sys_customer_operate_records`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_department`
--
ALTER TABLE `sys_department`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_dict`
--
ALTER TABLE `sys_dict`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_follow_records`
--
ALTER TABLE `sys_follow_records`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customerId` (`customerId`);

--
-- 表的索引 `sys_login_log`
--
ALTER TABLE `sys_login_log`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_role`
--
ALTER TABLE `sys_role`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `sys_user`
--
ALTER TABLE `sys_user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_tid_status` (`id`,`tid`,`status`) USING BTREE COMMENT '优化递归查询';

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `sys_contact`
--
ALTER TABLE `sys_contact`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- 使用表AUTO_INCREMENT `sys_contract`
--
ALTER TABLE `sys_contract`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用表AUTO_INCREMENT `sys_contract_payment`
--
ALTER TABLE `sys_contract_payment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用表AUTO_INCREMENT `sys_customer`
--
ALTER TABLE `sys_customer`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- 使用表AUTO_INCREMENT `sys_customer_operate_records`
--
ALTER TABLE `sys_customer_operate_records`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- 使用表AUTO_INCREMENT `sys_department`
--
ALTER TABLE `sys_department`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- 使用表AUTO_INCREMENT `sys_dict`
--
ALTER TABLE `sys_dict`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- 使用表AUTO_INCREMENT `sys_follow_records`
--
ALTER TABLE `sys_follow_records`
  MODIFY `id` int(12) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- 使用表AUTO_INCREMENT `sys_login_log`
--
ALTER TABLE `sys_login_log`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=231;

--
-- 使用表AUTO_INCREMENT `sys_role`
--
ALTER TABLE `sys_role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- 使用表AUTO_INCREMENT `sys_user`
--
ALTER TABLE `sys_user`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
