package org.denys.hudymov.service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.denys.hudymov.entity.Game;
import org.denys.hudymov.repository.GameRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GameService {
    public final GameRepository gameRepository;

    public synchronized Optional<Game> findLastGame(){
        return Optional.ofNullable(gameRepository.findTopByOrderByGameIdDesc());
    }

    @Transactional
    public synchronized long saveGame(Game entity){
        gameRepository.save(entity);
        return entity.getGameId();
    }

    @Transactional
    public synchronized void updateGame(Game entity){
        gameRepository.save(entity);
    }
}
