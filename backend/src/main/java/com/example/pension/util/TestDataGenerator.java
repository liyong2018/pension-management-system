package com.example.pension.util;

import com.example.pension.dao.ElderlyFamilyMemberDao;
import com.example.pension.dao.ElderlyProfileDao;
import com.example.pension.dao.OrganizationDao;
import com.example.pension.model.ElderlyFamilyMember;
import com.example.pension.model.ElderlyProfile;
import com.example.pension.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用于生成测试数据的工具类
 */
@Configuration
@RequiredArgsConstructor
public class TestDataGenerator {

    private final Random random = new Random();
    
    private final String[] genders = {"男", "女"};
    private final String[] communities = {"东湖社区", "西湖社区", "南湖社区", "北湖社区", "中心社区", "阳光社区", "和平社区", "幸福社区"};
    private final String[] pensionTypes = {"居家养老", "社区养老", "机构养老"};
    private final String[] abilityAssessments = {"能力完好", "轻度失能", "中度失能", "重度失能"};
    private final String[] relationships = {"子", "女", "儿媳", "女婿", "孙子", "孙女", "兄弟", "姐妹", "其他亲属"};
    private final String[] surnames = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴", "徐", "孙", "马", "朱", "胡", "林", "郭", "何", "高", "罗"};
    private final String[] firstNames = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军", "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞"};
    private final String[] orgTypes = {"居家养老单位", "社区养老单位（日照）", "机构养老单位（养老院）"};
    
    /**
     * 生成测试数据
     */
    @Bean
    @Profile("dev")
    @Transactional
    public CommandLineRunner generateTestData(
            OrganizationDao organizationDao,
            ElderlyProfileDao elderlyProfileDao,
            ElderlyFamilyMemberDao familyMemberDao) {
        
        return args -> {
            List<Organization> organizations = organizationDao.findAll();
            if (organizations.isEmpty()) {
                organizations = generateOrganizations(10);
                for (Organization org : organizations) {
                    organizationDao.insert(org);
                }
                System.out.println("已生成10个机构测试数据");
            } else {
                 System.out.println("机构数据已存在，跳过生成。");
            }
            
            organizations = organizationDao.findAll();
            
            List<ElderlyProfile> elderlyProfiles = generateElderlyProfiles(50, organizations);
            for (ElderlyProfile profile : elderlyProfiles) {
                elderlyProfileDao.insert(profile);
            }
            System.out.println("已生成50个老人档案测试数据");
            
            List<ElderlyProfile> savedElderlyProfiles = elderlyProfileDao.findWithConditions(null,null,null,null,0, Integer.MAX_VALUE);
            
            List<ElderlyFamilyMember> familyMembers = new ArrayList<>();
            if (!savedElderlyProfiles.isEmpty()) {
                 for (ElderlyProfile elderlyProfile : savedElderlyProfiles) {
                    familyMembers.addAll(generateFamilyMembers(elderlyProfile, random.nextInt(3) + 1));
                }
                for (ElderlyFamilyMember fm : familyMembers) {
                    familyMemberDao.insert(fm);
                }
                System.out.println("已生成" + familyMembers.size() + "个家属测试数据");
            } else {
                System.out.println("没有老人档案数据，跳过生成家属数据");
            }
        };
    }
    
    /**
     * 生成机构数据
     */
    private List<Organization> generateOrganizations(int count) {
        List<Organization> organizations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Organization organization = new Organization();
            organization.setName(getRandomOrgName());
            organization.setShortName(organization.getName().substring(0, Math.min(organization.getName().length(), 2)));
            organization.setType(orgTypes[random.nextInt(orgTypes.length)]);
            organization.setAddress(getRandomAddress());
            organization.setPhone("1" + (random.nextInt(9) + 1) + getRandomNumericString(9));
            organization.setEmail(getRandomString(5) + "@example.com");
            organization.setWebsite("http://www." + getRandomString(6) + ".com");
            organization.setEstablishmentDate(getRandomDate(2000, 2020));
            organization.setLicenseNumber("L" + getRandomNumericString(6));
            organization.setBedCount(random.nextInt(200) + 50);
            organization.setActualServiceCount(random.nextInt(organization.getBedCount()));
            organization.setDirectorName(getRandomPersonName());
            organization.setDirectorContact("1" + (random.nextInt(9) + 1) + getRandomNumericString(9));
            organization.setEmployeeCount(random.nextInt(50) + 10);
            organization.setProfessionalNurseCount(random.nextInt(organization.getEmployeeCount()));
            organizations.add(organization);
        }
        return organizations;
    }
    
    /**
     * 生成老人档案数据
     */
    private List<ElderlyProfile> generateElderlyProfiles(int count, List<Organization> organizations) {
        List<ElderlyProfile> elderlyProfiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ElderlyProfile profile = new ElderlyProfile();
            profile.setName(getRandomPersonName());
            profile.setGender(genders[random.nextInt(genders.length)]);
            profile.setBirthDate(getRandomDate(1930, 1960));
            profile.setIdCardNumber(getRandomIdCardNumber());
            profile.setPhone("1" + (random.nextInt(9) + 1) + getRandomNumericString(9));
            profile.setPhotoUrl("https://example.com/photos/" + getRandomString(10) + ".jpg");
            profile.setAddressDetail(getRandomAddress());
            profile.setCommunity(communities[random.nextInt(communities.length)]);
            profile.setPensionType(pensionTypes[random.nextInt(pensionTypes.length)]);
            
            if ("机构养老".equals(profile.getPensionType()) && organizations != null && !organizations.isEmpty()) {
                profile.setOrganization(organizations.get(random.nextInt(organizations.size())));
            }
            
            profile.setMedicalHistory(random.nextBoolean() ? "高血压,糖尿病" : "");
            profile.setAllergyHistory(random.nextBoolean() ? "对花粉过敏" : "");
            profile.setCurrentHealthStatus("一般");
            profile.setCareLevel(String.valueOf(random.nextInt(3) + 1) + "级护理");
            profile.setAbilityAssessment(abilityAssessments[random.nextInt(abilityAssessments.length)]);
            profile.setLivingHabits("作息规律" + (random.nextBoolean() ? "，喜欢清淡饮食" : ""));
            profile.setHobbies("下棋" + (random.nextBoolean() ? "，看书" : "") + (random.nextBoolean() ? "，散步" : ""));
            profile.setReligiousBelief(random.nextBoolean() ? "佛教" : "");
            profile.setRemarks("");
            
            elderlyProfiles.add(profile);
        }
        return elderlyProfiles;
    }
    
    /**
     * 生成家属数据
     */
    private List<ElderlyFamilyMember> generateFamilyMembers(ElderlyProfile elderlyProfile, int count) {
        List<ElderlyFamilyMember> familyMembers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ElderlyFamilyMember familyMember = new ElderlyFamilyMember();
            familyMember.setElderlyProfile(elderlyProfile);
            familyMember.setName(getRandomPersonName());
            familyMember.setRelationship(relationships[random.nextInt(relationships.length)]);
            familyMember.setPhone("1" + (random.nextInt(9) + 1) + getRandomNumericString(9));
            familyMembers.add(familyMember);
        }
        return familyMembers;
    }
    
    /**
     * 生成随机日期
     */
    private LocalDate getRandomDate(int startYear, int endYear) {
        int year = startYear + random.nextInt(endYear - startYear + 1);
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // 简化处理，避免月份天数问题
        return LocalDate.of(year, month, day);
    }
    
    /**
     * 生成随机人名
     */
    private String getRandomPersonName() {
        String surname = surnames[random.nextInt(surnames.length)];
        String firstName = firstNames[random.nextInt(firstNames.length)];
        return surname + firstName;
    }
    
    /**
     * 生成随机机构名称
     */
    private String getRandomOrgName() {
        String[] prefixes = {"阳光", "康乐", "幸福", "和谐", "祥和", "百年", "颐养", "乐康", "松柏", "仁爱"};
        String[] suffixes = {"老年公寓", "养老院", "敬老院", "老年服务中心", "养老中心", "康养中心", "老年之家", "颐养之家"};
        return prefixes[random.nextInt(prefixes.length)] + suffixes[random.nextInt(suffixes.length)];
    }
    
    /**
     * 生成随机地址
     */
    private String getRandomAddress() {
        String[] provinces = {"北京市", "上海市", "广东省", "江苏省", "浙江省", "山东省", "四川省", "河南省"};
        String[] cities = {"市辖区", "广州市", "深圳市", "南京市", "杭州市", "济南市", "成都市", "郑州市"};
        String[] districts = {"东城区", "西城区", "海淀区", "朝阳区", "天河区", "福田区", "玄武区", "江干区"};
        String[] streets = {"和平路", "建国路", "解放路", "人民路", "中山路", "复兴路", "建设路", "育才路"};
        
        String province = provinces[random.nextInt(provinces.length)];
        String city = cities[random.nextInt(cities.length)];
        String district = districts[random.nextInt(districts.length)];
        String street = streets[random.nextInt(streets.length)];
        String number = String.valueOf(random.nextInt(200) + 1) + "号";
        
        return province + city + district + street + number;
    }
    
    /**
     * 生成随机身份证号
     */
    private String getRandomIdCardNumber() {
        // 简化生成，不考虑校验位
        String[] areaCodes = {"110101", "310101", "440101", "320101", "330101", "370101", "510101", "410101"};
        String areaCode = areaCodes[random.nextInt(areaCodes.length)];
        String birthDate = String.format("%d%02d%02d", 
                1930 + random.nextInt(60), 
                random.nextInt(12) + 1, 
                random.nextInt(28) + 1);
        String sequence = String.format("%03d", random.nextInt(1000));
        String checkDigit = String.valueOf(random.nextInt(10));
        
        return areaCode + birthDate + sequence + checkDigit;
    }
    
    /**
     * 生成指定长度的随机数字字符串
     */
    private String getRandomNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机字母字符串
     */
    private String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }
} 