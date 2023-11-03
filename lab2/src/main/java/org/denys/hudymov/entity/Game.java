package org.denys.hudymov.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "games")
public class Game {
        @Id
        @SequenceGenerator(
                name = "game_sequence",
                sequenceName = "game_sequence",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "author_sequence"
        )
        @Column(name = "game_id")
        private Long gameId;
        private Boolean first1Win;
        private Boolean first2Win;
        private Boolean second1Win;
        private Boolean second2Win;
        private Boolean third1Win;
        private Boolean third2Win;
}
