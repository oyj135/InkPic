package com.yj.inkpic.controller;

import com.yj.inkpic.common.BaseResponse;
import com.yj.inkpic.common.ResultUtils;
import com.yj.inkpic.model.vo.LoginUserVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
