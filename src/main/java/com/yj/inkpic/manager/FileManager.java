package com.yj.inkpic.manager;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.config.CosClientConfig;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.excption.ThrowUtils;
import com.yj.inkpic.model.dto.file.AnalyzeCosParams;
import com.yj.inkpic.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 文件管理器
 */
@Slf4j
@Service
public class FileManager {
    /**
     * 1M
     */
    private static final long ONE_M = 1024 * 1024L;

    private static final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;


    /**
     * 上传图片方法
     *
     * @param multipartFile    上传的图片文件
     * @param uploadPathPrefix 图片上传路径前缀
     * @return UploadPictureResult 上传结果对象
     */
    public UploadPictureResult uploadPictureResult(MultipartFile multipartFile, String uploadPathPrefix) {
        // 验证图片文件的有效性
        validPicture(multipartFile);
        // 图片上传地址
        String imagePath = generateImageUploadPath(multipartFile, uploadPathPrefix);
        // 获取上传后的文件名
        String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        String nameWithoutExt = imageName.substring(0, imageName.lastIndexOf("."));
        try {
            File uploadFile = File.createTempFile(imagePath, null);
            multipartFile.transferTo(uploadFile);
            return analyzeCosReturn(new AnalyzeCosParams(cosManager.putPictureObject(imagePath, uploadFile), nameWithoutExt, imagePath));
        } catch (Exception e) {
            log.error("FileManager#uploadPicture2 error {}", ExceptionUtil.getRootCauseMessage(e));
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传图片失败");
        } finally {
            try {
                FileUtil.del(imagePath);
            } catch (IORuntimeException e) {
                log.error("FileManager#uploadPicture2 del filePath {}, error {}", imagePath, ExceptionUtil.getRootCauseMessage(e));
            }
        }
    }

    /**
     * 分析COS返回结果并构建上传图片结果对象
     *
     * @param analyzeCosParams 分析COS参数对象，包含上传结果和图片信息
     * @return UploadPictureResult 上传图片结果对象，包含图片的各种属性信息
     */
    private UploadPictureResult analyzeCosReturn(AnalyzeCosParams analyzeCosParams) {
        // 从参数对象中获取图片信息
        ImageInfo imageInfo = analyzeCosParams.getPutObjectResult().getCiUploadResult().getOriginalInfo().getImageInfo();
        // 使用建造者模式构建并返回上传结果对象
        return UploadPictureResult.builder()
                .picFormat(imageInfo.getFormat())           // 设置图片格式
                .picHeight(imageInfo.getHeight())           // 设置图片高度
                .picWidth(imageInfo.getWidth())            // 设置图片宽度
                .picSize((long) imageInfo.getQuality())      // 设置图片大小
                .picScale(NumberUtil.round(imageInfo.getHeight() * 1.0 / imageInfo.getWidth(), 2).doubleValue())  // 计算并设置图片比例
                .name(analyzeCosParams.getImageName())    // 设置图片名称
                .url(String.format("%s%s", cosClientConfig.getHost(), analyzeCosParams.getImagePath()))  // 构建完整URL
                .build();
    }

    /**
     * 生成图片上传路径
     *
     * @param multipartFile    上传的文件对象
     * @param uploadPathPrefix 上传路径前缀
     * @return 返回完整的上传路径
     */
    private String generateImageUploadPath(MultipartFile multipartFile, String uploadPathPrefix) {
        // 获取原始文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 提取后缀名
        String suffix = FileUtil.getSuffix(originalFilename);
        // 生成新的文件名，格式为：当前日期_随机字符串.原始文件后缀
        String uploadPath = String.format("%s_%s.%s", LocalDate.now(), RandomUtil.randomString(16), suffix);
        // 组合完整路径：上传路径前缀/新的文件名
        return String.format("%s/%s", uploadPathPrefix, uploadPath);
    }

    /**
     * 校验文件
     *
     * @param multipartFile multipart 文件
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}

