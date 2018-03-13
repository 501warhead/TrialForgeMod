package io.github.warhead.craftingdeadtrial.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Simple helper methods copied from Mojang's packet implementation for ease.
 */
public class PacketUtil {

    /**
     * Writes a String to the DataOutputStream
     */
    public static void writeString(String toWrite, DataOutput dataOutput) throws IOException {
        if (toWrite.length() > 32767) {
            throw new IOException("String too big");
        } else {
            dataOutput.writeShort(toWrite.length());
            dataOutput.writeChars(toWrite);
        }
    }

    /**
     * Reads a string from a packet
     */
    public static String readString(DataInput dataInput, int stringLength) throws IOException {
        short short1 = dataInput.readShort();

        if (short1 > stringLength) {
            throw new IOException("Received string length longer than maximum allowed (" + short1 + " > " + stringLength + ")");
        } else if (short1 < 0) {
            throw new IOException("Received string length is less than zero! Weird string!");
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            for (int j = 0; j < short1; ++j) {
                stringbuilder.append(dataInput.readChar());
            }

            return stringbuilder.toString();
        }
    }
}
