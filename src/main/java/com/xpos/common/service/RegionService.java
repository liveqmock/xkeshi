package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.Region;
import com.xpos.common.entity.example.RegionExample;

public interface RegionService {
    List<Region>  findRegionList(RegionExample  regionExample);
    Region    findRegion(RegionExample  regionExample);
}
