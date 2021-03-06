package io.serialized.samples.rockpaperscissors;

import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.feed.FeedClient;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.samples.rockpaperscissors.domain.GameState;
import io.serialized.samples.rockpaperscissors.domain.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

  private static final String GAME_AGGREGATE_TYPE = "game";

  private final Environment env;

  @Autowired
  public AppConfig(Environment env) {
    this.env = env;
  }

  @Bean
  public ProjectionClient projectionApiClient() {
    return ProjectionClient.projectionClient(getConfig()).build();
  }

  @Bean
  public AggregateClient<GameState> gameClient() {
    return AggregateClient.aggregateClient(GAME_AGGREGATE_TYPE, GameState.class, getConfig())
        .registerHandler(GameStarted.class, GameState::handleGameStarted)
        .registerHandler(PlayerWonRound.class, GameState::handlePlayerWonRound)
        .registerHandler(GameFinished.class, GameState::handleGameFinished)
        .registerHandler(PlayerAnswered.class, GameState::handlePlayerAnswered)
        .registerHandler(RoundStarted.class, GameState::handleRoundStarted)
        .registerHandler(RoundFinished.class, GameState::handleRoundFinished)
        .registerHandler(RoundTied.class, GameState::handleRoundTied)
        .build();
  }

  @Bean
  public FeedClient feedApiClient() {
    return FeedClient.feedClient(getConfig()).build();
  }

  private SerializedClientConfig getConfig() {
    return SerializedClientConfig.serializedConfig()
        .accessKey(env.getProperty("SERIALIZED_ACCESS_KEY"))
        .secretAccessKey(env.getProperty("SERIALIZED_SECRET_ACCESS_KEY"))
        .build();
  }

}
