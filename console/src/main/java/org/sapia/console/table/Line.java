package org.sapia.console.table;


/**
 * Models a line of content.
 * 
 * @author Yanick Duchesne
 * 
 */
class Line {
	
  StringBuffer _content = new StringBuffer();
  Line         _next;

  Line() {
  }

  Line append(String content) {
    _content.append(content);

    return this;
  }

  void format(int cellWidth) {
    Utils.formatLine(this, cellWidth);
  }

  String render(int width, int cellSpacing) {
    insertSpaces(_content, cellSpacing);
    appendSpaces(_content, width, cellSpacing);

    StringBuffer toReturn = new StringBuffer();
    toReturn.append(_content);

    return toReturn.toString();
  }

  int getLineCount() {
    Line current = this;
    int  count = 1;

    while (current._next != null) {
      current = current._next;
      count++;
    }

    return count;
  }

  Line getLineAt(int height) {
    int  count   = 0;
    Line current = this;

    while (count < height) {
      current = current._next;
      count++;
    }

    return current;
  }

  void addEmptyLines(int numToAdd, int width, int cellSpacing) {
    for (int i = 0; i < numToAdd; i++) {
      addEmptyLine(width, cellSpacing);
    }
  }

  void addEmptyLine(int width, int cellSpacing) {
    Line current = this;

    while (current._next != null) {
      current = current._next;
    }

    Line line = new Line();
    current._next = line;
  }

  private void insertSpaces(StringBuffer buf, int cellSpacing) {
    for (int i = 0; i < cellSpacing; i++) {
      buf.insert(i, ' ');
    }
  }

  private void appendSpaces(StringBuffer buf, int width, int cellSpacing) {
    int j = (width + (2 * cellSpacing)) - buf.length();

    for (int i = 0; i < j; i++) {
      buf.append(' ');
    }
  }
}
