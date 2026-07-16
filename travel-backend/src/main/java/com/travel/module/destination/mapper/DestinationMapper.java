package com.travel.module.destination.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.module.destination.entity.Destination;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DestinationMapper extends BaseMapper<Destination> {

    @Select("SELECT d.* FROM destinations d "
            + "INNER JOIN traveler_destinations td ON td.destination_id = d.id "
            + "WHERE td.traveler_id = #{travelerId}")
    List<Destination> selectByTravelerId(@Param("travelerId") String travelerId);

    @Select("SELECT COUNT(DISTINCT d.name) FROM destinations d "
            + "JOIN traveler_destinations td_m ON td_m.destination_id = d.id AND td_m.traveler_id = 'male' "
            + "JOIN traveler_destinations td_f ON td_f.destination_id = d.id AND td_f.traveler_id = 'female'")
    int countShared();

    @Select("SELECT COUNT(DISTINCT d.name) FROM destinations d "
            + "JOIN traveler_destinations td ON td.destination_id = d.id "
            + "WHERE d.status = #{status}")
    int countByStatus(@Param("status") String status);

    @Select("SELECT COUNT(DISTINCT d.region) FROM destinations d "
            + "JOIN traveler_destinations td ON td.destination_id = d.id")
    int countDistinctRegions();

    @Select("SELECT DISTINCT d.country FROM destinations d "
            + "JOIN traveler_destinations td ON td.destination_id = d.id")
    List<String> selectDistinctCountries();
}
