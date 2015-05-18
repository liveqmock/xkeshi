package com.xkeshi.dao;

import com.xkeshi.pojo.po.Terminal;
import org.apache.ibatis.annotations.Param;

/**
 * Created by david-y on 2015/2/4.
 */
public interface TerminalDAO {

    Terminal getDeviceValidInfo(@Param("deviceNumber") String deviceNumber, @Param("mid") String mid, @Param("operatorId") String operatorId);

}
