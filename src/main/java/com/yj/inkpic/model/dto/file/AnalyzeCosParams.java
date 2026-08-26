package com.yj.inkpic.model.dto.file;

import com.qcloud.cos.model.PutObjectResult;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分析COS参数
 * @author OuYJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "AnalyzeCosParams", description = "分析COS参数")
public class AnalyzeCosParams {
    private PutObjectResult putObjectResult;
    private String imageName;
    private String imagePath;
}