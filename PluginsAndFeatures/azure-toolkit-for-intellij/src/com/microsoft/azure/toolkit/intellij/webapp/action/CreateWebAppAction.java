/*
 * Copyright (c) Microsoft Corporation
 *
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.microsoft.azure.toolkit.intellij.webapp.action;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.toolkit.intellij.webapp.WebAppCreationDialog;
import com.microsoft.azure.toolkit.lib.common.handler.AzureExceptionHandler;
import com.microsoft.azure.toolkit.lib.common.handler.AzureExceptionHandler.AzureExceptionAction;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azure.toolkit.lib.common.task.AzureTask;
import com.microsoft.azure.toolkit.lib.common.task.AzureTaskManager;
import com.microsoft.azure.toolkit.lib.webapp.WebAppConfig;
import com.microsoft.azure.toolkit.lib.webapp.WebAppService;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.azurecommons.helpers.Nullable;
import com.microsoft.azuretools.ijidea.actions.AzureSignInAction;
import com.microsoft.azuretools.utils.AzureUIRefreshCore;
import com.microsoft.azuretools.utils.AzureUIRefreshEvent;
import com.microsoft.azuretools.utils.WebAppUtils;
import com.microsoft.intellij.runner.RunProcessHandler;
import com.microsoft.intellij.util.AzureLoginHelper;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.helpers.Name;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.webapp.WebAppModule;
import rx.Single;

import java.nio.file.Path;
import java.util.Objects;

import static com.microsoft.intellij.ui.messages.AzureBundle.message;

@Name("Create Web App")
public class CreateWebAppAction extends NodeActionListener {
    private static final String NOTIFICATION_GROUP_ID = "Azure Plugin";
    private final WebAppService webappService;
    private final WebAppModule webappModule;

    public CreateWebAppAction(WebAppModule webappModule) {
        super();
        this.webappModule = webappModule;
        this.webappService = WebAppService.getInstance();
    }

    @Override
    @AzureOperation(value = "open web app creation dialog", type = AzureOperation.Type.ACTION)
    public void actionPerformed(NodeActionEvent e) {
        final Project project = (Project) webappModule.getProject();
        if (!AzureSignInAction.doSignIn(AuthMethodManager.getInstance(), project) ||
            !AzureLoginHelper.isAzureSubsAvailableOrReportError(message("common.error.signIn"))) {
            return;
        }
        this.openDialog(project, null);
    }

    private void openDialog(final Project project, @Nullable final WebAppConfig config) {
        final WebAppCreationDialog dialog = this.createDialog(project, config);
        dialog.setOkActionListener((data) -> {
            DefaultLoader.getIdeHelper().invokeLater(dialog::close);
            this.createWebApp(data, project, dialog)
                .subscribe(webapp -> {
                    this.notifyCreationSuccess(webapp);
                    this.refreshAzureExplorer();
                }, (error) -> {
                    final AzureExceptionAction action = AzureExceptionAction.simple(
                        "Reopen Web App Creation Dialog",
                        t -> AzureTaskManager.getInstance().runLater(() -> this.openDialog(project, config)));
                    AzureExceptionHandler.notify(error, action);
                });
        });
        dialog.show();
    }

    @AzureOperation(value = "create web app", type = AzureOperation.Type.ACTION)
    private Single<WebApp> createWebApp(final WebAppConfig config, final Project project, final WebAppCreationDialog dialog) {
        final AzureTask<WebApp> task = new AzureTask<>(null, message("webapp.create.task.title"), false, () -> {
            boolean test = true;
            if (test) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("test");
            }
            final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
            indicator.setIndeterminate(true);
            return webappService.createWebApp(config);
        });
        return AzureTaskManager.getInstance().runInModal(task).toSingle().doOnSuccess(webapp -> {
            final Path artifact = config.getApplication();
            if (Objects.nonNull(artifact) && artifact.toFile().exists()) {
                AzureTaskManager.getInstance().runLater(() -> deploy(webapp, artifact, project));
            }
        });
    }

    @AzureOperation(value = "deploy artifact to web app", type = AzureOperation.Type.SERVICE)
    private void deploy(final WebApp webapp, final Path application, final Project project) {
        final AzureTask<Void> task = new AzureTask<>(null, message("webapp.deploy.task.title"), true, () -> {
            ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
            final RunProcessHandler processHandler = new RunProcessHandler();
            processHandler.addDefaultListener();
            final ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            processHandler.startNotify();
            consoleView.attachToProcess(processHandler);
            WebAppUtils.deployArtifactsToAppService(webapp, application.toFile(), true, processHandler);
        });
        AzureTaskManager.getInstance().runInModal(task);
    }

    @AzureOperation(value = "refresh azure explorer", type = AzureOperation.Type.TASK)
    private void refreshAzureExplorer() {
        AzureTaskManager.getInstance().runLater(() -> {
            if (AzureUIRefreshCore.listeners != null) {
                AzureUIRefreshCore.execute(new AzureUIRefreshEvent(AzureUIRefreshEvent.EventType.REFRESH, null));
            }
        });
    }

    private void notifyCreationSuccess(final WebApp app) {
        final String title = message("webapp.create.task.success.notification.title");
        final String message = String.format(message("webapp.create.task.success.notification.message"), app.name());
        final Notification notification = new Notification(NOTIFICATION_GROUP_ID, title, message, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }

    private WebAppCreationDialog createDialog(final Project project, @Nullable WebAppConfig config) {
        final WebAppCreationDialog dialog = new WebAppCreationDialog(project);
        if (Objects.nonNull(config)) {
            dialog.setData(config);
        }
        return dialog;
    }
}
