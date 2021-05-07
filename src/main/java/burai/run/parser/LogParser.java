/*
 * Copyright (C) 2018 Satomichi Nishihara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package burai.run.parser;

import java.io.File;
import java.io.IOException;

import burai.project.property.ProjectProperty;

public abstract class LogParser {

    private static final long SLEEP_TIME = 5000L;

    private boolean parsing;

    private boolean ending;

    protected ProjectProperty property;

    public LogParser(ProjectProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property is null.");
        }

        this.parsing = false;
        this.ending = false;
        this.property = property;
    }

    public abstract void parse(File file) throws IOException;

    public void startParsing(File file) {
        if (file == null) {
            return;
        }

        synchronized (this) {
            this.parsing = true;
            this.ending = false;
        }

        Thread thread = new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }
                }

                try {
                    this.parse(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }

                    try {
                        this.wait(SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                this.parse(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (this) {
                this.ending = false;
                this.notifyAll();
            }
        });

        thread.start();
    }

    public void endParsing() {
        synchronized (this) {
            if (!this.parsing) {
                return;
            }

            this.parsing = false;
            this.ending = true;
            this.notifyAll();
        }

        synchronized (this) {
            while (this.ending) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.parsing = false;
            this.ending = false;
        }
    }
}
