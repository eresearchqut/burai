/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

public class LivingReader {

    private static final int SIZE_BUFFERED_LINES = 16;

    private BufferedReader reader;

    private boolean reading;

    private Queue<String> lines;

    private IOException exception;

    public LivingReader(String path) throws FileNotFoundException {
        this(path == null ? null : new File(path));
    }

    public LivingReader(File file) throws FileNotFoundException {
        this(file == null ? null : new FileReader(file));
    }

    public LivingReader(Reader reader) {
        if (reader == null) {
            throw new NullPointerException("reader is null.");
        }

        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }

        this.reading = true;
        this.lines = null;
        this.exception = null;

        Thread thread = new Thread(() -> this.readAllLines());
        thread.start();
    }

    private void readAllLines() {
        this.lines = new LinkedList<>();

        int nline = 0;
        String line = null;
        String[] subLines = new String[SIZE_BUFFERED_LINES];

        while (true) {
            try {
                line = this.reader.readLine();

            } catch (IOException e) {
                line = null;
                synchronized (this) {
                    this.exception = e;
                }
            }

            if (line != null) {
                subLines[nline] = line;
                nline++;
            }

            if (line == null || nline >= SIZE_BUFFERED_LINES) {
                synchronized (this) {
                    for (int i = 0; i < nline; i++) {
                        this.lines.offer(subLines[i]);
                    }

                    this.notifyAll();
                }
            }

            if (line == null) {
                break;
            }
        }

        synchronized (this) {
            this.reading = false;
            this.notifyAll();
        }
    }

    public synchronized String readLine() throws IOException {
        String line = null;

        while (true) {
            line = this.lines == null ? null : this.lines.poll();
            if (line != null) {
                break;
            }

            if (!this.reading) {
                break;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if ((!this.reading) && this.exception != null) {
            throw this.exception;
        }

        return line;
    }
}
