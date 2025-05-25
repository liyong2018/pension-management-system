package com.example.pension.service.impl;

import com.example.pension.dao.DictionaryDao;
import com.example.pension.dto.DictionaryDTO;
import com.example.pension.model.Dictionary;
import com.example.pension.service.DictionaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;

    @Override
    public DictionaryDTO create(DictionaryDTO dictionaryDTO) {
        // 验证字典编码是否重复
        Dictionary existingDict = dictionaryDao.findByTypeAndCode(dictionaryDTO.getDictType(), dictionaryDTO.getDictCode());
        if (existingDict != null) {
            throw new RuntimeException("字典编码已存在：" + dictionaryDTO.getDictCode());
        }

        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(dictionaryDTO, dictionary);
        
        // 设置默认值
        if (dictionary.getSortOrder() == null) {
            dictionary.setSortOrder(0);
        }
        if (dictionary.getStatus() == null) {
            dictionary.setStatus("ACTIVE");
        }

        dictionaryDao.insert(dictionary);
        return getById(dictionary.getId());
    }

    @Override
    public DictionaryDTO getById(Long id) {
        Dictionary dictionary = dictionaryDao.findById(id);
        if (dictionary == null) {
            throw new RuntimeException("字典不存在，ID：" + id);
        }
        
        DictionaryDTO dto = new DictionaryDTO();
        BeanUtils.copyProperties(dictionary, dto);
        return dto;
    }

    @Override
    public List<DictionaryDTO> getByType(String dictType) {
        List<Dictionary> dictionaries = dictionaryDao.findByType(dictType);
        return dictionaries.stream()
                .map(dict -> {
                    DictionaryDTO dto = new DictionaryDTO();
                    BeanUtils.copyProperties(dict, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageInfo<DictionaryDTO> searchByConditions(String dictType, String dictCode, String dictLabel, String status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "sort_order asc, create_time desc");
        List<Dictionary> dictionaries = dictionaryDao.findWithConditions(dictType, dictCode, dictLabel, status);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionaries);
        
        List<DictionaryDTO> dictionaryDTOs = dictionaries.stream()
                .map(dict -> {
                    DictionaryDTO dto = new DictionaryDTO();
                    BeanUtils.copyProperties(dict, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        PageInfo<DictionaryDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(dictionaryDTOs);
        
        return result;
    }

    @Override
    public DictionaryDTO update(Long id, DictionaryDTO dictionaryDTO) {
        Dictionary existingDict = dictionaryDao.findById(id);
        if (existingDict == null) {
            throw new RuntimeException("字典不存在，ID：" + id);
        }

        // 验证字典编码是否重复（排除当前记录）
        if (dictionaryDTO.getDictCode() != null && 
            !dictionaryDTO.getDictCode().equals(existingDict.getDictCode())) {
            Dictionary duplicateDict = dictionaryDao.findByTypeAndCode(dictionaryDTO.getDictType(), dictionaryDTO.getDictCode());
            if (duplicateDict != null) {
                throw new RuntimeException("字典编码已存在：" + dictionaryDTO.getDictCode());
            }
        }

        BeanUtils.copyProperties(dictionaryDTO, existingDict, "id", "createTime");
        dictionaryDao.update(existingDict);
        
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        Dictionary dictionary = dictionaryDao.findById(id);
        if (dictionary == null) {
            throw new RuntimeException("字典不存在，ID：" + id);
        }
        
        dictionaryDao.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除列表不能为空");
        }
        
        dictionaryDao.deleteBatchByIds(ids);
    }

    @Override
    public List<DictionaryDTO> getAll() {
        List<Dictionary> dictionaries = dictionaryDao.findAll();
        return dictionaries.stream()
                .map(dict -> {
                    DictionaryDTO dto = new DictionaryDTO();
                    BeanUtils.copyProperties(dict, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long countByType(String dictType) {
        return dictionaryDao.countByType(dictType);
    }

    @Override
    public DictionaryDTO getByTypeAndCode(String dictType, String dictCode) {
        Dictionary dictionary = dictionaryDao.findByTypeAndCode(dictType, dictCode);
        if (dictionary == null) {
            return null;
        }
        
        DictionaryDTO dto = new DictionaryDTO();
        BeanUtils.copyProperties(dictionary, dto);
        return dto;
    }
} 