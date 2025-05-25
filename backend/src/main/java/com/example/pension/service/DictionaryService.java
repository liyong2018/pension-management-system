package com.example.pension.service;

import com.example.pension.dto.DictionaryDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DictionaryService {

    DictionaryDTO create(DictionaryDTO dictionaryDTO);

    DictionaryDTO getById(Long id);

    List<DictionaryDTO> getByType(String dictType);

    PageInfo<DictionaryDTO> searchByConditions(String dictType, String dictCode, String dictLabel, String status, int pageNum, int pageSize);

    DictionaryDTO update(Long id, DictionaryDTO dictionaryDTO);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    List<DictionaryDTO> getAll();

    Long countByType(String dictType);

    DictionaryDTO getByTypeAndCode(String dictType, String dictCode);
} 