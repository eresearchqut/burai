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

package burai.ssh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import burai.com.env.Environments;

import com.google.gson.Gson;

public class SSHServerList {

    private static SSHServerList instance = null;

    public static SSHServerList getInstance() {
        if (instance == null) {
            String path = Environments.getSSHDataPath();

            if (path != null && !path.isEmpty()) {
                try {
                    instance = readFile(path);

                } catch (IOException e) {
                    e.printStackTrace();
                    instance = null;
                }
            }
        }

        if (instance == null) {
            instance = new SSHServerList();
        }

        return instance;
    }

    private static SSHServerList readFile(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            return null;
        }

        Reader reader = null;
        SSHServerList sshServerList = null;

        try {
            File file = new File(path);
            if (!file.isFile()) {
                return null;
            }

            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            sshServerList = gson.fromJson(reader, SSHServerList.class);

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return sshServerList;
    }

    private static void writeFile(String path, SSHServerList sshServerList) throws IOException {
        if (path == null || path.isEmpty()) {
            return;
        }

        if (sshServerList == null) {
            return;
        }

        Writer writer = null;

        try {
            File file = new File(path);
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(sshServerList, writer);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }

    private List<SSHServer> sshServers;

    private SSHServerList() {
        this.sshServers = new ArrayList<SSHServer>();
    }

    public SSHServer getSSHServer(String title) {
        if (title != null && !title.isEmpty()) {
            return this.getSSHServer(new SSHServer(title));
        }

        return null;
    }

    public SSHServer getSSHServer(SSHServer sshServer) {
        if (sshServer == null) {
            return null;
        }

        int index = this.sshServers.indexOf(sshServer);

        return index < 0 ? null : this.sshServers.get(index);
    }

    public boolean hasSSHServer(String title) {
        if (title != null && !title.isEmpty()) {
            return this.hasSSHServer(new SSHServer(title));
        }

        return false;
    }

    public boolean hasSSHServer(SSHServer sshServer) {
        if (sshServer == null) {
            return false;
        }

        return this.sshServers.contains(sshServer);
    }

    public SSHServer[] listSSHServers() {
        if (this.sshServers.isEmpty()) {
            return null;
        }

        SSHServer[] sshArray = new SSHServer[this.sshServers.size()];
        return this.sshServers.toArray(sshArray);
    }

    public void addSSHServer(SSHServer sshServer) {
        if (sshServer == null) {
            return;
        }

        while (this.sshServers.contains(sshServer)) {
            this.sshServers.remove(sshServer);
        }

        this.sshServers.add(sshServer);
    }

    public void removeSSHServer(SSHServer sshServer) {
        if (sshServer == null) {
            return;
        }

        while (this.sshServers.contains(sshServer)) {
            this.sshServers.remove(sshServer);
        }
    }

    public void saveToFile() {
        String path = Environments.getSSHDataPath();

        if (path != null && !path.isEmpty()) {
            try {
                writeFile(path, this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
