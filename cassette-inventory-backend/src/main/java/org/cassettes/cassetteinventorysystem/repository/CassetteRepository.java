package org.cassettes.cassetteinventorysystem.repository;

import org.cassettes.cassetteinventorysystem.entity.Cassette;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CassetteRepository extends JpaRepository<Cassette, Integer> {
	
}
