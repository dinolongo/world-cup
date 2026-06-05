package com.worldcup2026.repository;

import com.worldcup2026.entity.GroupStanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupStandingRepository extends JpaRepository<GroupStanding, Long> {

    List<GroupStanding> findByGroupNameOrderByPositionAsc(String groupName);

    List<GroupStanding> findByTeamId(Long teamId);

    Optional<GroupStanding> findByGroupNameAndTeamId(String groupName, Long teamId);

    void deleteByGroupName(String groupName);
}
