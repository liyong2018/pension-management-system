package com.example.pension.mapper;

import com.example.pension.dto.ServiceStaffDTO;
import com.example.pension.model.ServiceStaff;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceStaffDTOMapper {

    ServiceStaffDTOMapper INSTANCE = Mappers.getMapper(ServiceStaffDTOMapper.class);

    /**
     * 实体转DTO
     */
    ServiceStaffDTO toDTO(ServiceStaff serviceStaff);

    /**
     * 实体列表转DTO列表
     */
    @Named("toDTOList")
    List<ServiceStaffDTO> toDTO(List<ServiceStaff> serviceStaffs);

    /**
     * DTO转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceStaff toEntity(ServiceStaffDTO serviceStaffDTO);

    /**
     * DTO列表转实体列表
     */
    @Named("toEntityList")
    List<ServiceStaff> toEntity(List<ServiceStaffDTO> serviceStaffDTOs);

    /**
     * 用DTO更新实体（忽略null值）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ServiceStaffDTO serviceStaffDTO, @MappingTarget ServiceStaff serviceStaff);

    /**
     * 部分更新实体（只更新非null字段）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "employeeId", ignore = true) // 工号不允许更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateEntityFromDTO(ServiceStaffDTO serviceStaffDTO, @MappingTarget ServiceStaff serviceStaff);

    /**
     * 创建时的映射（忽略系统字段）
     */
    @Named("toEntityForCreate")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceStaff toEntityForCreate(ServiceStaffDTO serviceStaffDTO);

    /**
     * 简化映射（只包含基本信息）
     */
    @Named("toSimpleDTO")
    @Mapping(target = "medicalHistory", ignore = true)
    @Mapping(target = "trainingRecords", ignore = true)
    @Mapping(target = "evaluationComments", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    ServiceStaffDTO toSimpleDTO(ServiceStaff serviceStaff);

    /**
     * 简化映射列表
     */
    @Named("toSimpleDTOList")
    @IterableMapping(qualifiedByName = "toSimpleDTO")
    List<ServiceStaffDTO> toSimpleDTO(List<ServiceStaff> serviceStaffs);

    /**
     * 统计信息映射（只包含统计相关字段）
     */
    @Named("toStatsDTO")
    @Mapping(target = "medicalHistory", ignore = true)
    @Mapping(target = "trainingRecords", ignore = true)
    @Mapping(target = "evaluationComments", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "emergencyContact", ignore = true)
    @Mapping(target = "emergencyPhone", ignore = true)
    @Mapping(target = "emergencyRelation", ignore = true)
    ServiceStaffDTO toStatsDTO(ServiceStaff serviceStaff);

    /**
     * 导出映射（包含所有需要导出的字段）
     */
    @Named("toExportDTO")
    @Mapping(target = "organizationName", ignore = true) // 需要单独设置
    ServiceStaffDTO toExportDTO(ServiceStaff serviceStaff);

    /**
     * 导出映射列表
     */
    @Named("toExportDTOList")
    @IterableMapping(qualifiedByName = "toExportDTO")
    List<ServiceStaffDTO> toExportDTO(List<ServiceStaff> serviceStaffs);

    /**
     * 从导入数据创建实体
     */
    @Named("toEntityFromImport")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceStaff toEntityFromImport(ServiceStaffDTO serviceStaffDTO);

    /**
     * 个人资料映射（隐藏敏感信息）
     */
    @Named("toProfileDTO")
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "evaluationComments", ignore = true)
    @Mapping(target = "medicalHistory", ignore = true)
    @Mapping(target = "idCard", expression = "java(maskIdCard(serviceStaff.getIdCard()))")
    ServiceStaffDTO toProfileDTO(ServiceStaff serviceStaff);

    /**
     * 掩码身份证号
     */
    default String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "****" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 工作信息映射（只包含工作相关字段）
     */
    @Named("toWorkInfoDTO")
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "emergencyContact", ignore = true)
    @Mapping(target = "emergencyPhone", ignore = true)
    @Mapping(target = "emergencyRelation", ignore = true)
    @Mapping(target = "medicalHistory", ignore = true)
    ServiceStaffDTO toWorkInfoDTO(ServiceStaff serviceStaff);

    /**
     * 联系信息映射（只包含联系相关字段）
     */
    @Named("toContactInfoDTO")
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "performanceScore", ignore = true)
    @Mapping(target = "evaluationComments", ignore = true)
    @Mapping(target = "medicalHistory", ignore = true)
    @Mapping(target = "trainingRecords", ignore = true)
    @Mapping(target = "workExperience", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "skills", ignore = true)
    ServiceStaffDTO toContactInfoDTO(ServiceStaff serviceStaff);
}