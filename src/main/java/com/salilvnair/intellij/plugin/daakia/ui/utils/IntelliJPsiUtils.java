package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author Salil V Nair
 */
public class IntelliJPsiUtils {
    private IntelliJPsiUtils() {}


    public static PsiFile[] findFileByName(Project project, String fileName) {
        return FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
    }

    public static PsiFile findFileByPath(Project project, String filePath) {
        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (virtualFile != null) {
            return PsiManager.getInstance(project).findFile(virtualFile);
        }
        return null;
    }

}
