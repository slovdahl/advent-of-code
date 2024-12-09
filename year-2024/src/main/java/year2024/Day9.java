package year2024;

import lib.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day9 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        String diskMap = input.findFirst().orElseThrow();
        char[] charArray = diskMap.toCharArray();

        int fileId = 0;
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < charArray.length; i++) {
            int digit = Character.digit(charArray[i], 10);
            if (i % 2 == 0) {
                for (int n = 0; n < digit; n++) {
                    blocks.add(new FileBlock(fileId));
                }
                fileId++;
            }
            else {
                for (int n = 0; n < digit; n++) {
                    blocks.add(new EmptySpace());
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

        long checksum = 0;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block instanceof FileBlock(int id)) {
                checksum += (long) id * i;
            }
        }

        return checksum; // Your puzzle answer was 6382875730645
    }

    private static int getFirstFreePosition(List<Block> blocks, int from) {
        for (int i = from; i < blocks.size(); i++) {
            if (blocks.get(i) instanceof EmptySpace) {
                return i;
            }
        }

        return -1;
    }

    private interface Block {}

    record FileBlock(int id) implements Block{
    }

    record EmptySpace() implements Block {
    }
}
