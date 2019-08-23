package cn.bite.travel.service;

import cn.bite.travel.domain.Category;

import java.util.List;

/**
 * 旅游分类的业务接口层
 */
public interface CategoryService {
    List<Category> findAll();
}
