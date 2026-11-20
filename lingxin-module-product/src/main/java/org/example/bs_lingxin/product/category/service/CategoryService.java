package org.example.bs_lingxin.product.category.service;

import org.example.bs_lingxin.product.category.dto.CategoryPageQueryDTO;
import org.example.bs_lingxin.product.category.dto.CategorySaveDTO;
import org.example.bs_lingxin.product.category.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> selectPage(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> selectTree(CategoryPageQueryDTO categoryPageQueryDTO);

    Category selectId(Long id);

    void categoryadd(CategorySaveDTO categorySaveDTO);

    void categoryupdate(CategorySaveDTO categorySaveDTO);

    void categorydelect(Long id);
}
