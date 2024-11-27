package org.hvassaa.helixkeys;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import org.jetbrains.annotations.NotNull;

public class EditorHandlerSetter extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TypedAction typedAction = TypedAction.getInstance();

        typedAction.setupRawHandler(new TestAction());
    }
}
