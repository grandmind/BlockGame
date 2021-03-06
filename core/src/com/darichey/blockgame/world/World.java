package com.darichey.blockgame.world;

import com.badlogic.gdx.math.Vector2;
import com.darichey.blockgame.entity.block.Block;
import com.darichey.blockgame.entity.dynamic.DynamicEntity;
import com.darichey.blockgame.entity.dynamic.EntityPlayer;
import com.darichey.blockgame.world.chunk.Chunk;
import com.darichey.blockgame.util.PerlinNoise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Contains all of the entities and other game objects that the player can interact with.
 */
public class World {

	/**
	 * The gravity of this world.
	 */
	public static final int GRAVITY_VELOCITY = 25;

	public static final int LOAD_RADIUS = 2;

	/**
	 * A hashmap of the {@link Chunk}s that make up the world. The Integer key is the x-position of the chunk in the world.
	 */
	private HashMap<Integer, Chunk> chunks = new HashMap<Integer, Chunk>();

	/**
	 * A list of {@link DynamicEntity}s in the world.
	 */
	private ArrayList<DynamicEntity> dynamicEntities = new ArrayList<DynamicEntity>();

	/**
	 * The {@link EntityPlayer} in the world.
	 */
	public EntityPlayer player = (EntityPlayer) spawnEntityAt(new EntityPlayer(), new Vector2(0, 5));

	public PerlinNoise noise = new PerlinNoise(new Random().nextLong());

	public World() {

		for (int i = -2; i < 2; i++) {
			addChunk(new Chunk(noise, i));
		}

		/*
		for (int x = -32; x < 32; x++) {
			int columnHeight = noise.getNoise(x, 256);
			for (int y = 0; y < columnHeight; y++) {
				Block block;
				if (y  < columnHeight - 5) {
					block = Blocks.stone;
				} else if (y == columnHeight - 1) {
					block = Blocks.grass;
				} else {
					block = Blocks.dirt;
				}
				if (y == 0) block = Blocks.bedrock;

				setBlockAt(block, new Vector2(x, y));
			}
		}
		*/

	}

	/**
	 * Gets the list of dynamic entities in the world.
	 * @return The dynamic entities.
	 */
	public ArrayList<DynamicEntity> getDynamicEntities() {
		return this.dynamicEntities;
	}

	/**
	 * Gets the {@link Block} at the given position.
	 * @param pos The position in the world.
	 * @return The Block at that position.
	 */
	public Block getBlockAt(Vector2 pos) {
		return getChunkForWorldPos(pos).getBlockAt(getChunkForWorldPos(pos).convertWorldToChunkPos(pos));
	}

	/**
	 * Sets the block at the given position to the given block.
	 * @param block The block.
	 * @param pos The position in the world.
	 */
	public void setBlockAt(Block block, Vector2 pos) {
		Chunk chunk = getChunkForWorldPos(pos);
		chunk.setBlockAt(block, chunk.convertWorldToChunkPos(pos));
	}

	/**
	 * Spawns an entity at the given position.
	 * @param entity The entity.
	 * @param pos The position.
	 * @return The entity at the given position.
	 */
	public DynamicEntity spawnEntityAt(DynamicEntity entity, Vector2 pos) {
		entity.setPosition(pos);
		entity.setWorld(this);
		getDynamicEntities().add(entity);
		return entity;
	}

	public Chunk getChunkForWorldPos(Vector2 pos) {
		int x;
		if (pos.x < 0) {
			x = (int) (-1 * Math.ceil(Math.abs(pos.x) / Chunk.WIDTH));
		} else {
			x = (int) Math.floor(pos.x / Chunk.WIDTH);
		}

		return chunks.get(x);
	}

	public Chunk getChunkAt(int xPos) {
		return chunks.get(xPos);
	}

	public ArrayList<Chunk> getChunks() {
		return new ArrayList<Chunk>(chunks.values());
	}

	public ArrayList<Chunk> getLoadedChunks() {
		ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>(5);
		int playerChunkPos = getChunkForWorldPos(player.getPosition()).getPosition();

		for (Chunk chunk : getChunks()) {
			if (chunk.getPosition()  >= playerChunkPos - LOAD_RADIUS && chunk.getPosition() <= playerChunkPos + LOAD_RADIUS)
				loadedChunks.add(chunk);
		}

		return loadedChunks;
	}

	public void addChunk(Chunk chunk) {
		chunks.put(chunk.getPosition(), chunk);
	}
}
