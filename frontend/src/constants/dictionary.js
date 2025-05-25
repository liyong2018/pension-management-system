// 字典类型常量
export const DICT_TYPES = {
  COMMUNITY: 'community',
  PENSION_TYPE: 'pensionType'
}

// 字典类型名称映射
export const DICT_TYPE_NAMES = {
  [DICT_TYPES.COMMUNITY]: '所属社区',
  [DICT_TYPES.PENSION_TYPE]: '养老类型'
}

// 字典状态常量
export const DICT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE'
}

// 字典状态名称映射
export const DICT_STATUS_NAMES = {
  [DICT_STATUS.ACTIVE]: '启用',
  [DICT_STATUS.INACTIVE]: '禁用'
}

// 字典状态选项
export const DICT_STATUS_OPTIONS = [
  { label: '启用', value: DICT_STATUS.ACTIVE },
  { label: '禁用', value: DICT_STATUS.INACTIVE }
] 