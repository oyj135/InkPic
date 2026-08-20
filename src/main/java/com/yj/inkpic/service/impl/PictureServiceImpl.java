package com.yj.inkpic.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.excption.ThrowUtils;
import com.yj.inkpic.manager.FileManager;
import com.yj.inkpic.model.dto.file.AnalyzeCosParams;
import com.yj.inkpic.model.dto.file.UploadPictureResult;
import com.yj.inkpic.model.dto.picture.PictureQueryRequest;
import com.yj.inkpic.model.dto.picture.PictureUploadRequest;
import com.yj.inkpic.model.dto.user.UserJwtDTO;
import com.yj.inkpic.model.dto.user.UserQueryRequest;
import com.yj.inkpic.model.entity.Picture;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.PictureVO;
import com.yj.inkpic.service.PictureService;
import com.yj.inkpic.mapper.PictureMapper;
import com.yj.inkpic.service.UserService;
import com.yj.inkpic.utils.BaseContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author OuYJ
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2026-08-20 12:56:31
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest) {
        UserJwtDTO currentUser = BaseContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        // 判断是新增还是删除
        Long picId = null;
        if (picId != null) {
            picId = pictureUploadRequest.getId();
        }
        // 如果是更新，判断当前图片是否已经存在
        if (picId != null) {
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, picId)
                    .exists();
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }
        // 使用当前日期时间为文件存储目录
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filePathPrefix = "/inkPic/" + format;
        UploadPictureResult uploadPictureResult = fileManager.uploadPictureResult(multipartFile, filePathPrefix);

        // 构造要入库的图片信息
        Long userId = currentUser.getId();
        Picture picture = savePictureInfo(uploadPictureResult, userId);

        // 操作数据库
        // 如果 picId 不为空，表示更新，否则是新增
        if (picId != null) {
            picture.setId(picId);
            picture.setEditTime(LocalDateTime.now());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败，数据库操作失败");
        return PictureVO.objToVo(picture);
    }

    private Picture savePictureInfo(UploadPictureResult uploadPictureResult, Long userId) {
        // 使用建造者模式构建并返回上传结果对象
        return Picture.builder()
                .url(uploadPictureResult.getUrl())
                .name(uploadPictureResult.getName())
                .picSize(uploadPictureResult.getPicSize())
                .picWidth(uploadPictureResult.getPicWidth())
                .picHeight(uploadPictureResult.getPicHeight())
                .picScale(uploadPictureResult.getPicScale())
                .picFormat(uploadPictureResult.getPicFormat())
                .userId(userId)
                .build();
    }


    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest 查询条件请求
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        if (pictureQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();


        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(name), "userProfile", name);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return queryWrapper;
    }
}




