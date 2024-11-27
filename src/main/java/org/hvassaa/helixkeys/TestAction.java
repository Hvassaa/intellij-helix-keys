package org.hvassaa.helixkeys;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;

import org.jetbrains.annotations.NotNull;

//https://intellij-support.jetbrains.com/hc/en-us/community/posts/207745409-What-is-the-way-to-catch-events-about-system-keys

public class TestAction implements TypedActionHandler {
  private Mode mode;
  private char[] prevs;

  public TestAction() {
    mode = Mode.NORMAL;
    prevs = new char[2];
  }

  private void emptyPrevs() {
    prevs[0] = Character.MIN_VALUE;
    prevs[1] = Character.MIN_VALUE;
    prevs[2] = Character.MIN_VALUE;
  }

  @Override
  public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
    if (mode == Mode.INSERT) {
      TypedActionHandler rawHandler = TypedAction.getInstance().getRawHandler();
      rawHandler.execute(editor, charTyped, dataContext);
      return;
    }

    State state = State.getInstance();
    switch (charTyped) {
      case 'l' -> state.doL(editor);
      case 'h' -> state.doH(editor);
      case 'j' -> state.doJ(editor);
      case 'k' -> state.doK(editor);
      case 'x' -> state.doX(editor);
      case 'w' -> state.doW(editor);
      case 'b' -> state.doB(editor);
      case 'd' -> state.doD(editor);
      case 'm' -> state.doM(editor);
    }

    // else if (charTyped == 'm') {
    // prevs[0] = charTyped;
    // } else if (prevs[0] == 'm' && charTyped == 'i') {
    // prevs[1] = charTyped;
    // Project project = editor.getProject();
    // Runnable runnable = () -> document.insertString(0, "Hello!");
    // WriteCommandAction.runWriteCommandAction(project, runnable);
    // } else if (prevs[0] == 'm' && prevs[1] == 'i' && charTyped == 'w') {
    // editor.getSelectionModel().selectWordAtCaret(false);
    // emptyPrevs();
    // }
  }

  public static class State {
    private static State state;

    private State() {
    }

    public static State getInstance() {
      if (state == null) {
        state = new State();
      }
      return state;

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
}

enum Mode {
  NORMAL,
  INSERT
}
