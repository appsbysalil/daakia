package com.salilvnair.intellij.plugin.daakia

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowFactory
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame.DaakiaMainFrame
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils


class MainViewFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val form = DaakiaMainFrame();
        val openAsTabAction: AnAction = object : AnAction("About Daakia") {
            override fun actionPerformed(e: AnActionEvent) {
                DaakiaUtils.showAboutDaakia(form)
            }
        }
        openAsTabAction.templatePresentation.icon = DaakiaIcons.DaakiaIcon
        val actionList: List<AnAction> = listOf(openAsTabAction)
        toolWindow.setTitleActions(actionList)
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(form.rootPane, "", true)
        contentManager.addContent(content)
        toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, null)
        toolWindow.setIcon(DaakiaIcons.DaakiaIcon)
    }
}