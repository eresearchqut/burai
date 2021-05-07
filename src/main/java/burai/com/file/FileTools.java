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

package burai.com.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileTools {

    private static final int BYTE_BUFFER = 1024;

    private FileTools() {
        // NOP
    }

    public static boolean copyFile(File srcFile, File dstFile) {
        return copyFile(srcFile, dstFile, true);
    }

    public static boolean copyFile(File srcFile, File dstFile, boolean progress) {
        if (srcFile == null || dstFile == null) {
            return false;
        }

        if (!srcFile.isFile()) {
            return false;
        }

        final File dstFileEff;
        if (!dstFile.isDirectory()) {
            dstFileEff = dstFile;
        } else {
            String name = srcFile.getName();
            if (name != null) {
                dstFileEff = new File(dstFile, name);
            } else {
                dstFileEff = null;
            }
        }

        if (dstFileEff == null) {
            return false;
        }

        FileProgress fileProgress = new FileProgress("Copying file: " + srcFile.getPath() + ".");

        fileProgress.runFileOperation(() -> {
            boolean status = true;
            InputStream input = null;
            OutputStream output = null;

            try {
                input = new BufferedInputStream(new FileInputStream(srcFile));
                output = new BufferedOutputStream(new FileOutputStream(dstFileEff, false));

                byte[] byteBuffer = new byte[BYTE_BUFFER];

                while (true) {
                    int nbyte = input.read(byteBuffer);
                    if (nbyte < 0) {
                        break;
                    }
                    output.write(byteBuffer, 0, nbyte);
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                status = false;

            } catch (IOException e2) {
                e2.printStackTrace();
                status = false;

            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        status = false;
                    }
                }

                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        status = false;
                    }
                }
            }

            return status;
        });

        if (progress) {
            fileProgress.showProgress();
        }

        return fileProgress.waitProgress();
    }

    public static boolean copyAllFiles(File srcFile, File dstFile) {
        return copyAllFiles(srcFile, dstFile, true);
    }

    public static boolean copyAllFiles(File srcFile, File dstFile, boolean progress) {
        if (srcFile == null || dstFile == null) {
            return false;
        }

        if (!srcFile.exists()) {
            return false;
        }

        boolean nestFile = false;
        if (srcFile.isDirectory()) {
            if (dstFile.isFile()) {
                return false;
            }
        } else {
            if (dstFile.isDirectory()) {
                nestFile = true;
            }
        }

        final File dstFileEff;
        if (!nestFile) {
            dstFileEff = dstFile;
        } else {
            String name = srcFile.getName();
            if (name != null) {
                dstFileEff = new File(dstFile, name);
            } else {
                dstFileEff = null;
            }
        }

        if (dstFileEff == null) {
            return false;
        }

        FileProgress fileProgress = new FileProgress("Copying file: " + srcFile.getPath() + ".");

        fileProgress.runFileOperation(() -> {
            boolean status = true;

            if (srcFile.isDirectory()) {
                if (!dstFileEff.isDirectory()) {
                    status = dstFileEff.mkdir();
                }
                if (status) {
                    File[] files = srcFile.listFiles();
                    for (File file : files) {
                        String name = file.getName();
                        if (name == null || name.isEmpty()) {
                            continue;
                        }
                        File srcFile2 = file;
                        File dstFile2 = new File(dstFileEff, name);
                        status = status && copyAllFiles(srcFile2, dstFile2, false);
                    }
                }

            } else {
                status = copyFile(srcFile, dstFileEff, false);
            }

            return status;
        });

        if (progress) {
            fileProgress.showProgress();
        }

        return fileProgress.waitProgress();
    }

    public static boolean deleteAllFiles(File file) {
        return deleteAllFiles(file, true);
    }

    public static boolean deleteAllFiles(File file, boolean progress) {
        if (file == null) {
            return false;
        }

        FileProgress fileProgress = new FileProgress("Deleting file: " + file.getPath() + ".");

        fileProgress.runFileOperation(() -> {
            boolean status = true;

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file2 : files) {
                    status = status && deleteAllFiles(file2, false);
                }
                status = status && file.delete();

            } else {
                status = file.delete();
            }

            return status;
        });

        if (progress) {
            fileProgress.showProgress();
        }

        return fileProgress.waitProgress();
    }
}
