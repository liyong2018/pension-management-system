package com.example.pension.dao;

import com.example.pension.model.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictionaryDao {

    void insert(Dictionary dictionary);

    void update(Dictionary dictionary);

    void deleteById(@Param("id") Long id);

    void deleteBatchByIds(@Param("ids") List<Long> ids);

    Dictionary findById(@Param("id") Long id);

    List<Dictionary> findByType(@Param("dictType") String dictType);

    List<Dictionary> findWithConditions(@Param("dictType") String dictType,
                                       @Param("dictCode") String dictCode,
                                       @Param("dictLabel") String dictLabel,
                                       @Param("status") String status);

    List<Dictionary> findAll();

    Long countByType(@Param("dictType") String dictType);

    Dictionary findByTypeAndCode(@Param("dictType") String dictType, @Param("dictCode") String dictCode);
} 