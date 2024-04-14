package com.salilvnair.intellij.plugin.daakia

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowFactory
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame.DaakiaMainFrame


class MainViewFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val form = DaakiaMainFrame();
//        toolWindow.setIcon(); // Set your tab icon if needed
        val openAsTabAction: AnAction = object : AnAction("Open as Tab") {
            override fun actionPerformed(e: AnActionEvent) {
                println("Opened Tab")
            }
        }
        val actionList: List<AnAction> = listOf(openAsTabAction)
        toolWindow.setTitleActions(actionList)
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(form.rootPane, "", true)
        contentManager.addContent(content)
        toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, null)
        toolWindow.setIcon(DaakiaIcons.DaakiaIcon)
    }
}