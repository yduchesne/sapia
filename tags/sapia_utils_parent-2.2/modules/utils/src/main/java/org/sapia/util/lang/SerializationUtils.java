package org.sapia.util.lang;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class SerializationUtils {

  /**
   * Utility method to read a UTF string from the object input stream.
   * 
   * @param in The object input stream from which to read the data.
   * @return The created string object.
   * @throws IOException If an error occurs.
   */
  public static String readUTFString(ObjectInput in) throws IOException {
    if (in.readBoolean()) {
      return in.readUTF();
    } else {
      return null;
    }
  }

  /**
   * Utility method to write a UTF string in the object output stream.
   * 
   * @param aValue The string value to encode.
   * @param out The object output stream to use.
   * @throws IOException If an erro occurs.
   */
  public static void writeUTFString(String aValue, ObjectOutput out) throws IOException {
    if (aValue != null) {
      out.writeBoolean(true);
      out.writeUTF(aValue);
    } else {
      out.writeBoolean(false);
    }
  }
  
  /**
   * Utility method to write a java date in the object output stream.
   * 
   * @param aDate The date to encode.
   * @param out The object output stream to use.
   * @throws IOException If an erro occurs.
   */
  public static void writeDate(Date aDate, ObjectOutput out) throws IOException {
    if (aDate == null) {
      out.writeByte(0);
    } else {
      if (aDate instanceof java.sql.Date) {
        out.writeByte(1);
      } else if (aDate instanceof java.sql.Time) {
        out.writeByte(2);
      } else if (aDate instanceof java.sql.Timestamp) {
        out.writeByte(3);
      } else {
        out.writeByte(4);
      }
      out.writeLong(aDate.getTime());
    }
  }
  
  /**
   * Utility method to read a java date from the object input stream.
   * 
   * @param in The object input stream from which to read the data.
   * @return The created date object.
   * @throws IOException If an error occurs.
   */
  public static Date readDate(ObjectInput in) throws IOException {
    byte flag = in.readByte();
    if (flag != 0) {
      long time = in.readLong();
      if (flag == 1) {
        return new java.sql.Date(time);
      } else if (flag == 2) {
        return new java.sql.Time(time);
      } else if (flag == 3) {
        return new java.sql.Timestamp(time);
      } else {
        return new Date(time);
      }
    } else {
      return null;
    }
  }
  
  /**
   * Utility method to read the content of a byte[] from the object output stream.
   *   
   * @param in The object input stream.
   * @return The byte array with the data.
   * @throws IOException If an error occurs.
   */
  public static byte[] readByteArray(ObjectInput in) throws IOException {
    byte[] someData = null;
    if (in.readBoolean()) {
      int size = in.readInt();
      someData = new byte[size];
      in.read(someData, 0, size);
    }
    return someData;
  }
  
  /**
   * Utility method that writes the content of the byte[] passed in to the object output stream.
   * 
   * @param someData The byte array to write.
   * @param out The object output stream.
   * @throws IOException If an error occurs.
   */
  public static void writeByteArray(byte[] someData, ObjectOutput out) throws IOException {
    if (someData == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      out.writeInt(someData.length);
      out.write(someData);
    }
  }
}
