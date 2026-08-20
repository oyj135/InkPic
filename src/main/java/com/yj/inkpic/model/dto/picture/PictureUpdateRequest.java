package com.yj.inkpic.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片更新请求
 */
@Data
@ApiModel("图片更新请求")
public class PictureUpdateRequest implements Serializable {

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
     * 简介
     */
    @ApiModelProperty("图片简介")
    private String introduction;

    /**
     * 分类
     */
    @ApiModelProperty("图片分类")
    private String category;

    /**
     * 标签
     */
    @ApiModelProperty("图片标签")
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}

