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

package burai.app.icon.web;

import java.util.HashMap;
import java.util.Map;

import burai.com.env.Environments;

public class WebEngineFactory {

    private static WebEngineFactory webEngineFactory;

    public static WebEngineFactory getInstance() {
        if (webEngineFactory == null) {
            webEngineFactory = new WebEngineFactory();
        }

        return webEngineFactory;
    }

    private Map<String, WebEngineWrapper> webEngineMap;

    private WebEngineFactory() {
        this.webEngineMap = new HashMap<String, WebEngineWrapper>();
    }

    public synchronized WebEngineWrapper getWebEngine(String url) {
        String url2 = url == null ? null : url.trim();
        if (url2 == null || url2.isEmpty()) {
            return new WebEngineWrapper();
        }

        if (this.webEngineMap.containsKey(url2)) {
            return this.webEngineMap.get(url2);
        }

        WebEngineWrapper webEngine = new WebEngineWrapper(url2);
        WebLoader.getInstance().loadWebEngine(url2, webEngine);
        this.webEngineMap.put(url2, webEngine);
        return webEngine;
    }

    public void touchAllWebEngines() {
        String[] urls = Environments.listWebsites();
        if (urls == null || urls.length < 1) {
            return;
        }

        for (String url : urls) {
            String url2 = url == null ? null : url.trim();
            if (url2 != null && (!url2.isEmpty())) {
                this.getWebEngine(url2);
            }
        }
    }
}
