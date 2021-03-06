package org.terasology.mazeapi;

import org.terasology.mazeapi.config.MazeConfig;
import org.terasology.mazeapi.mazeItems.Level;
import org.terasology.mazeapi.mazeItems.MazeRegion;
import org.terasology.mazeapi.mazeItems.ThinWallsLevel;
import org.terasology.mazeapi.mazeItems.WideWallsLevel;

public class Maze {

  public final Level level;
  public final MazeConfig config;

  public Maze(MazeConfig config) {
    this.config = config;
    switch (config.levelTypes) {
      case WIDE_WALLS:
        level = generateWideWallsLevel(config);
        break;
      case THIN_WALLS:
        level = generateThinWallsLevel(config);
        break;
      default:
        throw new UnsupportedOperationException("Requested unknown level type.");
    }
  }

  private WideWallsLevel generateWideWallsLevel(MazeConfig config) {
    checkWWLConfig(config);
    final WideWallsLevel level = new WideWallsLevel(config.width, config.height);
    level.attemptPlaceRoom(1, 1, 5, 5, MazeRegion.MAIN);
    int roomsPlaced = 0; // debug
    for (int i = 0; i < config.roomPlaceAttempts; i++) {
      int roomWidth = config.util.randomInt() % ((config.maxRoomWidth - config.minRoomWidth) / 2 + 1) * 2 + config.minRoomWidth;
      int roomHeight = config.util.randomInt() % ((config.maxRoomHeight - config.minRoomHeight) / 2 + 1) * 2 + config.minRoomHeight;
      int roomX = config.util.randomInt() % ((config.width - roomWidth) / 2) * 2 + 1;
      int roomY = config.util.randomInt() % ((config.height - roomHeight) / 2) * 2 + 1;
      if (level.attemptPlaceRoom(roomX, roomY, roomWidth, roomHeight, new MazeRegion())) {
        roomsPlaced++;
      }
    }
    level.fillEmptySpaceWithMazes(config);
    level.interConnect(config);
    level.removeDeadEnds();
    level.computeTileData();
    return level;
  }

  private ThinWallsLevel generateThinWallsLevel(MazeConfig config) {
    final ThinWallsLevel level = new ThinWallsLevel(config.width, config.height);
    level.attemptPlaceRoom(1, 1, 5, 5, MazeRegion.MAIN);
    int roomsPlaced = 0; // debug
    for (int i = 0; i < config.roomPlaceAttempts; i++) {
      int roomWidth = config.util.randomInt() % (config.maxRoomWidth - config.minRoomWidth) + config.minRoomWidth;
      int roomHeight = config.util.randomInt() % (config.maxRoomHeight - config.minRoomHeight) + config.minRoomHeight;
      int roomX = config.util.randomInt() % (config.width - roomWidth);
      int roomY = config.util.randomInt() % (config.height - roomHeight);
      if (level.attemptPlaceRoom(roomX, roomY, roomWidth, roomHeight, new MazeRegion())) {
        roomsPlaced++;
      }
    }
    level.fillEmptySpaceWithMazes(config);
    level.interConnect(config);
    level.removeDeadEnds();
    return level;
  }

  private void checkWWLConfig(MazeConfig config) {
    checkWWLParameterEven(config.width);
    checkWWLParameterEven(config.height);
    checkWWLParameterEven(config.minRoomHeight);
    checkWWLParameterEven(config.maxRoomHeight);
    checkWWLParameterEven(config.minRoomWidth);
    checkWWLParameterEven(config.maxRoomWidth);
  }

  private void checkWWLParameterEven(int parameter) {
    if (parameter % 2 == 0) {
      throw new IllegalArgumentException("WideWallsLevel must have all dimensions and positions even");
    }
  }
}
