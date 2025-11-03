package com.jwebmp.core.base.angular.client;

import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.properties.GlobalProperties;
import com.jwebmp.core.base.angular.client.annotations.angular.NgApp;
import com.jwebmp.core.base.angular.client.services.interfaces.INgApp;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;
import static com.jwebmp.core.base.angular.client.services.interfaces.ImportsStatementsComponent.getClassLocationDirectory;

@Log
public class AppUtils
{

    //app name to base user directory map
    private static final Map<String, File> baseAppDirectories = new HashMap<>();
    private static final Map<String, File> baseAppSrcDirectories = new HashMap<>();

    private static final Map<String, File> baseAppMainTSFiles = new HashMap<>();
    private static final Map<String, File> baseAppPackageJsonFiles = new HashMap<>();
    private static final Map<String, File> baseAppNpmrcFiles = new HashMap<>();

    private static final Map<String, File> baseAppTSConfigAppFiles = new HashMap<>();
    private static final Map<String, File> baseAppTSConfigFiles = new HashMap<>();
    private static final Map<String, File> baseGitIgnoreFile = new HashMap<>();
    private static final Map<String, File> basePolyfillsJsonFiles = new HashMap<>();
    private static final Map<String, File> baseAngularJsonFiles = new HashMap<>();
    private static final Map<String, File> baseIndexHtmlFiles = new HashMap<>();

    private static final Map<String, File> baseAppAssetsDirectories = new HashMap<>();

    private static final Map<String, File> baseAppResourceDirectories = new HashMap<>();

    private static final Map<String, File> baseDistDirectories = new HashMap<>();
    private static final Map<String, File> baseDistAssetsDirectories = new HashMap<>();
    public static File baseUserDirectory;

    private AppUtils()
    {

    }

    static
    {
        String userDir = GlobalProperties.getSystemPropertyOrEnvironment("JWEBMP_ROOT_PATH", new File(System.getProperty("user.dir"))
                .getPath());
        baseUserDirectory = new File(userDir.replaceAll("\\\\", "/") + "/webroot/");
        try
        {
            if (!baseUserDirectory.exists())
            {
                FileUtils.forceMkdirParent(baseUserDirectory);
                FileUtils.forceMkdir(baseUserDirectory);
            }
        }
        catch (IOException e)
        {
            log.log(Level.SEVERE, "Unable to create base directory for creating typescript! - " + userDir);
        }
        log.info("TypeScript is compiling to " + baseUserDirectory.getPath() + ". Change with env property \"JWEBMP_ROOT_PATH\"");

    }

    private static void buildFolderStructure(Class<? extends INgApp<?>> app)
    {
        getAppPath(app);
        getAppPackageJsonPath(app, false);

        getAppSrcPath(app);
        getAppMainTSPath(app, false);


        getAppAssetsPath(app);

        getDistPath(app);
        getDistAssetsPath(app);
    }

    private static boolean hasAppDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasMainTSFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppMainTSFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasPackageJsonFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppPackageJsonFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasNprmcFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppNpmrcFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }


    private static boolean hasTSConfigAppFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppTSConfigAppFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasTSConfigFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppTSConfigFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasPolyfillsFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (basePolyfillsJsonFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasAngularJsonFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAngularJsonFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasIndexHtmlFile(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseIndexHtmlFiles.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasDistDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseDistDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasDistAssetsDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseDistAssetsDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasAppSrcDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppSrcDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasAppAssetsDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppAssetsDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    private static boolean hasAppResourceDir(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (baseAppResourceDirectories.containsKey(appName))
        {
            return true;
        }
        return false;
    }

    public static String getAppName(Class<? extends INgApp<?>> app)
    {
        NgApp appAnnotation = app.getAnnotation(NgApp.class);
        return appAnnotation.value();
    }

    public static File getAppPath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasAppDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName);
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseAppDirectories.put(appName, appBaseDirectory);

                buildFolderStructure(app);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppDirectories.get(appName);
    }

    public static File getAppMainTSPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasMainTSFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getFileReferenceSrcFile(app, "main.ts"));
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }
                baseAppMainTSFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppMainTSFiles.get(appName);
    }

    public static File getAppPackageJsonPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasPackageJsonFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/package.json");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseAppPackageJsonFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppPackageJsonFiles.get(appName);
    }

    public static File getAppNpmrcPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasNprmcFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/.npmrc");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }
                baseAppNpmrcFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppNpmrcFiles.get(appName);
    }

    public static File getAppTsConfigAppPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasTSConfigAppFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/tsconfig.app.json");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseAppTSConfigAppFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppTSConfigAppFiles.get(appName);
    }

    public static File getAppTsConfigPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasTSConfigFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/tsconfig.json");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseAppTSConfigFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppTSConfigFiles.get(appName);
    }

    public static File getGitIgnorePath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasTSConfigFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/.gitignore");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseGitIgnoreFile.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseGitIgnoreFile.get(appName);
    }

    public static File getAppPolyfillsPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasPolyfillsFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getFileReferenceSrcFile(app, "polyfills.ts"));
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                basePolyfillsJsonFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return basePolyfillsJsonFiles.get(appName);
    }

    public static File getAngularJsonPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasAngularJsonFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getAppPath(app) + "/angular.json");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseAngularJsonFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAngularJsonFiles.get(appName);
    }

    public static File getIndexHtmlPath(Class<? extends INgApp<?>> app, boolean createNew)
    {
        String appName = getAppName(app);
        if (!hasIndexHtmlFile(app))
        {
            try
            {
                File appBaseDirectory = new File(getFileReferenceSrcFile(app, "index.html"));
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                }
                if (createNew && appBaseDirectory.exists())
                {
                    appBaseDirectory.delete();
                    appBaseDirectory.createNewFile();
                }
                else if (!appBaseDirectory.exists())
                {
                    appBaseDirectory.createNewFile();
                }

                baseIndexHtmlFiles.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseIndexHtmlFiles.get(appName);
    }

    public static File getAppResourcePath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasAppResourceDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName + "/app/");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseAppResourceDirectories.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppResourceDirectories.get(appName);
    }

    public static File getDistPath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasDistDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName + "/dist/jwebmp/browser/");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseDistDirectories.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseDistDirectories.get(appName);
    }

    public static File getDistAssetsPath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasDistAssetsDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName + "/dist/jwebmp/browser/assets/");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseDistAssetsDirectories.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseDistAssetsDirectories.get(appName);
    }

    public static File getAppSrcPath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasAppSrcDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName + "/src/app/");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseAppSrcDirectories.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppSrcDirectories.get(appName);
    }

    public static File getAppAssetsPath(Class<? extends INgApp<?>> app)
    {
        String appName = getAppName(app);
        if (!hasAppAssetsDir(app))
        {
            try
            {
                File appBaseDirectory = new File(baseUserDirectory.getCanonicalPath() + "/" + appName + "/public/");
                if (!appBaseDirectory.exists())
                {
                    FileUtils.forceMkdirParent(appBaseDirectory);
                    FileUtils.forceMkdir(appBaseDirectory);
                }
                baseAppAssetsDirectories.put(appName, appBaseDirectory);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return baseAppAssetsDirectories.get(appName);
    }

    public static String getFileReferenceSrcAppFile(Class<? extends INgApp<?>> app, Class<?> clazz, String... extension)
    {
        String classLocationDirectory = getClassLocationDirectory(clazz);
        classLocationDirectory = classLocationDirectory.replaceAll("\\\\", "/")
                                                       .replace('.', '/');

        String baseLocation = null;
        try
        {
            baseLocation = getAppPath(app).getCanonicalPath();
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }

        baseLocation = baseLocation.replaceAll("\\\\", "/");
        baseLocation += "/src/app/";

        classLocationDirectory = baseLocation + classLocationDirectory + getTsFilename(clazz) + (extension.length > 0 ? extension[0] : "");
        //classLocationDirectory = classLocationDirectory.replace('.', '/');
        return classLocationDirectory;
    }

    public static String getFileReferenceSrcFile(Class<? extends INgApp<?>> app, String fileName)
    {
        String baseLocation = null;
        try
        {
            baseLocation = getAppPath(app).getCanonicalPath();
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }

        baseLocation = baseLocation.replaceAll("\\\\", "/");
        baseLocation += "/src/" + fileName;
        return baseLocation;
    }

    public static String getFileReferenceAppFile(Class<? extends INgApp<?>> app, String fileName)
    {
        String baseLocation = null;
        try
        {
            baseLocation = getAppPath(app).getCanonicalPath();
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }

        baseLocation = baseLocation.replaceAll("\\\\", "/");
        baseLocation += "/" + fileName;
        return baseLocation;
    }


    public static File getFile(Class<? extends INgApp<?>> app, Class<?> clazz, String... extension)
    {
        String baseDir = getFileReferenceSrcAppFile(app, clazz, extension);
        return new File(baseDir);
    }

    public static void saveAssetToBaseDir(Class<? extends INgApp<?>> app, InputStream inputStream, String fileName, boolean includeDist)
    {
        File appAssetsPath = getAppAssetsPath(app).getParentFile();
        byte[] data;
        try
        {
            data = IOUtils.toByteArray(inputStream);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }

        fileName = cleanAssetName(fileName);
        /*if (fileName.startsWith("/"))
        {
            fileName = fileName.substring(1);
        }
        if (fileName.startsWith("assets/"))
        {
            fileName = fileName.substring(fileName.indexOf("assets/") + 7);
        }*/
        String assetFilePath;
        try
        {
            assetFilePath = FilenameUtils.concat(appAssetsPath.getCanonicalPath(), fileName);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }
        writeFile(new ByteArrayInputStream(data), assetFilePath);
        if (!appAssets.containsKey(app))
        {
            appAssets.put(app, new ArrayList<>());
        }
        appAssets.get(app)
                 .add("public/" + fileName);

        if (includeDist)
        {
            try
            {
                assetFilePath = FilenameUtils.concat(getDistAssetsPath(app).getCanonicalPath(), fileName);
            }
            catch (IOException e)
            {
                throw new UnsupportedOperationException(e);
            }
            writeFile(new ByteArrayInputStream(data), assetFilePath);
        }
    }

    public static void saveAsset(Class<? extends INgApp<?>> app, InputStream inputStream, String fileName)
    {
        saveAsset(app, inputStream, fileName, false);
    }

    private static Map<Class<? extends INgApp<?>>, List<String>> appAssets = new HashMap<>();

    public static List<String> getAssetList(Class<? extends INgApp<?>> app)
    {
        return appAssets.get(app);
    }

    public static String cleanAssetName(String assetName)
    {
        if (assetName.contains("src/assets/src/assets/"))
        {
            assetName = assetName.replace("src/assets/src/assets/", "src/assets/");
        }
        if (assetName.startsWith("src/assets/"))
        {
            return assetName.replace("src/assets/", "");
        }
        else
        {
            return assetName;
        }
    }

    public static void saveAsset(Class<? extends INgApp<?>> app, InputStream inputStream, String fileName, boolean includeDist)
    {
        File appAssetsPath = getAppAssetsPath(app);
        byte[] data;
        try
        {
            data = IOUtils.toByteArray(inputStream);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }

        fileName = cleanAssetName(fileName);
        /*if (fileName.startsWith("/"))
        {
            fileName = fileName.substring(1);
        }
        if (fileName.startsWith("assets/"))
        {
            fileName = fileName.substring(fileName.indexOf("assets/") + 7);
        }*/
        String assetFilePath;
        try
        {
            assetFilePath = FilenameUtils.concat(appAssetsPath.getCanonicalPath(), fileName);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }
        writeFile(new ByteArrayInputStream(data), assetFilePath);
        if (!appAssets.containsKey(app))
        {
            appAssets.put(app, new ArrayList<>());
        }
        appAssets.get(app)
                 .add("public/" + fileName);

        if (includeDist)
        {
            try
            {
                assetFilePath = FilenameUtils.concat(getDistAssetsPath(app).getCanonicalPath(), fileName);
            }
            catch (IOException e)
            {
                throw new UnsupportedOperationException(e);
            }
            writeFile(new ByteArrayInputStream(data), assetFilePath);
        }
    }

    private static void writeFile(InputStream inputStream, String assetFilePath)
    {
        File assetFile = new File(assetFilePath);
        File parentFile = assetFile.getParentFile();
        if (!parentFile.exists())
        {
            try
            {
                FileUtils.forceMkdirParent(assetFile);
            }
            catch (IOException e)
            {
                throw new UnsupportedOperationException(e);
            }
        }
        try (FileOutputStream fos = new FileOutputStream(assetFile))
        {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            IOUtils.write(fileBytes, fos);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                log.fine("Failed to close input stream for file: " + assetFilePath);
            }
        }
    }

    public static void saveAppResourceFile(Class<? extends INgApp<?>> app, InputStream inputStream, String fileName)
    {
        File appAssetsPath = getAppResourcePath(app);
        byte[] data;
        try
        {
            data = IOUtils.toByteArray(inputStream);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }
        if (fileName.startsWith("/"))
        {
            fileName = fileName.substring(1);
        }
        if (fileName.startsWith("app/") || fileName.startsWith("app\\"))
        {
            fileName = fileName.substring(4);
        }
        String assetFilePath;
        try
        {
            assetFilePath = FilenameUtils.concat(appAssetsPath.getCanonicalPath(), fileName);
        }
        catch (IOException e)
        {
            throw new UnsupportedOperationException(e);
        }
        writeFile(new ByteArrayInputStream(data), assetFilePath);
    }


    public static Set<Class<?>> getAllValidClasses(ClassInfo a)
    {
        ScanResult scan = IGuiceContext.instance()
                                       .getScanResult();
        Set<Class<?>> classes = new HashSet<>();
        if (a.isInterface() || a.isAbstract())
        {
            for (ClassInfo subclass : !a.isInterface() ? scan.getSubclasses(a.loadClass()) : scan.getClassesImplementing(a.loadClass()))
            {
                if (!subclass.isAbstract() && !subclass.isInterface())
                {
                    classes.add(subclass.loadClass());
                }
            }
        }
        else
        {
            Class<?> aClass = a.loadClass();
            classes.add(aClass);
        }
        return classes;
    }
}
