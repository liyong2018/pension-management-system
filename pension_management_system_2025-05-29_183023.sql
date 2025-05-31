-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pension_management_system
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `device_alarm_record`
--

DROP TABLE IF EXISTS `device_alarm_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_alarm_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '告警ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `alarm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '告警类型',
  `alarm_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '告警级别',
  `alarm_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '告警内容描述',
  `alarm_time` datetime NOT NULL COMMENT '告警时间',
  `alarm_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '触发告警的数据值',
  `process_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未处理' COMMENT '处理状态',
  `process_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理人员',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `process_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '处理结果描述',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE,
  KEY `idx_alarm_type` (`alarm_type`) USING BTREE,
  KEY `idx_alarm_level` (`alarm_level`) USING BTREE,
  KEY `idx_alarm_time` (`alarm_time`) USING BTREE,
  KEY `idx_process_status` (`process_status`) USING BTREE,
  KEY `idx_created_time` (`created_time`) USING BTREE,
  CONSTRAINT `device_alarm_record_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `smart_device` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='智能设备告警记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_alarm_record`
--

/*!40000 ALTER TABLE `device_alarm_record` DISABLE KEYS */;
INSERT INTO `device_alarm_record` VALUES (1,1,'健康异常','警告','心率异常：检测到心率过高','2024-01-15 10:30:00','{\"heart_rate\": 135, \"threshold\": 120}','已处理','护士小王','2024-01-15 10:35:00','已联系家属，老人情况稳定','老人刚运动完，心率正常升高','2024-01-15 10:30:00','2024-01-15 10:35:00'),(2,2,'健康异常','严重','血压异常：收缩压过高','2024-01-15 11:15:00','{\"systolic\": 165, \"diastolic\": 95, \"threshold_systolic\": 140}','已处理','医生李明','2024-01-15 11:20:00','建议调整药物剂量，已联系主治医生','需要密切观察血压变化','2024-01-15 11:15:00','2024-01-15 11:20:00'),(5,19,'SOS报警','严重','老人按下SOS紧急按钮，需要立即救援','2024-05-27 14:30:22','{\"buttonPressed\": true, \"location\": \"床头\", \"battery\": 78}','未处理',NULL,NULL,NULL,'老人可能遇到紧急情况','2025-05-27 11:15:25','2025-05-27 11:15:25'),(6,21,'SOS报警','严重','GPS定位器检测到老人长时间未移动','2024-05-27 13:45:15','{\"noMovement\": \"2小时\", \"lastLocation\": \"39.9042,116.4074\", \"battery\": 65}','处理中','张护士','2024-05-27 13:50:00','已联系家属，正在前往现场','可能是老人跌倒或身体不适','2025-05-27 11:15:25','2025-05-27 11:15:25'),(7,19,'SOS报警','严重','紧急按钮连续按压3次','2024-05-27 12:20:08','{\"pressCount\": 3, \"interval\": \"5秒\", \"battery\": 78}','已处理','李医生','2024-05-27 12:25:00','已确认老人安全，误触发','老人操作不当导致误报','2025-05-27 11:15:25','2025-05-27 11:15:25'),(8,21,'SOS报警','严重','老人离开安全区域范围','2024-05-27 11:15:30','{\"geoFence\": \"超出500米\", \"currentLocation\": \"39.9100,116.4200\", \"battery\": 65}','已处理','王社工','2024-05-27 11:20:00','老人去医院检查，已提前报备','正常外出，已确认安全','2025-05-27 11:15:25','2025-05-27 11:15:25'),(9,19,'SOS报警','警告','SOS设备电量过低','2024-05-27 10:30:45','{\"battery\": 15, \"lowBatteryWarning\": true}','未处理',NULL,NULL,NULL,'需要及时更换电池','2025-05-27 11:15:26','2025-05-27 11:15:26'),(10,17,'烟感报警','严重','检测到烟雾浓度超标','2024-05-27 14:45:12','{\"smokeLevel\": 4.2, \"threshold\": 3.5, \"temperature\": 35.8}','处理中','消防值班员','2024-05-27 14:47:00','已通知消防部门，正在现场检查','可能是厨房做饭产生的烟雾','2025-05-27 11:15:26','2025-05-27 11:15:26'),(11,17,'烟感报警','警告','烟雾浓度接近告警阈值','2024-05-27 13:20:30','{\"smokeLevel\": 3.2, \"threshold\": 3.5, \"temperature\": 28.5}','已处理','安全员','2024-05-27 13:25:00','现场检查无异常，可能是灰尘影响','建议清洁烟感器','2025-05-27 11:15:26','2025-05-27 11:15:26'),(12,17,'设备故障','警告','烟感器通信异常','2024-05-27 09:15:20','{\"communicationError\": true, \"lastResponse\": \"2024-05-27 08:30:00\"}','已处理','技术员','2024-05-27 09:30:00','重启设备后恢复正常','网络波动导致通信中断','2025-05-27 11:15:26','2025-05-27 11:15:26'),(13,17,'低电量','提醒','烟感器电量不足','2024-05-27 08:00:00','{\"battery\": 18, \"threshold\": 20}','未处理',NULL,NULL,NULL,'需要更换电池','2025-05-27 11:15:27','2025-05-27 11:15:27'),(14,13,'设备故障','警告','智能门锁电量过低','2024-05-27 16:00:00','{\"battery\": 18, \"threshold\": 20, \"lockStatus\": \"locked\"}','未处理',NULL,NULL,NULL,'门锁电量不足，可能影响正常使用','2025-05-27 11:15:27','2025-05-27 11:15:27'),(15,16,'数据异常','警告','空气质量差','2024-05-27 14:00:00','{\"pm25\": 85, \"threshold\": 75, \"aqi\": 120}','已处理','环境管理员','2024-05-27 14:10:00','已开启净化器最大档位','PM2.5超标，需要净化空气','2025-05-27 11:15:27','2025-05-27 11:15:27'),(16,15,'设备故障','提醒','智能插座功耗异常','2024-05-27 13:00:00','{\"power\": 2800, \"threshold\": 2500, \"current\": 12.5}','已处理','电工','2024-05-27 13:15:00','检查设备负载，已恢复正常','用电设备功率过大','2025-05-27 11:15:27','2025-05-27 11:15:27'),(17,14,'设备故障','提醒','窗帘控制器卡顿','2024-05-27 11:00:00','{\"position\": 60, \"targetPosition\": 100, \"stuck\": true}','未处理',NULL,NULL,NULL,'窗帘无法完全打开','2025-05-27 11:15:28','2025-05-27 11:15:28'),(18,18,'水浸报警','严重','检测到水浸','2024-05-27 16:30:00','{\"waterLevel\": 3, \"threshold\": 2, \"location\": \"厨房水槽下方\"}','处理中','维修工','2024-05-27 16:32:00','正在检查水管是否漏水','可能是水管破裂或漏水','2025-05-27 11:15:28','2025-05-27 11:15:28'),(19,20,'安全报警','警告','门窗异常开启','2024-05-27 15:45:00','{\"status\": \"open\", \"time\": \"15:45:00\", \"duration\": \"30分钟\"}','已处理','安保人员','2024-05-27 16:00:00','确认是老人开窗通风','正常开窗，无安全问题','2025-05-27 11:15:28','2025-05-27 11:15:28'),(20,20,'低电量','提醒','门窗传感器电量不足','2024-05-27 14:30:00','{\"battery\": 12, \"threshold\": 15}','未处理',NULL,NULL,NULL,'电池电量过低，需要更换','2025-05-27 11:15:28','2025-05-27 11:15:28'),(21,18,'设备故障','提醒','水浸传感器通信异常','2024-05-27 13:15:00','{\"communicationError\": true, \"lastResponse\": \"2024-05-27 12:30:00\"}','已处理','技术员','2024-05-27 13:30:00','更换传感器电池后恢复','电池电量不足导致通信异常','2025-05-27 11:15:28','2025-05-27 11:15:28'),(22,22,'定位异常','警告','室内定位精度下降','2024-05-27 17:00:00','{\"accuracy\": 1.5, \"threshold\": 0.3, \"zone\": \"living_room\"}','未处理',NULL,NULL,NULL,'定位精度不够，可能影响安全监护','2025-05-27 11:15:29','2025-05-27 11:15:29'),(23,21,'网络断连','警告','GPS定位器信号弱','2024-05-27 16:15:00','{\"signal\": -85, \"threshold\": -80, \"gpsStatus\": \"weak\"}','已处理','技术支持','2024-05-27 16:20:00','移动到窗边后信号恢复','室内GPS信号较弱','2025-05-27 11:15:29','2025-05-27 11:15:29'),(24,22,'设备故障','提醒','定位设备重启','2024-05-27 15:00:00','{\"reboot\": true, \"reason\": \"系统异常\", \"uptime\": \"重新启动\"}','已处理','系统管理员','2024-05-27 15:05:00','设备自动重启，运行正常','系统自动维护重启','2025-05-27 11:15:29','2025-05-27 11:15:29'),(25,1,'设备异常','警告','设备电量低','2025-05-24 07:31:24','{\"battery_level\": 15, \"threshold\": 20}','未处理',NULL,NULL,NULL,'需要及时充电','2025-05-24 07:31:24','2025-05-24 07:31:24'),(26,2,'健康异常','紧急','血压异常：收缩压过高','2025-05-22 07:31:24','{\"systolic\": 180, \"diastolic\": 110, \"threshold_systolic\": 140}','处理中','医生张华','2025-05-22 07:31:24','已紧急联系家属，建议立即就医','高血压危象，需要紧急处理','2025-05-22 07:31:24','2025-05-22 07:31:24');
/*!40000 ALTER TABLE `device_alarm_record` ENABLE KEYS */;

--
-- Table structure for table `device_data_record`
--

DROP TABLE IF EXISTS `device_data_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_data_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据记录ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `elderly_id` bigint DEFAULT NULL COMMENT '老人ID',
  `data_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据类型',
  `data_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据内容',
  `data_time` datetime NOT NULL COMMENT '数据采集时间',
  `data_value` decimal(10,2) DEFAULT NULL COMMENT '数据数值',
  `data_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据单位',
  `is_abnormal` tinyint DEFAULT '0' COMMENT '是否异常',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE,
  KEY `idx_elderly_id` (`elderly_id`) USING BTREE,
  KEY `idx_data_type` (`data_type`) USING BTREE,
  KEY `idx_data_time` (`data_time`) USING BTREE,
  KEY `idx_created_time` (`created_time`) USING BTREE,
  KEY `idx_is_abnormal` (`is_abnormal`) USING BTREE,
  CONSTRAINT `device_data_record_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `smart_device` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `device_data_record_ibfk_2` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='设备数据采集记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_data_record`
--

/*!40000 ALTER TABLE `device_data_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_data_record` ENABLE KEYS */;

--
-- Table structure for table `device_maintenance_record`
--

DROP TABLE IF EXISTS `device_maintenance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_maintenance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '维护记录ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `maintenance_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '维护类型',
  `maintenance_date` date NOT NULL COMMENT '维护日期',
  `maintenance_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '维护人员',
  `maintenance_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '维护内容',
  `maintenance_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '维护结果',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '维护费用',
  `next_maintenance_date` date DEFAULT NULL COMMENT '下次维护日期',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE,
  KEY `idx_maintenance_type` (`maintenance_type`) USING BTREE,
  KEY `idx_maintenance_date` (`maintenance_date`) USING BTREE,
  KEY `idx_created_time` (`created_time`) USING BTREE,
  CONSTRAINT `device_maintenance_record_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `smart_device` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='设备维护记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_maintenance_record`
--

/*!40000 ALTER TABLE `device_maintenance_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_maintenance_record` ENABLE KEYS */;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dictionary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典值',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(启用), INACTIVE(禁用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_dict_type_code` (`dict_type`,`dict_code`) USING BTREE,
  KEY `idx_dict_type` (`dict_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (1,'community','CHAOYANGPARK','朝阳公园社区','朝阳公园社区',1,'ACTIVE','朝阳区朝阳公园社区','2025-05-25 07:13:22','2025-05-29 08:09:07'),(2,'community','ZHONGGUANCUN','中关村社区','中关村社区',2,'ACTIVE','海淀区中关村社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(3,'community','XIDAN','西单社区','西单社区',3,'ACTIVE','西城区西单社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(4,'community','WANGFUJING','王府井社区','王府井社区',4,'ACTIVE','东城区王府井社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(5,'community','SANLITUN','三里屯社区','三里屯社区',5,'ACTIVE','朝阳区三里屯社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(6,'community','ZHONGSHAN','中山社区','中山社区',6,'ACTIVE','西城区中山社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(7,'community','DONGZHIMEN','东直门社区','东直门社区',7,'ACTIVE','东城区东直门社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(8,'community','HAIDIAN','海淀社区','海淀社区',8,'ACTIVE','海淀区海淀社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(9,'community','FENGTAI','丰台社区','丰台社区',9,'ACTIVE','丰台区丰台社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(10,'community','SHIJINGSHAN','石景山社区','石景山社区',10,'ACTIVE','石景山区石景山社区','2025-05-25 07:13:22','2025-05-25 07:13:22'),(11,'pensionType','HOME_CARE','居家养老','居家养老',1,'ACTIVE','在家中接受养老服务','2025-05-25 07:13:29','2025-05-25 07:13:29'),(12,'pensionType','COMMUNITY_DAY_CARE','社区养老（日照）','社区养老（日照）',2,'ACTIVE','社区日间照料中心','2025-05-25 07:13:29','2025-05-25 07:13:29'),(13,'pensionType','INSTITUTIONAL_CARE','机构养老（养老院）','机构养老（养老院）',3,'ACTIVE','入住养老机构','2025-05-25 07:13:29','2025-05-25 07:13:29'),(14,'pensionType','MEDICAL_CARE','医养结合','医养结合',4,'ACTIVE','医疗与养老相结合','2025-05-25 07:13:29','2025-05-25 07:13:29'),(15,'pensionType','ASSISTED_LIVING','辅助生活','辅助生活',5,'ACTIVE','半独立生活方式','2025-05-25 07:13:29','2025-05-25 07:13:29'),(16,'pensionType','RESPITE_CARE','临时照护','临时照护',6,'ACTIVE','短期临时照护服务','2025-05-25 07:13:29','2025-05-25 07:13:29'),(17,'pensionType','HOSPICE_CARE','临终关怀','临终关怀',7,'ACTIVE','生命末期关怀服务','2025-05-25 07:13:29','2025-05-25 07:13:29'),(18,'pensionType','SMART_ELDERLY','智慧养老','智慧养老',8,'ACTIVE','基于智能技术的养老服务','2025-05-25 07:13:29','2025-05-25 07:13:29'),(19,'community','TEST_COMMUNITY','测试社区','测试社区',999,'ACTIVE','这是一个测试数据','2025-05-25 07:28:04','2025-05-25 07:28:04'),(30,'serviceType','LIFE_CARE','生活照料','生活照料',1,'ACTIVE','日常生活照料服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(31,'serviceType','MEDICAL_CARE','医疗护理','医疗护理',2,'ACTIVE','医疗护理服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(32,'serviceType','REHABILITATION','康复训练','康复训练',3,'ACTIVE','康复训练服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(33,'serviceType','PSYCHOLOGICAL_SUPPORT','心理支持','心理支持',4,'ACTIVE','心理疏导和支持服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(34,'serviceType','ENTERTAINMENT','文娱活动','文娱活动',5,'ACTIVE','文化娱乐活动服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(35,'serviceType','EMERGENCY_RESCUE','紧急救援','紧急救援',6,'ACTIVE','紧急救援服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(36,'serviceType','HOUSEKEEPING','家政服务','家政服务',7,'ACTIVE','家政清洁服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(37,'serviceType','MEAL_DELIVERY','送餐服务','送餐服务',8,'ACTIVE','餐饮配送服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(38,'serviceType','TRANSPORTATION','交通接送','交通接送',9,'ACTIVE','交通接送服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(39,'serviceType','CONSULTATION','咨询服务','咨询服务',10,'ACTIVE','各类咨询服务','2025-05-25 08:28:08','2025-05-25 08:28:08'),(40,'elderly_type','normal','普通老人','normal',1,'ACTIVE','身体健康，生活能够自理的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(41,'elderly_type','empty_nest','空巢老人','empty_nest',2,'ACTIVE','子女长期不在身边的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(42,'elderly_type','living_alone','独居老人','living_alone',3,'ACTIVE','独自居住的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(43,'elderly_type','disabled','失能老人','disabled',4,'ACTIVE','生活不能自理的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(44,'elderly_type','elderly','高龄老人','elderly',5,'ACTIVE','80岁以上的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(45,'elderly_type','low_income','低收入老人','low_income',6,'ACTIVE','经济困难的老人','2025-05-26 06:09:14','2025-05-26 06:09:14'),(46,'elderly_type','special_care','特殊照护老人','special_care',7,'ACTIVE','需要特殊照护的老人','2025-05-26 06:09:14','2025-05-26 06:09:14');
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;

--
-- Table structure for table `elderly_family_member`
--

DROP TABLE IF EXISTS `elderly_family_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `elderly_family_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '家属ID',
  `elderly_id` bigint NOT NULL COMMENT '老人ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家属姓名',
  `relationship` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '与老人关系',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `elderly_id` (`elderly_id`) USING BTREE,
  CONSTRAINT `elderly_family_member_ibfk_1` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='老人家属信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `elderly_family_member`
--

/*!40000 ALTER TABLE `elderly_family_member` DISABLE KEYS */;
INSERT INTO `elderly_family_member` VALUES (3,2,'李强','儿子','13821111111','2025-05-24 15:52:59','2025-05-24 15:52:59'),(4,2,'王美玲','女儿','13822222222','2025-05-24 15:52:59','2025-05-24 15:52:59'),(9,1,'王小明','儿子','13811111111','2025-05-25 07:45:45','2025-05-25 07:45:45'),(10,1,'张丽华','儿媳','13812222222','2025-05-25 07:45:45','2025-05-25 07:45:45'),(17,3,'张伟','儿子','13831111111','2025-05-25 07:55:21','2025-05-25 07:55:21'),(18,3,'张敏','女儿','13832222222','2025-05-25 07:55:21','2025-05-25 07:55:21');
/*!40000 ALTER TABLE `elderly_family_member` ENABLE KEYS */;

--
-- Table structure for table `elderly_profile`
--

DROP TABLE IF EXISTS `elderly_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `elderly_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `id_card_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `photo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '照片URL',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '家庭住址',
  `community` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属社区',
  `pension_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '养老类型',
  `elderly_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '老人类型（字典值）',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '过往病史',
  `allergy_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '过敏史',
  `physical_exam_report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '体检报告',
  `current_health_status` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '当前健康状况',
  `care_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '护理等级',
  `ability_assessment` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '能力评估',
  `living_habits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '生活习惯',
  `hobbies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '兴趣爱好',
  `religious_belief` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '宗教信仰',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `organization_id` bigint DEFAULT NULL COMMENT '所属机构ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id_card_number` (`id_card_number`) USING BTREE,
  KEY `organization_id` (`organization_id`) USING BTREE,
  KEY `idx_elderly_type` (`elderly_type`),
  CONSTRAINT `elderly_profile_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='人员档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `elderly_profile`
--

/*!40000 ALTER TABLE `elderly_profile` DISABLE KEYS */;
INSERT INTO `elderly_profile` VALUES (1,'王建国','男','1938-03-15','110101193803152011','13601234567','https://i.pravatar.cc/150?img=1','北京市朝阳区朝阳公园南路123号','中山社区','机构养老（养老院）','elderly','高血压病史15年，糖尿病史8年，曾患脑梗塞','青霉素过敏','2024-11-01体检：血压150/90mmHg，血糖7.2mmol/L，心电图正常','血压血糖控制良好，行动能力正常','二级护理','能力完好','早睡早起，喜欢晨练，饮食清淡','太极拳、象棋、书法','无','退休干部，生活自理能力强',2,'2025-05-24 15:52:54','2025-05-26 06:09:14'),(2,'李秀梅','女','1941-07-22','110101194107228524','13701234568','https://i.pravatar.cc/150?img=2','北京市海淀区中关村大街45号','中关村社区','社区养老（日照）','disabled','骨质疏松，轻度认知障碍，慢性胃炎','无明显过敏史','2024-10-15体检：骨密度偏低，记忆力测试轻度异常','需要适当看护，容易忘事','三级护理','轻度失能','喜欢听戏，午休时间长','京剧、编织、养花','佛教','需要提醒服药，家属每日探视',NULL,'2025-05-24 15:52:54','2025-05-26 06:09:14'),(3,'张志强','男','1930-12-08','110101193012083719','13801234569','https://i.pravatar.cc/150?img=3','北京市西城区西单北大街78号','中关村社区','机构养老（养老院）','disabled','冠心病，轻度脑萎缩，前列腺增生','磺胺类药物过敏','2024-09-20体检：心脏彩超示轻度冠脉狭窄，认知功能尚可','心脏功能稍差，需要定期复查','三级护理','轻度失能','作息规律，饮食需要低盐低脂','读报、听广播、下棋','无','入住养老院3年，适应良好',3,'2025-05-24 15:52:54','2025-05-26 06:38:59'),(4,'张建国','男','1945-03-15','110101194503151234','13812345001',NULL,'朝阳区朝阳公园南路15号院3单元201','朝阳公园社区','居家养老','normal','高血压病史10年',NULL,NULL,'身体状况良好',NULL,'能力完好','早睡早起，喜欢晨练','太极拳、下棋',NULL,'退休教师，身体健康',NULL,'2025-05-26 06:31:31','2025-05-26 06:31:31'),(5,'李秀英','女','1940-07-22','110101194007221235','13812345002',NULL,'海淀区中关村大街32号楼2单元502','中关村社区','COMMUNITY_DAY_CARE','empty_nest','糖尿病，冠心病',NULL,NULL,'需要定期监测血糖',NULL,'轻度失能','规律作息，注意饮食','看电视、听戏曲',NULL,'子女在外地工作，独自居住',NULL,'2025-05-26 06:31:31','2025-05-26 06:31:31'),(6,'王福寿','男','1935-12-08','110101193512081236','13812345003',NULL,'丰台社区','丰台社区','ASSISTED_LIVING','elderly','脑梗后遗症',NULL,NULL,'需要辅助行走',NULL,'中度失能','需要协助日常生活','听音乐、晒太阳',NULL,'88岁高龄，需要专业护理',NULL,'2025-05-26 06:31:31','2025-05-26 06:31:31'),(7,'赵美华','女','1948-05-30','110101194805301237','13812345004',NULL,'东城区东直门内大街66号院4单元102','东直门社区','居家养老','living_alone','关节炎，骨质疏松',NULL,NULL,'关节疼痛，行动缓慢',NULL,'能力完好','喜欢安静，作息规律','编织、听广播',NULL,'丧偶独居，性格内向',NULL,'2025-05-26 06:31:31','2025-05-26 06:31:31'),(8,'刘国强','男','1942-09-14','110101194209141238','13812345005',NULL,'西城区三里屯北路12号院2单元401','三里屯社区','ASSISTED_LIVING','disabled','阿尔茨海默病',NULL,NULL,'认知功能严重下降',NULL,'重度失能','需要24小时护理','无法参与',NULL,'病情严重，需要专业护理',NULL,'2025-05-26 06:31:31','2025-05-26 06:31:31'),(9,'陈桂花','女','1950-11-25','110101195011251239','13812345006',NULL,'朝阳区朝阳公园西路8号院1单元203','朝阳公园社区','居家养老','low_income','慢性支气管炎，贫血',NULL,NULL,'体质较弱，容易感冒',NULL,'轻度失能','节俭朴素，早睡早起','看电视、聊天',NULL,'退休工人，收入微薄',NULL,'2025-05-26 06:31:55','2025-05-26 06:31:55'),(10,'孙志明','男','1943-02-18','110101194302181240','13812345007',NULL,'海淀区海淀路45号院3单元501','海淀社区','ASSISTED_LIVING','special_care','肺癌术后，化疗中',NULL,NULL,'免疫力低下，需要密切监护',NULL,'中度失能','需要特殊饮食和护理','听音乐',NULL,'癌症患者，需要专业医疗护理',NULL,'2025-05-26 06:31:55','2025-05-26 06:31:55'),(11,'马淑兰','女','1946-08-12','110101194608121241','13812345008',NULL,'丰台区中山路33号院2单元302','中山社区','COMMUNITY_DAY_CARE','normal','轻微高血脂',NULL,NULL,'身体基本健康，定期体检',NULL,'能力完好','爱干净，喜欢整理','跳广场舞、唱歌',NULL,'性格开朗，社交活跃',NULL,'2025-05-26 06:31:55','2025-05-26 06:31:55'),(12,'周建华','男','1944-06-03','110101194406031242','13812345009',NULL,'东城区东直门外大街22号院1单元401','东直门社区','居家养老','empty_nest','前列腺增生，白内障',NULL,NULL,'视力下降，夜尿频繁',NULL,'能力完好','喜欢安静，规律生活','听收音机、遛鸟',NULL,'退休干部，子女工作忙碌',NULL,'2025-05-26 06:31:55','2025-05-26 06:31:55'),(13,'吴凤英','女','1938-04-20','110101193804201243','13812345010',NULL,'西城区三里屯南路18号院4单元201','三里屯社区','ASSISTED_LIVING','elderly','心房颤动，慢性肾病',NULL,NULL,'心律不齐，肾功能下降',NULL,'轻度失能','需要协助部分日常活动','听戏、看照片',NULL,'85岁高龄，需要定期检查',NULL,'2025-05-26 06:31:55','2025-05-26 06:31:55'),(14,'胡德福','男','1947-10-15','110101194710151244','13812345011',NULL,'朝阳区朝阳公园东路25号院3单元102','朝阳公园社区','居家养老','living_alone','胃溃疡，失眠',NULL,NULL,'消化功能不佳，睡眠质量差',NULL,'能力完好','饮食清淡，作息不规律','看书、写字、养鱼',NULL,'退休会计，性格孤僻',NULL,'2025-05-26 06:32:20','2025-05-26 06:32:20'),(15,'郑玉梅','女','1941-01-28','110101194101281245','13812345012',NULL,'海淀区中关村南大街55号院2单元303','中关村社区','ASSISTED_LIVING','disabled','脑血管意外后遗症',NULL,NULL,'半身不遂，需要鼻饲',NULL,'重度失能','完全卧床，需要翻身护理','无法参与',NULL,'中风后遗症严重，需要康复护理',NULL,'2025-05-26 06:32:20','2025-05-26 06:32:20'),(16,'高建设','男','1949-12-05','110101194912051246','13812345013',NULL,'丰台区丰台北路77号院1单元402','丰台社区','居家养老','low_income','慢性阻塞性肺病',NULL,NULL,'呼吸功能下降，关节疼痛',NULL,'轻度失能','需要氧疗，行动缓慢','听广播',NULL,'退休工人，肺病严重',NULL,'2025-05-26 06:32:20','2025-05-26 06:32:20'),(17,'林秀珍','女','1945-07-08','110101194507081247','13812345014',NULL,'东城区东直门南大街41号院3单元501','东直门社区','ASSISTED_LIVING','special_care','尿毒症，血液透析',NULL,NULL,'肾功能衰竭，需要定期透析',NULL,'中度失能','严格控制饮食和水分','听音乐、看电视',NULL,'肾病晚期，每周透析3次',NULL,'2025-05-26 06:32:20','2025-05-26 06:32:20'),(18,'杨志强','男','1946-03-22','110101194603221248','13812345015',NULL,'西城区三里屯西路9号院2单元201','三里屯社区','COMMUNITY_DAY_CARE','normal','轻微耳聋',NULL,NULL,'听力下降，其他正常',NULL,'能力完好','喜欢运动，饮食规律','乒乓球、游泳',NULL,'退休工程师，身体健康',NULL,'2025-05-26 06:32:20','2025-05-26 06:32:20'),(19,'何美丽','女','1943-09-30','110101194309301249','13812345016',NULL,'朝阳区朝阳公园北路31号院1单元301','朝阳公园社区','居家养老','empty_nest','骨质疏松，白内障手术史',NULL,NULL,'骨密度低，视力恢复良好',NULL,'能力完好','小心谨慎，防止跌倒','看电视、聊天',NULL,'退休护士，子女在国外',NULL,'2025-05-26 06:32:46','2025-05-26 06:32:46'),(20,'袁国庆','男','1936-10-01','110101193610011250','13812345017',NULL,'海淀区海淀南路63号院4单元401','海淀社区','ASSISTED_LIVING','elderly','多器官功能衰退',NULL,NULL,'整体机能下降，需要综合护理',NULL,'中度失能','需要协助大部分日常活动','听戏曲',NULL,'87岁高龄，多种慢性病',NULL,'2025-05-26 06:32:46','2025-05-26 06:32:46'),(21,'谢春花','女','1948-04-15','110101194804151251','13812345018',NULL,'丰台区中山东路19号院3单元102','中山社区','居家养老','living_alone','抑郁症，高血压',NULL,NULL,'情绪低落，血压波动',NULL,'能力完好','作息不规律，食欲不振','很少参与活动',NULL,'丧偶后情绪低落，需要心理疏导',NULL,'2025-05-26 06:32:46','2025-05-26 06:32:46'),(22,'冯德华','男','1940-11-18','110101194011181252','13812345019',NULL,'东城区东直门北大街28号院2单元502','东直门社区','ASSISTED_LIVING','disabled','帕金森病晚期',NULL,NULL,'震颤严重，认知功能下降',NULL,'重度失能','需要24小时专业护理','无法参与',NULL,'帕金森病晚期，生活完全不能自理',NULL,'2025-05-26 06:32:46','2025-05-26 06:32:46'),(23,'曾爱莲','女','1957-06-12','110101195706121253','13812345020',NULL,'西城区三里屯东路7号院1单元203','三里屯社区','居家养老','low_income','类风湿关节炎，贫血',NULL,NULL,'关节变形，活动受限',NULL,'轻度失能','需要协助家务，行动缓慢','听广播、晒太阳',NULL,'退休清洁工，关节炎严重',NULL,'2025-05-26 06:32:46','2025-05-26 06:38:28');
/*!40000 ALTER TABLE `elderly_profile` ENABLE KEYS */;

--
-- Table structure for table `health_monitoring_data`
--

DROP TABLE IF EXISTS `health_monitoring_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `elderly_id` bigint NOT NULL COMMENT '老人ID',
  `device_id` bigint DEFAULT NULL COMMENT '设备ID',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `data_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据类型',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '监测值',
  `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `is_abnormal` tinyint(1) DEFAULT '0' COMMENT '是否异常',
  `alarm_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '告警级别',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `elderly_id` (`elderly_id`) USING BTREE,
  KEY `device_id` (`device_id`) USING BTREE,
  CONSTRAINT `health_monitoring_data_ibfk_1` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `health_monitoring_data_ibfk_2` FOREIGN KEY (`device_id`) REFERENCES `smart_device` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='健康监测数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_monitoring_data`
--

/*!40000 ALTER TABLE `health_monitoring_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `health_monitoring_data` ENABLE KEYS */;

--
-- Table structure for table `homepage_statistics`
--

DROP TABLE IF EXISTS `homepage_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homepage_statistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `record_date` date NOT NULL COMMENT '记录日期',
  `elderly_population_count` int DEFAULT NULL COMMENT '老龄人口总数',
  `organization_facility_count` int DEFAULT NULL COMMENT '养老机构及设施总数',
  `practitioner_count` int DEFAULT NULL COMMENT '从业人员总数',
  `subsidy_amount_total` decimal(15,2) DEFAULT NULL COMMENT '发放补贴总额',
  `elderly_type_normal_count` int DEFAULT NULL COMMENT '老人类型-正常老人数量',
  `elderly_type_empty_nester_count` int DEFAULT NULL COMMENT '老人类型-空巢老人数量',
  `elderly_type_living_alone_count` int DEFAULT NULL COMMENT '老人类型-独居老人数量',
  `elderly_type_disabled_count` int DEFAULT NULL COMMENT '老人类型-残疾老人数量',
  `elderly_type_advanced_age_count` int DEFAULT NULL COMMENT '老人类型-高龄老人数量',
  `ability_assessment_intact_count` int DEFAULT NULL COMMENT '能力评估-能力完好数量',
  `ability_assessment_mild_disability_count` int DEFAULT NULL COMMENT '能力评估-轻度失能数量',
  `ability_assessment_moderate_disability_count` int DEFAULT NULL COMMENT '能力评估-中度失能数量',
  `ability_assessment_severe_disability_count` int DEFAULT NULL COMMENT '能力评估-重度失能数量',
  `ability_assessment_unassessed_count` int DEFAULT NULL COMMENT '能力评估-未评估数量',
  `age_distribution_60_69_count` int DEFAULT NULL COMMENT '年龄分布-60至69岁数量',
  `age_distribution_70_79_count` int DEFAULT NULL COMMENT '年龄分布-70至79岁数量',
  `age_distribution_80_89_count` int DEFAULT NULL COMMENT '年龄分布-80至89岁数量',
  `age_distribution_90_plus_count` int DEFAULT NULL COMMENT '年龄分布-90岁以上数量',
  `connected_devices_count` int DEFAULT NULL COMMENT '接入设备总数',
  `device_status_online_count` int DEFAULT NULL COMMENT '设备状态-在线数量',
  `device_status_offline_count` int DEFAULT NULL COMMENT '设备状态-离线数量',
  `device_status_fault_count` int DEFAULT NULL COMMENT '设备状态-故障数量',
  `total_alarms_today` int DEFAULT NULL COMMENT '今日告警总数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `record_date` (`record_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='首页统计数据快照表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homepage_statistics`
--

/*!40000 ALTER TABLE `homepage_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `homepage_statistics` ENABLE KEYS */;

--
-- Table structure for table `menu_permission`
--

DROP TABLE IF EXISTS `menu_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父权限ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单/权限名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `permission_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识符',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '前端路由路径',
  `component_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `is_visible` tinyint(1) DEFAULT '1' COMMENT '是否在菜单中显示',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '澶囨敞璇存槑',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `permission_key` (`permission_key`) USING BTREE,
  KEY `parent_id` (`parent_id`) USING BTREE,
  CONSTRAINT `menu_permission_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `menu_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_permission`
--

/*!40000 ALTER TABLE `menu_permission` DISABLE KEYS */;
INSERT INTO `menu_permission` VALUES (1,NULL,'首页','MENU','home:view','/','HomeView','House',1,1,1,'111','2025-05-25 11:52:32','2025-05-29 10:00:36'),(2,NULL,'机构管理','MENU','organization:view','/organization-management','views/organization/OrganizationIndex','OfficeBuilding',2,1,1,'','2025-05-25 11:52:32','2025-05-29 10:00:36'),(3,NULL,'人员档案','MENU','elderly:view','/elderly-profiles','views/elderly/ElderlyProfileList','User',3,1,1,'','2025-05-25 11:52:32','2025-05-29 10:00:36'),(4,NULL,'智能设备','CATALOG','smart-device:view','/smart-device','','Monitor',4,1,1,'分发1','2025-05-25 11:52:32','2025-05-29 10:00:36'),(5,NULL,'服务记录','MENU','service:view','/service-records','views/service-record/ServiceRecordList','Document',5,1,1,NULL,'2025-05-25 11:52:32','2025-05-29 10:00:36'),(6,NULL,'志愿者管理','MENU','volunteer:view','/volunteers','views/volunteer/VolunteerListSimple','Avatar',6,0,1,'','2025-05-25 11:52:32','2025-05-29 10:00:36'),(7,NULL,'系统管理','CATALOG','system:view','/system',NULL,'Setting',7,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(11,4,'设备管理','MENU','smart-device:manage','/smart-devices','views/smart-device/SmartDeviceList','Monitor',1,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(12,4,'告警管理','MENU','smart-device:alarm','/device-alarms','views/smart-device/DeviceAlarmList','Warning',2,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(21,7,'用户管理','MENU','system:user:manage','/system/users','views/system/SystemUserList','User',1,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(22,7,'角色管理','MENU','system:role:manage','/system/roles','views/system/RoleList','Avatar',2,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(23,7,'权限管理','MENU','system:permission:manage','/system/permissions','views/system/PermissionList','Key',3,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(24,7,'菜单管理','MENU','system:menu:manage','/system/menus','views/system/MenuList','Menu',4,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(25,7,'日志管理','MENU','system:log:manage','/system/logs','views/system/LogList','Document',5,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01'),(26,7,'字典管理','MENU','system:dict:manage','/system/dictionaries','views/system/DictionaryManagementSimple','Collection',6,1,1,NULL,'2025-05-25 11:52:32','2025-05-25 11:53:01');
/*!40000 ALTER TABLE `menu_permission` ENABLE KEYS */;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作用户',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `operation_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '操作描述',
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `log_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'INFO' COMMENT '日志级别',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '用户代理',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `response_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '响应结果',
  `request_time` int DEFAULT NULL COMMENT '请求时长（毫秒）',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `error_stack` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '错误堆栈',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_operation_type` (`operation_type`) USING BTREE,
  KEY `idx_log_level` (`log_level`) USING BTREE,
  KEY `idx_module` (`module`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `system_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,'admin',1,'CREATE','创建了新用户 \"张三\"','用户管理','INFO','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36','/api/system-users','POST','{\"username\":\"zhangsan\",\"fullName\":\"张三\"}','{\"success\":true,\"data\":{\"id\":1}}',156,NULL,NULL,'2025-05-24 15:53:12'),(2,'admin',1,'LOGIN','管理员登录系统','用户认证','INFO','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36','/api/auth/login','POST','{\"username\":\"admin\"}','{\"success\":true,\"token\":\"xxx\"}',78,NULL,NULL,'2025-05-24 15:53:12');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '机构ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构名称',
  `short_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '机构简称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '机构类型：居家养老单位、社区养老单位（日照）、机构养老单位（养老院）',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网址',
  `establishment_date` date DEFAULT NULL COMMENT '成立时间',
  `license_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '许可证号',
  `business_scope` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '经营范围',
  `bed_count` int DEFAULT NULL COMMENT '床位数量',
  `actual_service_count` int DEFAULT NULL COMMENT '实际服务人数',
  `charging_standard` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '收费标准',
  `area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '机构规模（占地面积、建筑面积等）',
  `director_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人姓名',
  `director_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人联系方式',
  `employee_count` int DEFAULT NULL COMMENT '员工总数',
  `professional_nurse_count` int DEFAULT NULL COMMENT '专业护理人员数量',
  `fire_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消防许可证',
  `sanitary_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '卫生许可证',
  `medical_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医疗机构执业许可证',
  `other_qualifications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '其他资质证书',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '机构描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='机构信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
INSERT INTO `organization` VALUES (1,'测试中文机构名称','中文简称','机构养老单位','北京市朝阳区测试地址123号',116.4203000,39.9289000,'010-12345678','test@example.com','www.yangguang.com','2015-01-15','YL2015001','提供养老服务、医疗保健、文化娱乐等综合服务',200,180,'标准间：3000元/月；单人间：4000元/月；特需间：6000元/月','占地面积：5000㎡，建筑面积：8000㎡','张三','13901234567',50,20,'XF202301001','WS202301001','YY202301001','养老机构等级评定5A级','io','2025-05-24 15:52:37','2025-05-27 07:31:15'),(2,'和谐养老院','和谐院','机构养老单位（养老院）','上海市浦东新区和谐路456号',121.5054000,31.2304000,'021-87654321','hexie@example.com','www.hexie.com','2016-03-20','YL2016002','养老照护、康复理疗、营养配餐',150,130,'双人间：3500元/月；单人间：4500元/月','占地面积：4000㎡，建筑面积：6000㎡','李华','13812345678',40,15,'XF202302002','WS202302002','YY202302002','医养结合示范单位','111','2025-05-24 15:52:37','2025-05-27 07:31:15'),(3,'乐龄社区日照中心','乐龄中心','社区养老单位（日照）','广州市天河区乐龄路789号',113.3245000,23.1291000,'020-98765432','leling@example.com','www.leling.com','2017-05-10','YL2017003','日间照料、康复训练、文娱活动',0,50,'日托：100元/天；半月托：1400元/月；月托：2600元/月','建筑面积：1200㎡','王芳','13923456789',15,5,'XF202303003','WS202303003','YY202303003','社区养老示范点',NULL,'2025-05-24 15:52:37','2025-05-27 07:31:15');
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色键',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态：1启用，0禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `role_name` (`role_name`) USING BTREE,
  UNIQUE KEY `role_key` (`role_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'超级管理员','SUPER_ADMIN','拥有所有权限','2025-05-24 15:52:34','2025-05-24 15:52:34','1'),(2,'机构管理员','ORG_ADMIN','管理本机构相关事务','2025-05-24 15:52:34','2025-05-24 15:52:34','1'),(3,'普通用户','USER','基本查看权限','2025-05-24 15:52:34','2025-05-24 16:00:23','1'),(4,'机构负责人','ORG_DIRECTOR','机构负责人，负责机构日常管理和运营','2025-05-25 06:37:11','2025-05-25 06:37:11','1'),(5,'机构从业人员','ORG_SERVER','机构从业人员','2025-05-27 02:05:23','2025-05-29 09:48:25','1');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`) USING BTREE,
  KEY `permission_id` (`permission_id`) USING BTREE,
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `menu_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,1),(1,2),(4,2),(5,2),(1,3),(4,3),(5,3),(1,4),(4,4),(5,4),(1,5),(4,5),(5,5),(1,6),(5,6),(1,7),(1,11),(4,11),(5,11),(1,12),(4,12),(5,12),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;

--
-- Table structure for table `service_record`
--

DROP TABLE IF EXISTS `service_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `elderly_id` bigint NOT NULL COMMENT '老人ID',
  `service_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务内容',
  `service_time` datetime NOT NULL COMMENT '服务时间',
  `service_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务地址',
  `service_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务类型',
  `service_duration` decimal(5,2) DEFAULT NULL COMMENT '服务时长',
  `service_provider_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务提供方类型',
  `service_provider_id` bigint DEFAULT NULL COMMENT '服务提供方ID',
  `service_provider_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务提供方姓名',
  `work_order_price` decimal(10,2) DEFAULT NULL COMMENT '工单价格',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务状态',
  `evaluation_score` int DEFAULT NULL COMMENT '评价分数',
  `evaluation_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `elderly_id` (`elderly_id`) USING BTREE,
  CONSTRAINT `service_record_ibfk_1` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='服务记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_record`
--

/*!40000 ALTER TABLE `service_record` DISABLE KEYS */;
INSERT INTO `service_record` VALUES (1,1,'日常生活护理服务','2024-01-15 10:00:00','北京市朝阳区','生活照料',2.00,'机构员工',NULL,'张护士',50.00,'已完成',NULL,NULL,'2025-05-24 15:53:02','2025-05-25 08:34:11'),(2,2,'健康检查服务','2024-01-16 14:30:00','北京市海淀区','医疗护理',0.50,'志愿者',1,'aa',80.00,'进行中',NULL,'','2025-05-24 15:53:02','2025-05-25 08:34:11'),(3,3,'医疗咨询服务','2024-01-15 10:00:00','北京朝阳养老院','医疗护理',0.50,'志愿者',1,'aa',100.00,'待处理',NULL,'','2025-05-24 15:53:02','2025-05-25 08:34:11');
/*!40000 ALTER TABLE `service_record` ENABLE KEYS */;

--
-- Table structure for table `smart_device`
--

DROP TABLE IF EXISTS `smart_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `smart_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `device_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备编号',
  `device_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备类型',
  `device_brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备品牌',
  `device_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备型号',
  `device_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '在线' COMMENT '设备状态',
  `purchase_date` date DEFAULT NULL COMMENT '购买日期',
  `installation_location` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '安装位置',
  `elderly_id` bigint DEFAULT NULL COMMENT '绑定的老人ID',
  `organization_id` bigint DEFAULT NULL COMMENT '所属机构ID',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `mac_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MAC地址',
  `wifi_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Wi-Fi配置信息',
  `communication_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通信协议',
  `data_collection_frequency` int DEFAULT '60' COMMENT '数据采集频率（秒）',
  `alarm_threshold` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '报警阈值配置',
  `battery_threshold` int DEFAULT '20' COMMENT '电量阈值',
  `real_time_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '实时数据',
  `battery_level` int DEFAULT NULL COMMENT '电量百分比',
  `signal_strength` int DEFAULT NULL COMMENT '信号强度',
  `last_communication_time` datetime DEFAULT NULL COMMENT '最后通信时间',
  `data_upload_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '数据上传状态',
  `warranty_expiry_date` date DEFAULT NULL COMMENT '保修期限',
  `maintenance_cycle` int DEFAULT NULL COMMENT '维护周期（天）',
  `next_maintenance_date` date DEFAULT NULL COMMENT '下次维护时间',
  `failure_count` int DEFAULT '0' COMMENT '故障次数',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `device_code` (`device_code`) USING BTREE,
  KEY `idx_device_code` (`device_code`) USING BTREE,
  KEY `idx_device_type` (`device_type`) USING BTREE,
  KEY `idx_device_status` (`device_status`) USING BTREE,
  KEY `idx_elderly_id` (`elderly_id`) USING BTREE,
  KEY `idx_organization_id` (`organization_id`) USING BTREE,
  KEY `idx_created_time` (`created_time`) USING BTREE,
  CONSTRAINT `smart_device_ibfk_1` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `smart_device_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='智能设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smart_device`
--

/*!40000 ALTER TABLE `smart_device` DISABLE KEYS */;
INSERT INTO `smart_device` VALUES (1,'DEV001','智能手环Pro','健康监测设备','小米','Mi Band 7','在线','2023-01-15','张伟 卧室',1,1,'192.168.1.101','AA:BB:CC:DD:EE:01',NULL,'MQTT',60,'{\"heart_rate_max\": 120, \"heart_rate_min\": 60}',20,'{\"heart_rate\": 78, \"steps\": 8500, \"battery\": 85}',85,85,'2024-01-15 14:30:00','正常','2025-01-15',30,'2024-02-15',0,'2023-01-15 10:00:00','2024-01-15 14:30:00','admin',NULL,0),(2,'DEV002','血压监测仪','健康监测设备','欧姆龙','HEM-7156','故障','2023-02-19','李淑华 客厅',2,1,'192.168.1.102','AA:BB:CC:DD:EE:02',NULL,'TCP/IP',300,'{\"systolic_max\": 140, \"diastolic_max\": 90}',16,'{\"systolic\": 125, \"diastolic\": 80, \"battery\": 70}',4,90,'2024-01-15 14:00:00','正常','2025-02-20',60,'2024-03-20',0,'2023-02-20 09:00:00','2025-05-25 15:58:44','admin',NULL,0),(3,'DEV003','智能手环Plus','健康监测设备','华为','HUAWEI Band 8','在线','2024-01-15','卧室床头柜',3,1,'192.168.1.103','00:1B:44:11:3A:B7',NULL,'Bluetooth 5.0',300,'{\"heartRate\": {\"min\": 60, \"max\": 100}, \"steps\": {\"daily\": 8000}}',20,'{\"heartRate\": 72, \"steps\": 5432, \"battery\": 85}',85,-45,'2024-05-27 10:30:00','正常','2025-01-15',30,'2024-06-15',0,'2025-05-27 11:00:07','2025-05-27 11:00:07','admin','admin',0),(4,'DEV004','电子血压计Pro','健康监测设备','欧姆龙','HEM-7200','在线','2024-02-10','客厅茶几',4,1,'192.168.1.104','00:1B:44:11:3A:B8',NULL,'Wi-Fi',1800,'{\"systolic\": {\"min\": 90, \"max\": 140}, \"diastolic\": {\"min\": 60, \"max\": 90}}',15,'{\"systolic\": 125, \"diastolic\": 78, \"pulse\": 68}',78,-38,'2024-05-27 09:15:00','正常','2026-02-10',90,'2024-08-10',0,'2025-05-27 11:00:07','2025-05-27 11:00:07','admin','admin',0),(5,'DEV005','血糖仪','健康监测设备','强生','OneTouch Ultra','在线','2024-01-20','餐厅餐桌',5,1,NULL,'00:1B:44:11:3A:B9',NULL,'Bluetooth 4.0',86400,'{\"glucose\": {\"min\": 4.0, \"max\": 7.0}}',25,'{\"glucose\": 5.8, \"testTime\": \"2024-05-27 08:00:00\"}',65,-42,'2024-05-27 08:00:00','正常','2025-01-20',180,'2024-11-20',0,'2025-05-27 11:00:07','2025-05-27 11:00:07','admin','admin',0),(6,'DEV006','智能体重秤','健康监测设备','华为','HUAWEI Scale 3','在线','2024-03-05','卫生间',6,1,'192.168.1.106','00:1B:44:11:3A:C0',NULL,'Wi-Fi',86400,'{\"weight\": {\"min\": 40, \"max\": 120}, \"bmi\": {\"min\": 18.5, \"max\": 24.9}}',10,'{\"weight\": 68.5, \"bmi\": 22.3, \"bodyFat\": 18.2}',92,-35,'2024-05-27 07:30:00','正常','2026-03-05',60,'2024-07-05',0,'2025-05-27 11:00:08','2025-05-27 11:00:08','admin','admin',0),(7,'DEV007','智能血氧仪','健康监测设备','鱼跃','YX303','在线','2024-02-28','床头柜抽屉',7,1,NULL,'00:1B:44:11:3A:C1',NULL,'Bluetooth 5.0',3600,'{\"spo2\": {\"min\": 95, \"max\": 100}, \"pulse\": {\"min\": 60, \"max\": 100}}',20,'{\"spo2\": 98, \"pulse\": 75, \"pi\": 8.5}',88,-40,'2024-05-27 11:00:00','正常','2025-02-28',90,'2024-08-28',0,'2025-05-27 11:00:08','2025-05-27 11:00:08','admin','admin',0),(8,'DEV008','智能体温计','健康监测设备','博朗','ThermoScan 7','在线','2024-01-30','药箱',8,1,NULL,'00:1B:44:11:3A:C2',NULL,'Bluetooth 4.2',43200,'{\"temperature\": {\"min\": 36.0, \"max\": 37.5}}',15,'{\"temperature\": 36.8, \"measureTime\": \"2024-05-27 06:00:00\"}',72,-38,'2024-05-27 06:00:00','正常','2025-01-30',120,'2024-09-30',0,'2025-05-27 11:00:08','2025-05-27 11:00:08','admin','admin',0),(9,'DEV009','心电监护仪','健康监测设备','迈瑞','BeneHeart R3','在线','2024-03-15','卧室书桌',9,1,'192.168.1.109','00:1B:44:11:3A:C3',NULL,'Wi-Fi',60,'{\"heartRate\": {\"min\": 50, \"max\": 120}, \"rhythm\": \"normal\"}',25,'{\"heartRate\": 68, \"rhythm\": \"sinus\", \"qrs\": 0.08}',76,-42,'2024-05-27 11:15:00','正常','2026-03-15',30,'2024-06-15',0,'2025-05-27 11:00:08','2025-05-27 11:00:08','admin','admin',0),(10,'DEV010','睡眠监测垫','健康监测设备','慕思','DeRUCCI T9','在线','2024-02-20','床垫下方',10,1,'192.168.1.110','00:1B:44:11:3A:C4',NULL,'Wi-Fi',1800,'{\"sleepQuality\": {\"min\": 70, \"max\": 100}, \"deepSleep\": {\"min\": 20, \"max\": 40}}',5,'{\"sleepDuration\": 7.5, \"deepSleep\": 28, \"quality\": 85}',95,-30,'2024-05-27 06:30:00','正常','2026-02-20',180,'2024-11-20',0,'2025-05-27 11:00:08','2025-05-27 11:00:08','admin','admin',0),(11,'DEV011','智能灯光控制器','智能家居设备','飞利浦','Hue Bridge 2.0','在线','2024-01-25','客厅电视柜',11,1,'192.168.1.111','00:1B:44:11:3A:C5',NULL,'Zigbee 3.0',3600,'{\"brightness\": {\"min\": 1, \"max\": 100}, \"colorTemp\": {\"min\": 2700, \"max\": 6500}}',NULL,'{\"brightness\": 75, \"colorTemp\": 4000, \"status\": \"on\"}',NULL,-25,'2024-05-27 11:20:00','正常','2026-01-25',365,'2025-01-25',0,'2025-05-27 11:00:09','2025-05-27 11:00:09','admin','admin',0),(12,'DEV012','智能温控系统','智能家居设备','海尔','U-home T6','在线','2024-02-15','客厅墙面',12,1,'192.168.1.112','00:1B:44:11:3A:C6',NULL,'Wi-Fi',600,'{\"temperature\": {\"min\": 16, \"max\": 30}, \"humidity\": {\"min\": 30, \"max\": 70}}',NULL,'{\"temperature\": 24.5, \"humidity\": 55, \"mode\": \"auto\"}',NULL,-32,'2024-05-27 11:25:00','正常','2026-02-15',180,'2024-11-15',0,'2025-05-27 11:00:09','2025-05-27 11:00:09','admin','admin',0),(13,'DEV013','智能门锁','智能家居设备','德施曼','T86','在线','2024-03-01','入户门',13,1,'192.168.1.113','00:1B:44:11:3A:C7',NULL,'Wi-Fi',86400,'{\"battery\": {\"min\": 20, \"max\": 100}, \"attempts\": {\"max\": 5}}',20,'{\"battery\": 68, \"lockStatus\": \"locked\", \"lastOpen\": \"2024-05-27 08:30:00\"}',68,-28,'2024-05-27 08:30:00','正常','2026-03-01',90,'2024-08-01',0,'2025-05-27 11:00:09','2025-05-27 11:00:09','admin','admin',0),(14,'DEV014','智能窗帘控制器','智能家居设备','杜亚','DT52S','在线','2024-02-05','客厅窗户',14,1,'192.168.1.114','00:1B:44:11:3A:C8',NULL,'Wi-Fi',7200,'{\"position\": {\"min\": 0, \"max\": 100}, \"speed\": {\"min\": 1, \"max\": 10}}',NULL,'{\"position\": 60, \"status\": \"stopped\", \"lightLevel\": 450}',NULL,-35,'2024-05-27 07:00:00','正常','2025-02-05',180,'2024-11-05',0,'2025-05-27 11:00:10','2025-05-27 11:00:10','admin','admin',0),(15,'DEV015','智能插座','智能家居设备','小米','Mi Smart Plug','在线','2024-01-10','客厅沙发旁',15,1,'192.168.1.115','00:1B:44:11:3A:C9',NULL,'Wi-Fi',1800,'{\"power\": {\"max\": 2500}, \"current\": {\"max\": 10}}',NULL,'{\"power\": 125.5, \"current\": 0.52, \"voltage\": 220, \"status\": \"on\"}',NULL,-30,'2024-05-27 11:30:00','正常','2025-01-10',365,'2025-01-10',0,'2025-05-27 11:00:10','2025-05-27 11:00:10','admin','admin',0),(16,'DEV016','智能空气净化器','智能家居设备','小米','Mi Air Purifier 4','在线','2024-03-10','卧室角落',16,1,'192.168.1.116','00:1B:44:11:3A:D0',NULL,'Wi-Fi',300,'{\"pm25\": {\"max\": 75}, \"filterLife\": {\"min\": 10}}',NULL,'{\"pm25\": 15, \"aqi\": 35, \"filterLife\": 85, \"mode\": \"auto\"}',NULL,-33,'2024-05-27 11:35:00','正常','2026-03-10',90,'2024-08-10',0,'2025-05-27 11:00:10','2025-05-27 11:00:10','admin','admin',0),(17,'DEV017','烟感器','安全设备','海康威视','DS-PDSMK-S','在线','2024-01-05','客厅天花板',17,1,'192.168.1.117','00:1B:44:11:3A:D1',NULL,'Wi-Fi',60,'{\"smoke\": {\"threshold\": 3.5}, \"temperature\": {\"max\": 60}}',10,'{\"smoke\": 0.2, \"temperature\": 23.5, \"status\": \"normal\"}',85,-28,'2024-05-27 11:40:00','正常','2026-01-05',365,'2025-01-05',0,'2025-05-27 11:00:10','2025-05-27 11:00:10','admin','admin',0),(18,'DEV018','水浸传感器','安全设备','萤石','T2','在线','2024-02-12','厨房水槽下方',18,1,'192.168.1.118','00:1B:44:11:3A:D2',NULL,'Wi-Fi',300,'{\"waterLevel\": {\"threshold\": 2}}',15,'{\"waterLevel\": 0, \"humidity\": 45, \"status\": \"dry\"}',92,-40,'2024-05-27 11:45:00','正常','2025-02-12',180,'2024-11-12',0,'2025-05-27 11:00:11','2025-05-27 11:00:11','admin','admin',0),(19,'DEV019','SOS紧急报警器','安全设备','中兴','ZTE-SOS01','在线','2024-01-18','床头按钮',19,1,'192.168.1.119','00:1B:44:11:3A:D3',NULL,'4G LTE',86400,'{\"battery\": {\"min\": 20}, \"signal\": {\"min\": -80}}',20,'{\"battery\": 78, \"signal\": -55, \"status\": \"standby\"}',78,-55,'2024-05-27 11:50:00','正常','2025-01-18',90,'2024-07-18',0,'2025-05-27 11:00:11','2025-05-27 11:00:11','admin','admin',0),(20,'DEV020','门窗磁感应器','安全设备','绿米','Aqara Door Sensor','在线','2024-02-25','入户门门框',20,1,NULL,'00:1B:44:11:3A:D4',NULL,'Zigbee 3.0',3600,'{\"battery\": {\"min\": 15}, \"tamper\": \"enabled\"}',15,'{\"status\": \"closed\", \"battery\": 88, \"lastOpen\": \"2024-05-27 08:30:00\"}',88,-45,'2024-05-27 08:30:00','正常','2025-02-25',365,'2025-02-25',0,'2025-05-27 11:00:11','2025-05-27 11:00:11','admin','admin',0),(21,'DEV021','GPS定位器','定位设备','华为','HUAWEI Watch GT 3','在线','2024-03-20','手腕佩戴',1,1,NULL,'00:1B:44:11:3A:D5',NULL,'4G LTE + GPS',1800,'{\"battery\": {\"min\": 15}, \"geoFence\": {\"radius\": 500}}',15,'{\"latitude\": 39.9042, \"longitude\": 116.4074, \"accuracy\": 5, \"speed\": 0}',65,-50,'2024-05-27 11:55:00','正常','2025-03-20',30,'2024-06-20',0,'2025-05-27 11:00:11','2025-05-27 11:00:11','admin','admin',0),(22,'DEV022','室内定位设备','定位设备','思必驰','DUI-1001','在线','2024-02-08','客厅电视柜',2,1,'192.168.1.122','00:1B:44:11:3A:D6',NULL,'UWB + Wi-Fi',600,'{\"accuracy\": {\"min\": 0.3}, \"zone\": \"living_room\"}',NULL,'{\"x\": 3.2, \"y\": 1.8, \"z\": 1.2, \"zone\": \"living_room\", \"accuracy\": 0.2}',NULL,-25,'2024-05-27 12:00:00','正常','2025-02-08',180,'2024-11-08',0,'2025-05-27 11:00:12','2025-05-27 11:00:12','admin','admin',0);
/*!40000 ALTER TABLE `smart_device` ENABLE KEYS */;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `organization_id` bigint DEFAULT NULL COMMENT '所属机构ID',
  `is_admin` tinyint(1) DEFAULT '0' COMMENT '是否为超级管理员',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  KEY `organization_id` (`organization_id`) USING BTREE,
  CONSTRAINT `system_user_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user`
--

/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` VALUES (1,'admin','$2a$10$U2wvjNHKUpxKvDirwT7K8.ZMX9jPuOkFuxlcV0RvJv/JOLMX/h5I2','系统管理员','admin@example.com','18030617315',1,1,1,'2025-05-29 18:28:40','2025-05-24 15:52:45','2025-05-29 10:28:39'),(3,'zhangming','$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi','张明','zhangming@yangguang.com','13901234567',2,0,1,NULL,'2025-05-25 06:37:21','2025-05-25 09:20:25'),(4,'lihua','$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi','李华','lihua@hexie.com','13812345678',2,0,1,NULL,'2025-05-25 06:37:21','2025-05-25 06:37:21'),(5,'wangfang','$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi','王芳','wangfang@leling.com','13923456789',2,0,1,NULL,'2025-05-25 06:37:21','2025-05-25 09:17:05'),(6,'chenwei','$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi','陈伟','chenwei@example.com','13701234567',3,0,1,NULL,'2025-05-25 06:37:21','2025-05-25 09:20:42'),(7,'liujing','$2a$10$rTx0NXMrJZhQZXcRZvKIzOu.1vN9PRLqvE/8D.GtVzKQH1VXEqRJi','刘静','liujing@example.com','13801234567',2,0,1,NULL,'2025-05-25 06:37:21','2025-05-29 09:47:59');
/*!40000 ALTER TABLE `system_user` ENABLE KEYS */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `system_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1),(3,4),(4,4),(6,4),(7,4),(4,5),(5,5),(7,5);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;

--
-- Temporary view structure for view `v_organization_directors`
--

DROP TABLE IF EXISTS `v_organization_directors`;
/*!50001 DROP VIEW IF EXISTS `v_organization_directors`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_organization_directors` AS SELECT 
 1 AS `user_id`,
 1 AS `username`,
 1 AS `director_name`,
 1 AS `director_contact`,
 1 AS `email`,
 1 AS `organization_id`,
 1 AS `organization_name`,
 1 AS `organization_short_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `volunteer`
--

DROP TABLE IF EXISTS `volunteer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '志愿者ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `id_card_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系方式',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '常住地址',
  `household_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '户籍地址',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最高学历',
  `graduation_school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '毕业院校',
  `occupation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职业',
  `work_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作单位',
  `professional_skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '专业技能',
  `hobbies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '兴趣爱好',
  `language_ability` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语言能力',
  `service_intention` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务意向',
  `available_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务时间说明',
  `service_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务经历',
  `emergency_contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_contact_relationship` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '与志愿者关系',
  `emergency_contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系人电话',
  `registration_date` date DEFAULT NULL COMMENT '注册日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE' COMMENT '状态',
  `total_service_hours` decimal(10,2) DEFAULT '0.00' COMMENT '累计服务时长',
  `points` int DEFAULT '0' COMMENT '积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id_card_number` (`id_card_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='志愿者信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer`
--

/*!40000 ALTER TABLE `volunteer` DISABLE KEYS */;
INSERT INTO `volunteer` VALUES (1,'周三','Male',20,NULL,'18030617315','40000','成都市',NULL,'Bachelor',NULL,'软件工程师','北京','计算机',NULL,NULL,'志愿者',NULL,NULL,'李四','同学','18030617315','2025-05-24','ACTIVE',1.00,180,'2025-05-24 15:57:22','2025-05-29 06:06:48');
/*!40000 ALTER TABLE `volunteer` ENABLE KEYS */;

--
-- Table structure for table `volunteer_service_assignment`
--

DROP TABLE IF EXISTS `volunteer_service_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_service_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分配/记录ID',
  `volunteer_id` bigint NOT NULL COMMENT '志愿者ID',
  `project_id` bigint DEFAULT NULL COMMENT '关联的服务项目ID',
  `elderly_id` bigint DEFAULT NULL COMMENT '服务对象老人ID',
  `service_date` date NOT NULL COMMENT '服务日期',
  `service_start_time` time DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` time DEFAULT NULL COMMENT '服务结束时间',
  `service_duration_hours` decimal(5,2) DEFAULT NULL COMMENT '服务时长（小时）',
  `service_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '服务内容描述',
  `feedback_from_elderly` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '老人或家属反馈',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '状态',
  `points_awarded` int DEFAULT '0' COMMENT '授予积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `volunteer_id` (`volunteer_id`) USING BTREE,
  KEY `project_id` (`project_id`) USING BTREE,
  KEY `elderly_id` (`elderly_id`) USING BTREE,
  CONSTRAINT `volunteer_service_assignment_ibfk_1` FOREIGN KEY (`volunteer_id`) REFERENCES `volunteer` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `volunteer_service_assignment_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `volunteer_service_project` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `volunteer_service_assignment_ibfk_3` FOREIGN KEY (`elderly_id`) REFERENCES `elderly_profile` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='志愿者服务记录与分配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_service_assignment`
--

/*!40000 ALTER TABLE `volunteer_service_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `volunteer_service_assignment` ENABLE KEYS */;

--
-- Table structure for table `volunteer_service_project`
--

DROP TABLE IF EXISTS `volunteer_service_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_service_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `service_category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务类别',
  `start_time` datetime DEFAULT NULL COMMENT '项目开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '项目结束时间',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务地点',
  `recruitment_count` int DEFAULT NULL COMMENT '招募人数',
  `current_enrolled_count` int DEFAULT '0' COMMENT '当前报名人数',
  `requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '志愿者要求',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'OPEN' COMMENT '项目状态',
  `organization_id` bigint DEFAULT NULL COMMENT '发布机构ID',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `organization_id` (`organization_id`) USING BTREE,
  KEY `created_by_user_id` (`created_by_user_id`) USING BTREE,
  CONSTRAINT `volunteer_service_project_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `volunteer_service_project_ibfk_2` FOREIGN KEY (`created_by_user_id`) REFERENCES `system_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='志愿者服务项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_service_project`
--

/*!40000 ALTER TABLE `volunteer_service_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `volunteer_service_project` ENABLE KEYS */;

--
-- Dumping routines for database 'pension_management_system'
--

--
-- Final view structure for view `v_organization_directors`
--

/*!50001 DROP VIEW IF EXISTS `v_organization_directors`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_organization_directors` AS select `u`.`id` AS `user_id`,`u`.`username` AS `username`,`u`.`full_name` AS `director_name`,`u`.`phone` AS `director_contact`,`u`.`email` AS `email`,`u`.`organization_id` AS `organization_id`,`o`.`name` AS `organization_name`,`o`.`short_name` AS `organization_short_name` from (((`system_user` `u` join `user_role` `ur` on((`u`.`id` = `ur`.`user_id`))) join `role` `r` on((`ur`.`role_id` = `r`.`id`))) left join `organization` `o` on((`u`.`organization_id` = `o`.`id`))) where ((`r`.`role_key` = 'ORG_DIRECTOR') and (`u`.`is_active` = true)) order by `u`.`organization_id`,`u`.`full_name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-29 18:30:28
