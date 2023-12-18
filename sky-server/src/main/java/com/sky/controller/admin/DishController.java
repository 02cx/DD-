package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
@RestController
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    // 清除缓存的方法
    public void cleanCache(String pattern){
        Set set = redisTemplate.keys(pattern);
        redisTemplate.delete(set);
    }

    /**
     * 添加菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("添加菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){

        dishService.saveDishWithFlavor(dishDTO);
        // 清理缓存
        String key = "dish:" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     *  菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @ApiOperation("删除菜品")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.delete(ids);

        //将所有的菜品缓存数据清理掉，所有以dish:开头的key
        cleanCache("dish:*");

        return Result.success();
    }

    /**
     *  根据id查询菜品信息（修改回显）
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVo = dishService.getById(id);
        return Result.success(dishVo);
    }


    /**
     *  修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish:开头的key
        cleanCache("dish:*");
        return Result.success();
    }

    /**
     *  根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        List<Dish> list = dishService.getByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish:*");

        return Result.success();
    }

}
