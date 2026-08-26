package com.yj.inkpic.model.vo;

import cn.hutool.json.JSONUtil;
import com.yj.inkpic.model.entity.Picture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片封装类
 * @author OuYJ
 */
@Data
@ApiModel("图片封装类")
public class PictureVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty("图片id")
    private Long id;

    /**
     * 图片 url
     */
    @ApiModelProperty("图片url")
    private String url;

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
     * 标签
     */
    @ApiModelProperty("图片标签")
    private List<String> tags;

    /**
     * 分类
     */
    @ApiModelProperty("图片分类")
    private String category;

    /**
     * 文件体积
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
     * 用户 id
     */
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty("编辑时间")
    private LocalDateTime editTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建用户信息
     */
    @ApiModelProperty("创建用户信息")
    private UserVO user;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     */
    public static Picture voToObj(PictureVO pictureVO) {
        if (pictureVO == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO, picture);
        // 类型不同，需要转换  
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * 对象转封装类
     */
    public static PictureVO objToVo(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        // 类型不同，需要转换  
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }
}

