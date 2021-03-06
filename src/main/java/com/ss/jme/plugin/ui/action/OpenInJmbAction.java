package com.ss.jme.plugin.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.ss.jme.plugin.JmeModuleComponent;
import com.ss.jme.plugin.jmb.command.client.OpenFileClientCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The action to open a file in jMB.
 *
 * @author JavaSaBr
 */
public class OpenInJmbAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = event.getProject();
        if (file == null || project == null) {
            return;
        }

        Path assetFolder = getAssetFolder(file, project);
        if (assetFolder == null) {
            return;
        }

        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module == null) {
            return;
        }

        final JmeModuleComponent moduleComponent = module.getComponent(JmeModuleComponent.class);
        moduleComponent.sendCommand(new OpenFileClientCommand(assetFolder, Paths.get(file.getPath())));
    }

    /**
     * Gets an asset folder of the filer.
     *
     * @param file    the file.
     * @param project the project.
     * @return the asset folder or null.
     */
    private @Nullable Path getAssetFolder(@NotNull VirtualFile file, @NotNull Project project) {

        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module == null) {
            return null;
        }

        JmeModuleComponent moduleComponent = module.getComponent(JmeModuleComponent.class);
        Path assetFolder = moduleComponent.getAssetFolder();
        Path fileAssetFolder = moduleComponent.getAssetFolder(file);

        if (assetFolder == null || !assetFolder.equals(fileAssetFolder)) {
            return null;
        }

        return assetFolder;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {

        Presentation presentation = event.getPresentation();

        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = event.getProject();
        if (file == null || project == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        presentation.setEnabledAndVisible(getAssetFolder(file, project) != null);
    }
}
