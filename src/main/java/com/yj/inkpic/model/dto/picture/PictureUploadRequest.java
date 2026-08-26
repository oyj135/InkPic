package com.yj.inkpic.model.dto.picture;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片上传请求
 * @author OuYJ
 */
@Data
@ApiModel(value = "PictureUploadRequest", description = "图片上传请求")
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}

