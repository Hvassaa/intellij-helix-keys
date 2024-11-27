package org.hvassaa.helixkeys;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

//https://intellij-support.jetbrains.com/hc/en-us/community/posts/207745409-What-is-the-way-to-catch-events-about-system-keys

public class TestAction implements TypedActionHandler {
  private TypedActionHandler originalHandler;

  public TestAction(TypedActionHandler originHandler) {
    this.originalHandler = originHandler;
  }

  @Override
  public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
    State state = State.getInstance();

    if (state.getMode() == Mode.INSERT) {
      originalHandler.execute(editor, charTyped, dataContext);
    } else if (state.getMode() == Mode.NORMAL) {
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
        case 'i' -> state.doI(editor);
        case 'a' -> state.doA(editor);
      }
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
}

enum Mode {
  NORMAL,
  INSERT
}

enum Ops {
  MATCH,
  INSIDE,
  AROUND
}
