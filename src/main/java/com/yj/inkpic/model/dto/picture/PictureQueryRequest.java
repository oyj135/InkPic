package com.yj.inkpic.model.dto.picture;

import com.yj.inkpic.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 图片查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("图片查询请求")
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty("图片id")
    private Long id;

    /**
     * 图片名称
     */
    @ApiModelProperty("图片名称")
    private String name;

    /**
     * 图片简介
     */
    @ApiModelProperty("图片简介")
    private String introduction;

    /**
     * 图片分类
     */
    @ApiModelProperty("图片分类")
    private String category;

    /**
     * 图片标签
     */
    @ApiModelProperty("图片标签")
    private List<String> tags;

    /**
     * 图片体积
     */
    @ApiModelProperty("图片体积")
    private Long picSize;

    /**
     * 图片宽度
     */
    @ApiModelProperty("图片宽度")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @ApiModelProperty("图片高度")
    private Integer picHeight;

    /**
     * 图片比例
     */
    @ApiModelProperty("图片比例")
    private Double picScale;

    /**
     * 图片格式
     */
    @ApiModelProperty("图片格式")
    private String picFormat;

    /**
     * 搜索词（同时搜名称、简介等）
     */
    @ApiModelProperty("搜索词（同时搜名称、简介等）")
    private String searchText;

    /**
     * 用户 id
     */
    @ApiModelProperty("用户 id")
    private Long userId;

    private static final long serialVersionUID = 1L;
}

