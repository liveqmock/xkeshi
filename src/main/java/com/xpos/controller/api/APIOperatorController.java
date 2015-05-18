package com.xpos.controller.api;

import com.xkeshi.pojo.vo.offline.OffLineOperatorShiftVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.service.OperatorService;
import com.xkeshi.service.OperatorShiftService;
import com.xpos.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yc
 * @date 2015/4/10
 * @description
 */
@Controller
@RequestMapping("/api")
public class APIOperatorController extends BaseController {

    @Resource(name = "newOperatorService")
    private OperatorService newOperatorService;

    @Resource(name = "newOperatorShiftService")
    private OperatorShiftService operatorShiftService;


    /**
     * 获取商家操作员列表
     * @param systemParam
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "operator/list", method = RequestMethod.GET)
    public Result loadOperators(@ModelAttribute SystemParam systemParam){
        return newOperatorService.loadOperatorsByShopIdForAPI(systemParam);
    }

    /**
     * 上传离线交接班信息
     * @param systemParam
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "offline/shift/upload", method = RequestMethod.POST)
    public Result uploadOffLineOperatorShiftInfo(@ModelAttribute SystemParam systemParam,
                                                 @RequestBody Map<String,List<OffLineOperatorShiftVO>> map) {
        List<OffLineOperatorShiftVO> shiftVOs = map.get("shiftList");
        return operatorShiftService.uploadOffLineOperatorShiftInfo(systemParam, shiftVOs);
    }


}
