package com.xkeshi.service;

import com.xkeshi.dao.TerminalDAO;
import com.xkeshi.pojo.po.Terminal;
import com.xkeshi.pojo.vo.DeviceValidVO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by david-y on 2015/2/4.
 */
@Service
public class DeviceService {
    @Autowired(required = false)
    private TerminalDAO terminalDAO;

    @Autowired
    private Mapper dozerMapper;


    /**
     * 获取设备验证信息
     *
     * Gets device valid info.
     *
     * @param deviceNumber the device number
     * @param mid the mid
     * @param operatorId the operator id
     * @return the device valid info
     */
    public DeviceValidVO getDeviceValidInfo(String deviceNumber, String mid, String operatorId) {
        Terminal terminalPO = terminalDAO.getDeviceValidInfo(deviceNumber, mid, operatorId);
        if (terminalPO == null) {
            return null;
        }
        return dozerMapper.map(terminalPO,DeviceValidVO.class);
    }
}
