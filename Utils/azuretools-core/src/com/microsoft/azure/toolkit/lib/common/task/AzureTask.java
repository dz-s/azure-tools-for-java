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

package com.microsoft.azure.toolkit.lib.common.task;

import lombok.Builder;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class AzureTask<T> {
    private Modality modality;
    private Supplier<T> supplier;
    private Object project;
    private boolean cancellable;
    @Builder.Default
    private boolean backgroundable = true;
    private String title;

    public AzureTask(Runnable runnable) {
        this.supplier = () -> {
            runnable.run();
            return null;
        };
        this.modality = Modality.DEFAULT;
    }

    public AzureTask(Supplier<T> supplier) {
        this.supplier = supplier;
        this.modality = Modality.DEFAULT;
    }

    public AzureTask(Runnable runnable, Modality modality) {
        this.supplier = () -> {
            runnable.run();
            return null;
        };
        this.modality = modality;
    }

    public AzureTask(Supplier<T> supplier, Modality modality) {
        this.supplier = supplier;
        this.modality = modality;
    }

    public AzureTask(Object project, String title, boolean cancellable, Runnable runnable) {
        this.project = project;
        this.title = title;
        this.cancellable = cancellable;
        this.supplier = () -> {
            runnable.run();
            return null;
        };
        this.modality = Modality.DEFAULT;
    }

    public AzureTask(Object project, String title, boolean cancellable, Supplier<T> supplier) {
        this.project = project;
        this.title = title;
        this.cancellable = cancellable;
        this.supplier = supplier;
        this.modality = Modality.DEFAULT;
    }

    public AzureTask(Object project, String title, boolean cancellable, Runnable runnable, Modality modality) {
        this.project = project;
        this.title = title;
        this.cancellable = cancellable;
        this.supplier = () -> {
            runnable.run();
            return null;
        };
        this.modality = modality;
    }

    public AzureTask(Object project, String title, boolean cancellable, Supplier<T> supplier, Modality modality) {
        this.project = project;
        this.title = title;
        this.cancellable = cancellable;
        this.supplier = supplier;
        this.modality = modality;
    }

    public enum Modality {
        DEFAULT, ANY, NONE
    }
}
