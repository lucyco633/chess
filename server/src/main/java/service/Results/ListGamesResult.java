package service.Results;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public record ListGamesResult(Collection<GameData> games) {
}
