package org.sapia.dataset.io;

import org.sapia.dataset.util.Strings;

/**
 * A utility class for generating string content.
 * 
 * @author yduchesne
 *
 */
public class ContentBuffer {

  private String indent;
  private int lineLength;
  private int lastLineLength, longestLineLength;
  
  private StringBuilder builder = new StringBuilder();
  
  public ContentBuffer() {
  }
  
  /**
   * @param indentation the indentation to use.
   */
  public ContentBuffer(int indentation) {
    this.indent = Strings.repeat(" ", indentation);
  }

  /**
   * @return a new instance of this class.
   */
  public static ContentBuffer obj() {
    return new ContentBuffer();
  }

  /**
   * @param indentation an indentation (number of space chars at beginning of each line).
   * @return a new instance of this class.
   */
  public static ContentBuffer obj(int indentation) {
    return new ContentBuffer(indentation);
  }
  
  /**
   * @param indentation an indentation (number of space chars at beginning of each line).
   * @param levels the number of indentation levels.
   * @return a new instance of this class.
   */
  public static ContentBuffer obj(int indentation, int levels) {
    return new ContentBuffer(indentation * levels);
  }
  
  /**
   * @param chars a {@link String} potentially embedding a format.
   * @param args the format's arguments.
   * @return this instance.
   * @see String#format(String, Object...)
   */
  public ContentBuffer chars(String chars, Object...format) {
    if (lineLength == 0 && indent != null) {
      builder.append(indent);
    }
    builder.append(String.format(chars, format));
    lineLength += chars.length();
    return this;
  }
  
  /**
   * @param chars some characters to add to this instance.
   * @return this instance.
   */
  public ContentBuffer chars(String chars) {
    if (lineLength == 0 && indent != null) {
      builder.append(indent);
    }
    builder.append(chars);
    lineLength += chars.length();
    return this;
  }

  /**
   * Writes the given string, appending a line separator at the end.
   * 
   * @param line a line {@link String} potentially embedding a format.
   * @param args the format's arguments.
   * @return this instance.
   * @see String#format(String, Object...)
   */
  public ContentBuffer line(String line, Object...args) {
    if (lineLength == 0 && indent != null) {
      builder.append(indent);
    }
    lastLineLength = line.length() + lineLength;
    if (lastLineLength > longestLineLength) {
      longestLineLength = lastLineLength;
    }
    lineLength = 0;
    builder.append(String.format(line, args)).append(System.lineSeparator());
    return this;
  }
  
  /**
   * @param line a line to add to this instance.
   * @return this instance.
   */
  public ContentBuffer line(String line) {
    if (lineLength == 0 && indent != null) {
      builder.append(indent);
    }
    lastLineLength = line.length() + lineLength;
    if (lastLineLength > longestLineLength) {
      longestLineLength = lastLineLength;
    }
    lineLength = 0;
    builder.append(line).append(System.lineSeparator());
    return this;
  }
  
  /**
   * Adds an empty line to this instance.
   * 
   * @return this instance.
   */
  public ContentBuffer line() {
    if (lineLength == 0 && indent != null) {
      builder.append(indent);
    }
    lastLineLength = lineLength;
    if (lastLineLength > longestLineLength) {
      longestLineLength = lastLineLength;
    }
    lineLength = 0;
    builder.append(System.lineSeparator());
    return this;
  }

  /**
   * Repeats the given pattern.
   * 
   * @param pattern a string pattern to repeat.
   * @param repetitions the number of times to repeat the pattern.
   * @return this instance.
   */
  public ContentBuffer repeat(String pattern, int repetitions) {
    return chars(Strings.repeat(pattern, repetitions));
  }
  
  /**
   * Repeats the given pattern, adding a <code>newline</code> at the end.
   * 
   * @param pattern a string pattern to repeat.
   * @param repetitions the number of times to repeat the pattern.
   * @return this instance.
   */
  public ContentBuffer repeatln(String pattern, int repetitions) {
    return line(Strings.repeat(pattern, repetitions));
  }
  
  /**
   * @param buffer a {@link ContentBuffer} to append to this instance.
   * @return this instance.
   */
  public ContentBuffer append(ContentBuffer buffer) {
    chars(buffer.toString());
    return this;
  }
  
  /**
   * @return the current line length.
   */
  public int getCurrentLineLength() {
    return lineLength;
  }
  
  /**
   * @return the length of the last line that was added.
   */
  public int getLastLineLength() {
    return lastLineLength;
  }
  
  /**
   * @return the length of the longest line that was added.
   */
  public int getLongestLineLength() {
    return longestLineLength;
  }
  
  @Override
  public String toString() {
    return builder.toString();
  }
}
