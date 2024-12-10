package year2024;

import lib.Day;
import lib.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day9 extends Day {

    private String diskMap;
    private char[] charArray;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        diskMap = input.findFirst().orElseThrow();
        charArray = diskMap.toCharArray();
    }

    @Override
    protected Object part1(Stream<String> input) {
        int fileId = 0;
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < charArray.length; i++) {
            int size = Character.digit(charArray[i], 10);
            if (i % 2 == 0) {
                for (int n = 0; n < size; n++) {
                    blocks.add(new FileBlock(fileId, size));
                }
                fileId++;
            } else {
                for (int n = 0; n < size; n++) {
                    blocks.add(new EmptySpace(size));
                }
            }
        }

        int firstFreePosition = getFirstFreePosition(blocks, 0);

        for (int i = blocks.size() - 1; i >= 1; i--) {
            Block block = blocks.get(i);
            if (block instanceof EmptySpace) {
                continue;
            }

            blocks.set(firstFreePosition, block);
            blocks.remove(i);

            firstFreePosition = getFirstFreePosition(blocks, firstFreePosition + 1);
            if (firstFreePosition == -1 || firstFreePosition >= i) {
                // No more free blocks
                break;
            }
        }

        return calculateChecksum(blocks); // Your puzzle answer was 6382875730645
    }

    @Override
    protected Object part2(Stream<String> input) {
        int fileId = 0;
        List<Block> blocks = new ArrayList<>();
        Map<Integer, EmptySpace> freeBlocks = new TreeMap<>();
        for (int i = 0; i < charArray.length; i++) {
            int size = Character.digit(charArray[i], 10);
            if (i % 2 == 0) {
                for (int n = 0; n < size; n++) {
                    blocks.add(new FileBlock(fileId, size));
                }
                fileId++;
            } else {
                int blockStart = blocks.size();
                for (int n = 0; n < size; n++) {
                    blocks.add(new EmptySpace(size));
                }
                if (size > 0) {
                    freeBlocks.put(blockStart, new EmptySpace(size));
                }
            }
        }

        Set<Integer> moved = new HashSet<>();

        for (int i = blocks.size() - 1; i >= 1; i--) {
            Block block = blocks.get(i);
            if (!(block instanceof FileBlock fileBlock)) {
                continue;
            }

            if (moved.contains(fileBlock.id())) {
                continue;
            }

            Optional<Pair<EmptySpace, Integer>> optionalFirstBigEnoughFreeBlock = findFirstBigEnoughFreeBlock(freeBlocks, fileBlock.size());
            if (optionalFirstBigEnoughFreeBlock.isEmpty()) {
                continue;
            }

            EmptySpace freeBlock = optionalFirstBigEnoughFreeBlock.get().first();
            Integer freeBlockStart = optionalFirstBigEnoughFreeBlock.get().second();

            if (freeBlockStart >= i) {
                continue;
            }

            // Get all the file block parts to move
            List<FileBlock> fileBlocksToMove = new ArrayList<>(fileBlock.size());
            for (int j = i - fileBlock.size() + 1; j < i; j++) {
                fileBlocksToMove.add((FileBlock) blocks.get(j));
            }
            fileBlocksToMove.add(fileBlock);

            // Find the start of the preceding free block, if any
            int startOfFreeBlock = -1;
            for (int j = i - fileBlock.size(); j >= Math.max(i - fileBlock.size() - 9 - 1, 0); j--) {
                Block b = blocks.get(j);
                if (!(b instanceof EmptySpace)) {
                    startOfFreeBlock = j + 1;
                    break;
                }
            }

            // Find the end of the following free block, if any
            int endOfFreeBlock = -1;
            for (int j = i + 1; j < Math.min(i + 9 + 1, blocks.size()); j++) {
                Block b = blocks.get(j);
                if (!(b instanceof EmptySpace)) {
                    endOfFreeBlock = j - 1;
                    break;
                }
            }
            if (endOfFreeBlock == -1) {
                // No free block following this block, only mark free until the end of the file block.
                endOfFreeBlock = i;
            }

            if (startOfFreeBlock != -1) {
                EmptySpace newEmptySpace = new EmptySpace(endOfFreeBlock - startOfFreeBlock + 1);
                for (int j = startOfFreeBlock; j <= endOfFreeBlock; j++) {
                    blocks.set(j, newEmptySpace);
                }
                freeBlocks.put(startOfFreeBlock, newEmptySpace);
                freeBlocks.remove(i + 1);
            }

            int blocksToMove = fileBlocksToMove.size();
            for (int j = freeBlockStart; j < freeBlockStart + blocksToMove; j++) {
                if (!(blocks.get(j) instanceof EmptySpace)) {
                    throw new IllegalStateException();
                }
                blocks.set(j, fileBlocksToMove.removeFirst());
            }

            if (!fileBlocksToMove.isEmpty()) {
                throw new IllegalStateException();
            }

            if (freeBlock.size() > fileBlock.size()) {
                EmptySpace remainingFreeSpace = new EmptySpace(freeBlock.size() - fileBlock.size());
                for (int j = freeBlockStart + fileBlock.size(); j < freeBlockStart + freeBlock.size(); j++) {
                    blocks.set(j, remainingFreeSpace);
                }
                freeBlocks.put(freeBlockStart + fileBlock.size(), remainingFreeSpace);
            }

            freeBlocks.remove(freeBlockStart);

            moved.add(fileBlock.id());
        }

        return calculateChecksum(blocks); // Your puzzle answer was 6420913943576
    }

    private static long calculateChecksum(List<Block> blocks) {
        long checksum = 0;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block instanceof FileBlock(int id, int _)) {
                checksum += (long) id * i;
            }
        }
        return checksum;
    }

    private static int getFirstFreePosition(List<Block> blocks, int from) {
        for (int i = from; i < blocks.size(); i++) {
            if (blocks.get(i) instanceof EmptySpace) {
                return i;
            }
        }

        return -1;
    }

    private static Optional<Pair<EmptySpace, Integer>> findFirstBigEnoughFreeBlock(Map<Integer, EmptySpace> freeBlocks, int neededSize) {
        for (var entry : freeBlocks.entrySet()) {
            EmptySpace freeBlock = entry.getValue();
            Integer freeBlockStart = entry.getKey();
            if (freeBlock.size() < neededSize) {
                continue;
            }

            return Optional.of(Pair.of(freeBlock, freeBlockStart));
        }

        return Optional.empty();
    }

    private interface Block {
        int size();
    }

    record FileBlock(int id, int size) implements Block {
    }

    record EmptySpace(int size) implements Block {
    }
}
