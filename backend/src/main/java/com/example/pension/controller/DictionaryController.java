package com.example.pension.controller;

import com.example.pension.dto.DictionaryDTO;
import com.example.pension.service.DictionaryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dictionaries")
@CrossOrigin(origins = "*")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 获取字典列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageInfo<DictionaryDTO>> search(
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictCode,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageInfo<DictionaryDTO> result = dictionaryService.searchByConditions(dictType, dictCode, dictLabel, status, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据类型获取字典列表
     */
    @GetMapping("/type/{dictType}")
    public ResponseEntity<List<DictionaryDTO>> getByType(@PathVariable String dictType) {
        List<DictionaryDTO> result = dictionaryService.getByType(dictType);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个字典详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<DictionaryDTO> getById(@PathVariable Long id) {
        DictionaryDTO result = dictionaryService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新字典
     */
    @PostMapping
    public ResponseEntity<DictionaryDTO> create(@RequestBody DictionaryDTO dictionaryDTO) {
        try {
            DictionaryDTO result = dictionaryService.create(dictionaryDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建字典失败", e);
            throw new RuntimeException("创建字典失败: " + e.getMessage());
        }
    }

    /**
     * 更新字典信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<DictionaryDTO> update(
            @PathVariable Long id,
            @RequestBody DictionaryDTO dictionaryDTO) {
        try {
            DictionaryDTO result = dictionaryService.update(id, dictionaryDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新字典失败", e);
            throw new RuntimeException("更新字典失败: " + e.getMessage());
        }
    }

    /**
     * 删除字典
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            dictionaryService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除字典失败", e);
            throw new RuntimeException("删除字典失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除字典
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        try {
            dictionaryService.batchDelete(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("批量删除字典失败", e);
            throw new RuntimeException("批量删除字典失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有字典
     */
    @GetMapping("/all")
    public ResponseEntity<List<DictionaryDTO>> getAll() {
        List<DictionaryDTO> result = dictionaryService.getAll();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据类型统计数量
     */
    @GetMapping("/count/{dictType}")
    public ResponseEntity<Long> countByType(@PathVariable String dictType) {
        Long result = dictionaryService.countByType(dictType);
        return ResponseEntity.ok(result);
    }
} 