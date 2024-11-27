package org.hvassaa.helixkeys;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;

import java.util.ArrayDeque;
import java.util.Deque;

public class State {
  private static State state;
  private Mode mode = Mode.NORMAL;
  private Deque<Ops> ops = new ArrayDeque<>();

  private State() {
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public Mode getMode() {
    return mode;
  }

  public static State getInstance() {
    if (state == null) {
      state = new State();
    }
    return state;

  }

  public void doI(Editor e) {
    mode = Mode.INSERT;
  }

  public void doA(Editor e) {
    e.getCaretModel().getPrimaryCaret().moveCaretRelatively(1, 0, false, false);
    mode = Mode.INSERT;
  }

  public void doM(Editor e) {
    // This currently does 'mi)' locally to the current line
    Caret caret = e.getCaretModel().getPrimaryCaret();
    Document docuemnt = e.getDocument();
    int caretOffset = caret.getOffset();
    int lineNumber = docuemnt.getLineNumber(caretOffset);
    int lineEndOffset = docuemnt.getLineEndOffset(lineNumber);
    int lineStartOffset = docuemnt.getLineStartOffset(lineNumber);

    TextRange toEnd = TextRange.create(caretOffset, lineEndOffset);
    TextRange toStart = TextRange.create(lineStartOffset, caretOffset);
    String toEndString = docuemnt.getText(toEnd);
    String toStartString = docuemnt.getText(toStart);
    int startMarkerIdx = 0;
    int endMarkerIdx = 0;

    System.out.println(toEndString);
    System.out.println(toStartString);

    for (int i = 0; i < toEndString.length(); i++) {
      char c = toEndString.charAt(i);
      if (c == ')') {
        endMarkerIdx = i;
        break;
      }
    }
    for (int i = 0; i < toStartString.length(); i++) {
      char c = toStartString.charAt(toStartString.length() - 1 - i);
      if (c == '(') {
        startMarkerIdx = i;
        break;
      }
    }

    e.getSelectionModel().setSelection(caretOffset - startMarkerIdx, caretOffset + endMarkerIdx);
  }

  public void doK(Editor e) {
    Caret caret = e.getCaretModel().getPrimaryCaret();
    caret.moveCaretRelatively(0, -1, false, false);
  }

  public void doJ(Editor e) {
    Caret caret = e.getCaretModel().getPrimaryCaret();
    caret.moveCaretRelatively(0, 1, false, false);
  }

  public void doL(Editor e) {
    Caret caret = e.getCaretModel().getPrimaryCaret();
    int offset = caret.getOffset() + 1;
    caret.moveToOffset(offset);
    caret.removeSelection();
  }

  public void doH(Editor e) {
    Caret caret = e.getCaretModel().getPrimaryCaret();
    int offset = caret.getOffset() - 1;
    caret.moveToOffset(offset);
    caret.removeSelection();
  }

  public void doD(Editor e) {
    SelectionModel selectionModel = e.getSelectionModel();
    Project project = e.getProject();
    Runnable runnable;
    Document document = e.getDocument();

    if (e.getSelectionModel().hasSelection()) {
      int selectionStart = selectionModel.getSelectionStart();
      int selectionEnd = selectionModel.getSelectionEnd();
      runnable = () -> document.deleteString(selectionStart, selectionEnd);
    } else {
      int offset = e.getCaretModel().getCurrentCaret().getOffset();
      runnable = () -> document.deleteString(offset, offset + 1);
    }
    WriteCommandAction.runWriteCommandAction(project, runnable);
  }

  public void doB(Editor e) {
    Caret caret = e.getCaretModel().getCurrentCaret();
    SelectionModel selectionModel = e.getSelectionModel();

    int endOffset = caret.getOffset();
    selectionModel.selectWordAtCaret(false);
    int startOffset = selectionModel.getSelectionStart();
    if (endOffset == startOffset) {
      caret.moveCaretRelatively(-1, 0, false, false);
      doB(e);
    } else {
      // selectionModel.removeSelection();
      selectionModel.setSelection(startOffset, endOffset);
      caret.moveToOffset(startOffset);
    }
  }

  public void doW(Editor e) {
    Caret caret = e.getCaretModel().getCurrentCaret();
    SelectionModel selectionModel = e.getSelectionModel();

    int initialOffset = caret.getOffset();
    selectionModel.selectWordAtCaret(false);
    int endOffset = selectionModel.getSelectionEnd();

    if (initialOffset == endOffset) {
      caret.moveCaretRelatively(2, 0, false, false);
      doW(e);
    } else {
      // selectionModel.removeSelection();
      selectionModel.setSelection(initialOffset, endOffset);
      caret.moveToOffset(endOffset);
    }
  }

  public void doX(Editor e) {
    e.getCaretModel().getCurrentCaret().selectLineAtCaret();
  }
}
