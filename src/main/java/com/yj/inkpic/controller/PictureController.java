package com.yj.inkpic.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.inkpic.annotation.AuthCheck;
import com.yj.inkpic.annotation.LogOperation;
import com.yj.inkpic.common.BaseResponse;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.common.ResultUtils;
import com.yj.inkpic.constant.LogConstant;
import com.yj.inkpic.constant.UserConstant;
import com.yj.inkpic.excption.ThrowUtils;
import com.yj.inkpic.model.dto.picture.PictureQueryRequest;
import com.yj.inkpic.model.dto.picture.PictureUploadRequest;
import com.yj.inkpic.model.entity.Picture;
import com.yj.inkpic.model.vo.PictureVO;
import com.yj.inkpic.service.PictureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/20
 * <p>
 * 图片模块接口
 */

@RestController
@RequestMapping("/picture")
@Api(tags = "图片模块接口")
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 图片上传
     *
     * @param multipartFile 上传的文件
     * @return 文件访问地址
     */
    @PostMapping("/upload")
    @ApiOperation("图片上传")
    @LogOperation(module = LogConstant.PIC_MANAGER, type = LogConstant.PIC_UPLOAD)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> UploadFile(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest) {
        ThrowUtils.throwIf(multipartFile.isEmpty(), ErrorCode.PARAMS_ERROR, "请求参数错误");
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest);
        return ResultUtils.success(pictureVO);
    }

    @PostMapping("/list/page")
    public BaseResponse<PictureVO> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数错误");
        // 获取分页参数
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        Page<Object> page = new Page<>(current, pageSize);
        // 获取查询参数
        QueryWrapper<Picture> queryWrapper = pictureService.getQueryWrapper(pictureQueryRequest);
        // todo 调用service层查询方法
//        pictureService.listPictureByPage(page, keyword);
        return ResultUtils.success(null);
    }
}
