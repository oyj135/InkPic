package com.yj.inkpic.model.dto.file;

import com.qcloud.cos.model.PutObjectResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 不用成员变量因为多线程时会出问题
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeCosParams {
    private PutObjectResult putObjectResult;
    private String imageName;
    private String imagePath;
}