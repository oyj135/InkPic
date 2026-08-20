package com.yj.inkpic.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑请求
 */
@Data
@ApiModel("图片编辑请求")
public class PictureEditRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
