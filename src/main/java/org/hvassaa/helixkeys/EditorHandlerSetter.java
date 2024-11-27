package org.hvassaa.helixkeys;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.actions.EscapeAction;

import org.jetbrains.annotations.NotNull;

public class EditorHandlerSetter extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    TypedAction typedAction = TypedAction.getInstance();
    TypedActionHandler originalRawHandler = typedAction.getRawHandler();
    typedAction.setupRawHandler(new TestAction(originalRawHandler));

    // this does not work, how to capture escape???
    EscapeAction asd = new EscapeAction();
    EditorActionHandler orginal = asd.getHandler();
    asd.setupHandler(new EscapeToNormalHandler(orginal));
  }
}
