package com.example.pension.constant;

/**
 * 设备类型常量
 */
public class DeviceTypeConstants {
    
    // 健康监测设备
    public static final String HEALTH = "HEALTH";
    public static final String HEALTH_NAME = "健康监测设备";
    
    // 安全设备
    public static final String SAFETY = "SAFETY";
    public static final String SAFETY_NAME = "安全设备";
    
    // 环境监测设备
    public static final String ENVIRONMENT = "ENVIRONMENT";
    public static final String ENVIRONMENT_NAME = "环境监测设备";
    
    // 人脸识别设备
    public static final String FACE_RECOGNITION = "FACE_RECOGNITION";
    public static final String FACE_RECOGNITION_NAME = "人脸识别设备";
    
    // 其他设备
    public static final String OTHER = "OTHER";
    public static final String OTHER_NAME = "其他设备";
    
    // 具体设备子类型
    public static final String SOS = "SOS";
    public static final String SOS_NAME = "SOS报警器";
    
    public static final String SMOKE_DETECTOR = "烟感";
    public static final String SMOKE_DETECTOR_NAME = "烟感器";
    
    public static final String WATER_LEAK = "水浸";
    public static final String WATER_LEAK_NAME = "水浸传感器";
    
    public static final String FALL_DETECTOR = "跌倒";
    public static final String FALL_DETECTOR_NAME = "跌倒检测器";
    
    public static final String GAS_LEAK = "燃气";
    public static final String GAS_LEAK_NAME = "燃气泄露检测器";
    
    public static final String INFRARED = "红外";
    public static final String INFRARED_NAME = "红外探测器";
    
    public static final String DOOR_SENSOR = "门磁";
    public static final String DOOR_SENSOR_NAME = "门磁传感器";
    
    public static final String TEMPERATURE = "温度";
    public static final String TEMPERATURE_NAME = "温度传感器";
    
    public static final String HUMIDITY = "湿度";
    public static final String HUMIDITY_NAME = "湿度传感器";
    
    public static final String LIGHT = "光照";
    public static final String LIGHT_NAME = "光照传感器";
    
    // 人脸识别设备子类型
    public static final String FACE_ACCESS_CONTROL = "人脸门禁";
    public static final String FACE_ACCESS_CONTROL_NAME = "人脸识别门禁";
    
    public static final String FACE_ATTENDANCE = "人脸考勤";
    public static final String FACE_ATTENDANCE_NAME = "人脸识别考勤机";
    
    public static final String FACE_SURVEILLANCE = "人脸监控";
    public static final String FACE_SURVEILLANCE_NAME = "人脸识别监控";
    
    /**
     * 获取设备类型中文名称
     * @param deviceType 设备类型代码
     * @return 中文名称
     */
    public static String getDeviceTypeName(String deviceType) {
        if (deviceType == null) {
            return "未知设备";
        }
        
        switch (deviceType) {
            case HEALTH:
                return HEALTH_NAME;
            case SAFETY:
                return SAFETY_NAME;
            case ENVIRONMENT:
                return ENVIRONMENT_NAME;
            case FACE_RECOGNITION:
                return FACE_RECOGNITION_NAME;
            case OTHER:
                return OTHER_NAME;
            case SOS:
                return SOS_NAME;
            case SMOKE_DETECTOR:
                return SMOKE_DETECTOR_NAME;
            case WATER_LEAK:
                return WATER_LEAK_NAME;
            case FALL_DETECTOR:
                return FALL_DETECTOR_NAME;
            case GAS_LEAK:
                return GAS_LEAK_NAME;
            case INFRARED:
                return INFRARED_NAME;
            case DOOR_SENSOR:
                return DOOR_SENSOR_NAME;
            case TEMPERATURE:
                return TEMPERATURE_NAME;
            case HUMIDITY:
                return HUMIDITY_NAME;
            case LIGHT:
                return LIGHT_NAME;
            case FACE_ACCESS_CONTROL:
                return FACE_ACCESS_CONTROL_NAME;
            case FACE_ATTENDANCE:
                return FACE_ATTENDANCE_NAME;
            case FACE_SURVEILLANCE:
                return FACE_SURVEILLANCE_NAME;
            default:
                return deviceType + "设备";
        }
    }
    
    /**
     * 判断是否为人脸识别设备
     * @param deviceType 设备类型
     * @return 是否为人脸识别设备
     */
    public static boolean isFaceRecognitionDevice(String deviceType) {
        return FACE_RECOGNITION.equals(deviceType) || 
               FACE_ACCESS_CONTROL.equals(deviceType) || 
               FACE_ATTENDANCE.equals(deviceType) || 
               FACE_SURVEILLANCE.equals(deviceType);
    }
    
    /**
     * 判断是否为安全设备
     * @param deviceType 设备类型
     * @return 是否为安全设备
     */
    public static boolean isSafetyDevice(String deviceType) {
        return SAFETY.equals(deviceType) || 
               SOS.equals(deviceType) || 
               SMOKE_DETECTOR.equals(deviceType) || 
               WATER_LEAK.equals(deviceType) || 
               FALL_DETECTOR.equals(deviceType) || 
               GAS_LEAK.equals(deviceType) || 
               INFRARED.equals(deviceType) || 
               DOOR_SENSOR.equals(deviceType);
    }
    
    /**
     * 判断是否为环境监测设备
     * @param deviceType 设备类型
     * @return 是否为环境监测设备
     */
    public static boolean isEnvironmentDevice(String deviceType) {
        return ENVIRONMENT.equals(deviceType) || 
               TEMPERATURE.equals(deviceType) || 
               HUMIDITY.equals(deviceType) || 
               LIGHT.equals(deviceType);
    }
    
    /**
     * 判断是否为健康监测设备
     * @param deviceType 设备类型
     * @return 是否为健康监测设备
     */
    public static boolean isHealthDevice(String deviceType) {
        return HEALTH.equals(deviceType);
    }
}