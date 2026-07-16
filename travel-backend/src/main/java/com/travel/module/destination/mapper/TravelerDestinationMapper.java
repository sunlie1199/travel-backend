package com.travel.module.destination.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.module.destination.entity.TravelerDestination;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TravelerDestinationMapper extends BaseMapper<TravelerDestination> {

    @Select("SELECT COUNT(*) FROM traveler_destinations WHERE traveler_id = #{travelerId}")
    int countByTravelerId(@Param("travelerId") String travelerId);

    @Select("SELECT d.name, d.region, d.latitude, d.longitude, "
            + "MAX(CASE WHEN td.traveler_id = 'male' THEN 1 ELSE 0 END) AS maleVisited, "
            + "MAX(CASE WHEN td.traveler_id = 'female' THEN 1 ELSE 0 END) AS femaleVisited "
            + "FROM destinations d "
            + "JOIN traveler_destinations td ON td.destination_id = d.id "
            + "GROUP BY d.name, d.region, d.latitude, d.longitude")
    List<Map<String, Object>> selectSharedFootprints();
}
