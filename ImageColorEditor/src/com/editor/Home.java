package com.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class Home extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        new MainUi();
    }
}
