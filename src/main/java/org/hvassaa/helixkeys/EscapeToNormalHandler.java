package org.hvassaa.helixkeys;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.template.impl.editorActions.EscapeHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public class EscapeToNormalHandler extends EscapeHandler {
  public EscapeToNormalHandler(EditorActionHandler originalHandler) {
    super(originalHandler);
  }

  @Override
  public void doExecute(@NotNull Editor editor, com.intellij.openapi.editor.Caret caret, DataContext dataContext) {
    State.getInstance().setMode(Mode.NORMAL);
  }
}
