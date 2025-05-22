package com.example.pension.api;

import com.example.pension.dto.ElderlyFamilyMemberDTO;
import com.example.pension.dto.ElderlyProfileDTO;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.repository.ElderlyProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ElderlyProfileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElderlyProfileRepository elderlyProfileRepository;

    @Test
    public void testGetAllElderlyProfiles() throws Exception {
        // 确保数据库中有数据
        long count = elderlyProfileRepository.count();
        if (count == 0) {
            // 如果没有数据，创建一条测试数据
            ElderlyProfile profile = new ElderlyProfile();
            profile.setName("测试老人");
            profile.setGender("男");
            profile.setBirthDate(LocalDate.of(1950, 1, 1));
            profile.setIdCardNumber("110101195001011234");
            profile.setPhone("13900001234");
            profile.setPensionType("居家养老");
            elderlyProfileRepository.save(profile);
        }

        // 测试分页查询
        mockMvc.perform(get("/api/elderly-profiles")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    public void testCreateAndGetElderlyProfile() throws Exception {
        // 创建老人档案
        ElderlyProfileDTO profileDTO = new ElderlyProfileDTO();
        profileDTO.setName("测试创建老人");
        profileDTO.setGender("女");
        profileDTO.setBirthDate(LocalDate.of(1945, 5, 5));
        profileDTO.setIdCardNumber("110101194505051234");
        profileDTO.setPhone("13800001234");
        profileDTO.setAddressDetail("测试地址");
        profileDTO.setCommunity("测试社区");
        profileDTO.setPensionType("社区养老");
        profileDTO.setAbilityAssessment("能力完好");

        MvcResult result = mockMvc.perform(post("/api/elderly-profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试创建老人"))
                .andReturn();

        // 从返回结果中获取ID
        ElderlyProfileDTO createdProfile = objectMapper.readValue(
                result.getResponse().getContentAsString(), ElderlyProfileDTO.class);
        Long profileId = createdProfile.getId();

        // 获取创建的老人档案
        mockMvc.perform(get("/api/elderly-profiles/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId))
                .andExpect(jsonPath("$.name").value("测试创建老人"));
    }

    @Test
    public void testUpdateElderlyProfile() throws Exception {
        // 先创建一个老人档案
        ElderlyProfile profile = new ElderlyProfile();
        profile.setName("待更新老人");
        profile.setGender("男");
        profile.setBirthDate(LocalDate.of(1950, 1, 1));
        profile.setIdCardNumber("110101195001015678");
        profile.setPhone("13900005678");
        profile.setPensionType("居家养老");
        ElderlyProfile savedProfile = elderlyProfileRepository.save(profile);

        // 更新老人档案
        ElderlyProfileDTO updateDTO = new ElderlyProfileDTO();
        updateDTO.setId(savedProfile.getId());
        updateDTO.setName("已更新老人");
        updateDTO.setGender("男");
        updateDTO.setBirthDate(LocalDate.of(1950, 1, 1));
        updateDTO.setIdCardNumber("110101195001015678");
        updateDTO.setPhone("13900005678");
        updateDTO.setPensionType("机构养老");
        updateDTO.setAbilityAssessment("轻度失能");

        mockMvc.perform(put("/api/elderly-profiles/{id}", savedProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("已更新老人"))
                .andExpect(jsonPath("$.pensionType").value("机构养老"))
                .andExpect(jsonPath("$.abilityAssessment").value("轻度失能"));
    }

    @Test
    public void testAddAndGetFamilyMembers() throws Exception {
        // 先创建一个老人档案
        ElderlyProfile profile = new ElderlyProfile();
        profile.setName("家属测试老人");
        profile.setGender("女");
        profile.setBirthDate(LocalDate.of(1940, 6, 6));
        profile.setIdCardNumber("110101194006061234");
        profile.setPhone("13900006666");
        profile.setPensionType("居家养老");
        ElderlyProfile savedProfile = elderlyProfileRepository.save(profile);

        // 添加家属
        ElderlyFamilyMemberDTO familyMemberDTO = new ElderlyFamilyMemberDTO();
        familyMemberDTO.setName("测试家属");
        familyMemberDTO.setRelationship("子");
        familyMemberDTO.setPhone("13800008888");

        mockMvc.perform(post("/api/elderly-profiles/{elderlyId}/family-members", savedProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(familyMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试家属"))
                .andExpect(jsonPath("$.elderlyId").value(savedProfile.getId()));

        // 获取家属列表
        MvcResult result = mockMvc.perform(get("/api/elderly-profiles/{elderlyId}/family-members", savedProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        List<?> familyMembers = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ElderlyFamilyMemberDTO.class));

        assertNotNull(familyMembers);
        assertEquals(1, familyMembers.size());
    }

    @Test
    public void testDeleteElderlyProfile() throws Exception {
        // 先创建一个老人档案
        ElderlyProfile profile = new ElderlyProfile();
        profile.setName("待删除老人");
        profile.setGender("男");
        profile.setBirthDate(LocalDate.of(1955, 5, 5));
        profile.setIdCardNumber("110101195505051234");
        profile.setPhone("13900007777");
        profile.setPensionType("居家养老");
        ElderlyProfile savedProfile = elderlyProfileRepository.save(profile);

        // 删除老人档案
        mockMvc.perform(delete("/api/elderly-profiles/{id}", savedProfile.getId()))
                .andExpect(status().isNoContent());

        // 验证已删除
        mockMvc.perform(get("/api/elderly-profiles/{id}", savedProfile.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStatistics() throws Exception {
        // 确保数据库中有不同类型的老人数据
        if (elderlyProfileRepository.count() < 3) {
            // 创建不同养老类型和能力评估的老人
            ElderlyProfile profile1 = new ElderlyProfile();
            profile1.setName("统计测试老人1");
            profile1.setGender("男");
            profile1.setPensionType("居家养老");
            profile1.setAbilityAssessment("能力完好");
            elderlyProfileRepository.save(profile1);

            ElderlyProfile profile2 = new ElderlyProfile();
            profile2.setName("统计测试老人2");
            profile2.setGender("女");
            profile2.setPensionType("社区养老");
            profile2.setAbilityAssessment("轻度失能");
            elderlyProfileRepository.save(profile2);

            ElderlyProfile profile3 = new ElderlyProfile();
            profile3.setName("统计测试老人3");
            profile3.setGender("男");
            profile3.setPensionType("机构养老");
            profile3.setAbilityAssessment("中度失能");
            elderlyProfileRepository.save(profile3);
        }

        // 测试养老类型统计
        mockMvc.perform(get("/api/elderly-profiles/pension-type-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        // 测试能力评估统计
        mockMvc.perform(get("/api/elderly-profiles/ability-assessment-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
} 