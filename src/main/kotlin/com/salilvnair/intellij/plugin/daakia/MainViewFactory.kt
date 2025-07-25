package com.salilvnair.intellij.plugin.daakia

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowFactory
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.DaakiaMainPanel
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext
import javax.swing.SwingUtilities
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils


class MainViewFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val rootPane = SwingUtilities.getRootPane(toolWindow.component)
        val dataContext = DataContext(project, GlobalContext())
        val mainPanel = DaakiaMainPanel(rootPane, dataContext)

        val openAsTabAction: AnAction = object : AnAction("About Daakia") {
            override fun actionPerformed(e: AnActionEvent) {
                DaakiaUtils.showAboutDaakia(mainPanel)
            }
        }
        openAsTabAction.templatePresentation.icon = DaakiaIcons.DaakiaIcon
        toolWindow.setTitleActions(listOf(openAsTabAction))

        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(mainPanel, "", true)
        contentManager.addContent(content)

        toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, null)
        toolWindow.setIcon(DaakiaIcons.DaakiaIcon)
    }
}