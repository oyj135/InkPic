package com.yj.inkpic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yj.inkpic.model.dto.picture.PictureQueryRequest;
import com.yj.inkpic.model.dto.picture.PictureUploadRequest;
import com.yj.inkpic.model.dto.user.UserQueryRequest;
import com.yj.inkpic.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author OuYJ
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2026-08-20 12:56:31
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
}
