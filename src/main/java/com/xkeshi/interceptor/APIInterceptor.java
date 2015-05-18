package com.xkeshi.interceptor;

import com.alibaba.fastjson.JSON;
import com.xkeshi.pojo.vo.DeviceValidVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.service.DeviceService;
import com.xkeshi.utils.DateUtils;
import com.xkeshi.utils.EncryptionUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class APIInterceptor implements HandlerInterceptor {

    @Autowired
    DeviceService deviceService;



    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        Map<String, String[]> requestParamMap = httpServletRequest.getParameterMap();

        // 对参数名进行字典排序
        String[] keyArray = requestParamMap.keySet().toArray(new String[0]);
        ArrayUtils.add(keyArray,"secret");

        Map<String, String> paramMap = new HashMap<>();

        for (String key : keyArray) {
            paramMap.put(key, requestParamMap.get(key)[0]);
        }


        //验证时间戳
        String timestamp =  paramMap.get("timestamp");
        if (timestamp == null) {
            redirect(httpServletResponse, "5001", "时间戳为空");
            return false;
        } else {
            try {
                Date timestampDate = new Date(Long.valueOf(timestamp)*1000);
                //比较当前时间是否是和当前相差10分钟
                int betweenMin = DateUtils.getMinutesBetween(new Date(), timestampDate);
                if (Math.abs(betweenMin) > 10) {
                    redirect(httpServletResponse, "5002", "时间戳范围不正确");
                    return false;
                }
            } catch (Exception e) {
                redirect(httpServletResponse, "5003", "时间戳错误");
                return false;
            }
        }


        //根据系统参数获取设备信息
        if (!ArrayUtils.contains(keyArray,"deviceNumber")){
            redirect(httpServletResponse, "5004", "缺少deviceNumber参数");
            return false;
        } else if (!ArrayUtils.contains(keyArray,"mid")){
            redirect(httpServletResponse, "5005", "缺少mid参数");
            return false;
        } else if (!ArrayUtils.contains(keyArray,"operatorId")){
            redirect(httpServletResponse, "5006", "缺少operatorId参数");
            return false;
        }

        String deviceNumber = paramMap.get("deviceNumber");
        String mid = paramMap.get("mid");
        String operatorId = paramMap.get("operatorId");

        //查询是否存在该设备
        DeviceValidVO deviceValidVO = deviceService.getDeviceValidInfo(deviceNumber,mid,operatorId);
        if (deviceValidVO == null){
            redirect(httpServletResponse, "5007", "系统参数出错");
            return false;
        }


        //获取签名
        if (ArrayUtils.contains(keyArray, "sign")) {
            String signParam = paramMap.get("sign");
            paramMap.remove("sign");

            //参数中加入设备secret
            paramMap.put("secret", deviceValidVO.getDeviceSecret());

            keyArray = ArrayUtils.add(keyArray,"secret");

            Arrays.sort(keyArray);
            String str = "";
            for (String key : keyArray) {
                str = StringUtils.join(str, StringUtils.isNotBlank(str) ? "&" : null, key, "=", paramMap.get(key));
            }

            System.out.println("==========" + str);
            String sign = EncryptionUtil.md5(str);
            System.out.println("==========" + sign);
            System.out.println("==========" + signParam);

            if (!StringUtils.equalsIgnoreCase(sign, signParam)) {
                redirect(httpServletResponse, "5008", "签名验证不通过");
                return false;
            }

        }else{
            redirect(httpServletResponse, "5004", "缺少签名参数");
            return false;
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


    private void redirect(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();

        Result result = new Result(message,code);
        String data = JSON.toJSONString(result);
        pw.write(data);
    }
}
