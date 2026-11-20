package org.example.bs_lingxin.product.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.bs_lingxin.common.exception.BusinessException;
import org.example.bs_lingxin.product.category.CategoryMapper;
import org.example.bs_lingxin.product.category.dto.CategoryPageQueryDTO;
import org.example.bs_lingxin.product.category.dto.CategorySaveDTO;
import org.example.bs_lingxin.product.category.entity.Category;
import org.example.bs_lingxin.product.category.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }


    /**
     * 获取父类分类下的分类数据
     * */
    @Override
    public List<Category> selectTree(CategoryPageQueryDTO categoryPageQueryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryPageQueryDTO,category);
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery(Category.class)
                .eq(categoryPageQueryDTO.getParentId() != null,Category::getParentId,categoryPageQueryDTO.getParentId());
        List<Category> categories = categoryMapper.selectList(wrapper);
        if (categories.isEmpty()){
            throw new BusinessException("父类分类下无分类数据");
        }
        return categories;
    }

    /**
     * 分页查询分类列表
     * */
    @Override
    public List<Category> selectPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 限制最大跳转页码50页
        final long MAX_ALLOW_PAGE = 50;
        Long current = categoryPageQueryDTO.getCurrent();
        if (current > MAX_ALLOW_PAGE) {
            throw new BusinessException("查询页码不能超过" + MAX_ALLOW_PAGE + "页，请增加筛选条件缩小数据集");
        }
        Page<Category> page = new Page<>(categoryPageQueryDTO.getCurrent(),categoryPageQueryDTO.getSize());
        Category category = new Category();
        BeanUtils.copyProperties(categoryPageQueryDTO,category);
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery(Category.class)
                .eq(categoryPageQueryDTO.getParentId() != null,Category::getParentId,categoryPageQueryDTO.getParentId())
                .eq(categoryPageQueryDTO.getId() != null,Category::getId,categoryPageQueryDTO.getId())
                .eq(categoryPageQueryDTO.getStatus() != null,Category::getStatus,categoryPageQueryDTO.getStatus())
                .like(StringUtils.hasText(categoryPageQueryDTO.getCategoryName()),Category::getCategoryName,categoryPageQueryDTO.getCategoryName())
                .like(StringUtils.hasText(categoryPageQueryDTO.getCategoryCode()),Category::getCategoryCode,categoryPageQueryDTO.getCategoryCode());
        List<Category> categories = categoryMapper.selectPage(page,wrapper);
        if(categories.isEmpty()){
            throw new BusinessException("暂无数据");
        }
        return categories;
    }

    /**
     * id查询分类
     * */
    @Override
    public Category selectId(Long id) {
        Category categoryId = categoryMapper.selectById(id);
        if (categoryId == null){
            throw new BusinessException("该分类已经存在");
        }
        return categoryId;
    }

    /**
     * 新增分类
     * */
    @Override
    public void categoryadd(CategorySaveDTO categorySaveDTO) {
        selectcategory(categorySaveDTO.getId());
        Category category = new Category();
        BeanUtils.copyProperties(categorySaveDTO,category);
        if (categoryMapper.insert(category) < 1){
            throw new BusinessException("新增商品分类失败");
        }
    }

    /**
     * 修改分类
     * */
    @Override
    public void categoryupdate(CategorySaveDTO categorySaveDTO) {
        selectcategory(categorySaveDTO.getId());
        Category category = new Category();
        BeanUtils.copyProperties(categorySaveDTO,category);
        if (categoryMapper.updateById(category) == 0){
            throw new BusinessException("修改商品分类失败");
        }
    }

    /**
     * 逻辑删除分类
     * */
    @Override
    public void categorydelect(Long id) {
        selectcategory(id);
        if (categoryMapper.deleteById(id) == 0){
            throw new BusinessException("商家删除失败");
        }
    }

    /**
     * 查询分类存在
     * */
    public void selectcategory(Long id){
        Category categoryId = categoryMapper.selectById(id);
        if (categoryId == null) {
            throw new BusinessException("该分类已经存在");
        }
    }
}
