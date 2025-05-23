#!/bin/bash

# 定义API基础URL
BASE_URL="http://localhost:8080/api"

# 输出分隔线
function print_separator() {
  echo "============================================================"
}

# 1. 查询老人档案列表 (分页)
print_separator
echo "1. 查询老人档案列表 (分页)"
curl -s -X GET "$BASE_URL/elderly-profiles?page=0&size=5" | json_pp
echo ""

# 2. 按关键字搜索老人档案
print_separator
echo "2. 按关键字搜索老人档案"
curl -s -X GET "$BASE_URL/elderly-profiles?keyword=李" | json_pp
echo ""

# 3. 多条件查询老人档案
print_separator
echo "3. 多条件查询老人档案"
curl -s -X GET "$BASE_URL/elderly-profiles?pensionType=居家养老&community=东湖社区" | json_pp
echo ""

# 4. 获取养老类型统计
print_separator
echo "4. 获取养老类型统计"
curl -s -X GET "$BASE_URL/elderly-profiles/pension-type-statistics" | json_pp
echo ""

# 5. 获取能力评估统计
print_separator
echo "5. 获取能力评估统计"
curl -s -X GET "$BASE_URL/elderly-profiles/ability-assessment-statistics" | json_pp
echo ""

# 6. 获取特定老人档案
print_separator
echo "6. 获取特定老人档案"
ELDERLY_ID=$(curl -s -X GET "$BASE_URL/elderly-profiles?page=0&size=1" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "获取老人ID: $ELDERLY_ID"
curl -s -X GET "$BASE_URL/elderly-profiles/$ELDERLY_ID" | json_pp
echo ""

# 7. 获取老人家属列表
print_separator
echo "7. 获取老人家属列表"
curl -s -X GET "$BASE_URL/elderly-profiles/$ELDERLY_ID/family-members" | json_pp
echo ""

# 8. 创建新老人档案
print_separator
echo "8. 创建新老人档案"
NEW_ELDERLY=$(cat <<EOF
{
  "name": "测试老人",
  "gender": "男",
  "birthDate": "1940-01-01",
  "idCardNumber": "110101194001011234",
  "phone": "13900001234",
  "addressDetail": "测试地址",
  "community": "测试社区",
  "pensionType": "居家养老",
  "abilityAssessment": "能力完好",
  "careLevel": "1级护理",
  "medicalHistory": "高血压",
  "allergyHistory": "无",
  "hobbies": "下棋,看书"
}
EOF
)
curl -s -X POST "$BASE_URL/elderly-profiles" \
  -H "Content-Type: application/json" \
  -d "$NEW_ELDERLY" | json_pp
echo ""

# 9. 为老人添加家属
print_separator
echo "9. 为老人添加家属"
NEW_FAMILY_MEMBER=$(cat <<EOF
{
  "name": "测试家属",
  "relationship": "子",
  "phone": "13800001111"
}
EOF
)
curl -s -X POST "$BASE_URL/elderly-profiles/$ELDERLY_ID/family-members" \
  -H "Content-Type: application/json" \
  -d "$NEW_FAMILY_MEMBER" | json_pp
echo ""

# 10. 批量查询老人档案
print_separator
echo "10. 批量查询老人ID列表"
ELDERLY_IDS=$(curl -s -X GET "$BASE_URL/elderly-profiles?page=0&size=10" | grep -o '"id":[0-9]*' | cut -d':' -f2 | tr '\n' ',' | sed 's/,$//')
echo "老人ID列表: [$ELDERLY_IDS]"
echo ""

print_separator
echo "API测试完成" 