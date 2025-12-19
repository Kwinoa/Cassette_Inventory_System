package org.cassettes.cassetteinventorysystem.repository;

import java.util.List;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CassetteRepository extends JpaRepository<Cassette, Integer> {
	// Finds all cassettes where cassette.user.id = userId
	List<Cassette> findByUser_Id(Long userId);
	
	Cassette findByIdAndUser_Id(Long id, Long userId);
	
	boolean existsByIdAndUser_Id(Long id, Long userId);
	
	@Modifying
	@Transactional
	void deleteByIdAndUser_Id(Long id, Long userId);
}
