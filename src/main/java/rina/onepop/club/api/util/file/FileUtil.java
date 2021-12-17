package rina.onepop.club.api.util.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author SrRina
 * @since 22/01/2021 at 23:29
 **/
public class FileUtil {
    public static final int BUFFER = 4096;

    public static void renameFolder(String folder, String renameTo) throws IOException {
        final File old = new File(folder);
        final File nnw = new File(renameTo);

        old.renameTo(nnw);
    }

    public static void deleteFolder(String path) throws IOException {
        Files.walk(Paths.get(path)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    public static void createFileIfNeeded(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public static void createFolderIfNeeded(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    public static void deleteFile(final String path) {
        File file = new File(path);
        file.delete();
    }

    /**
     * Simple extract zip to manage zip files.
     *
     * @param zipPath the current zip file path.
     * @param extractedPath the extracted path of zip file.
     */
    public static void extractZipFolder(String zipPath, String extractedPath) {
        try {
            File file = new File(zipPath);
            ZipFile zip = new ZipFile(file);

            new File(extractedPath).mkdir();

            Enumeration zipFilesEntries = zip.entries();

            while (zipFilesEntries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipFilesEntries.nextElement();

                File destFile = new File(extractedPath, entry.getName());
                File destinationFileParent = destFile.getParentFile();

                destinationFileParent.mkdir();

                if (entry.isDirectory() == false) {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zip.getInputStream(entry));

                    int currentByte;

                    byte data[] = new byte[BUFFER];

                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fileOutputStream, BUFFER);

                    while ((currentByte = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }

                    dest.flush();
                    dest.close();

                    bufferedInputStream.close();
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static void compactZipFolder(String sourceDirPath, String zipFilePath) throws IOException {
        Path path1 = Files.createFile(Paths.get(zipFilePath));
        Path path2 = Paths.get(sourceDirPath);

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(path1))) {
            Stream<Path> paths = Files.walk(path2);

            paths.filter(path -> Files.isDirectory(path) == false).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(path2.relativize(path).toString());

                try {
                    zip.putNextEntry(zipEntry);
                    Files.copy(path, zip);
                    zip.close();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            });
        }
    }
}
