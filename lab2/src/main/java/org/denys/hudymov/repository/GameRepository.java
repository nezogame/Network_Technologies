package org.denys.hudymov.repository;

import jakarta.transaction.Transactional;
import org.denys.hudymov.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Transactional
    Game findTopByOrderByGameIdDesc();
}
