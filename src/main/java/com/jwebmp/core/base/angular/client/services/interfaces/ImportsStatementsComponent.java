package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.spi.OnGetAllImports;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;

public interface ImportsStatementsComponent<J extends ImportsStatementsComponent<J>>
{
    String importString = "import { %s } from '%s';\n";
    String importPlainString = "import %s from '%s';\n";
    String importDirectString = "import '%s';\n";

    default List<NgComponentReference> getComponentReferences()
    {
        return new ArrayList<>();
    }

    default List<NgImportReference> getAllImportAnnotations()
    {
        List<NgImportReference> refs = new ArrayList<>();

        List<NgComponentReference> moduleRefs = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgComponentReference.class);
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            moduleRefs.addAll(comp.getConfigurations(NgComponentReference.class, false));
        }
        moduleRefs.addAll(getComponentReferences());
        for (NgComponentReference moduleRef : moduleRefs)
        {
            refs.addAll(putRelativeLinkInMap(getClass(), moduleRef));
        }
        List<NgDataTypeReference> dataTypeReferences = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgDataTypeReference.class);
        for (NgDataTypeReference moduleRef : dataTypeReferences)
        {
            refs.addAll(putRelativeLinkInMap(getClass(), getNgComponentReference(moduleRef.value())));
        }
        refs.addAll(IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgImportReference.class));
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            refs.addAll(comp.getConfigurations(NgImportReference.class, false));
        }

        Set<OnGetAllImports> interceptors = IGuiceContext.loaderToSet(ServiceLoader.load(OnGetAllImports.class));
        for (OnGetAllImports interceptor : interceptors)
        {
            interceptor.perform(refs, this);
        }

        return refs;
    }

    default List<NgImportReference> clean(List<NgImportReference> refs)
    {
        List<NgImportReference> workable = new ArrayList<>();
        Set<String> uniqueEntries = new HashSet<>();

        // Process all NgImportReferences, splitting values and ensuring uniqueness
        for (NgImportReference ref : refs)
        {
            // Split by commas in `value` and process each individual value
            String[] values = ref.value().split(",");
            for (String value : values)
            {
                String trimmedValue = value.trim();

                // Create a new NgImportReference for the split value
                NgImportReference importReference = AnnotationUtils.getNgImportReference(trimmedValue, ref.reference().trim());

                // Apply constraints (e.g., onSelf) and ensure filename exclusion
                if (!importReference.value().equals(getTsFilename(getClass())))
                {
                    // Ensure uniqueness by combining both `value` and `reference`
                    String uniqueKey = trimmedValue;// + "|" + importReference.reference();
                    if (uniqueEntries.add(uniqueKey))
                    {
                        workable.add(importReference);
                    }
                }
            }
        }
        return workable;
    }

    default Map<String, String> imports()
    {
        return imports(new File[]{});
    }

    default Map<String, String> imports(File... srcRelative)
    {
        Map<String, String> out = new java.util.HashMap<>(importSelf());
        return out;
    }

    default Map<String, String> importSelf(File... srcRelative)
    {
        Map<String, String> out = new java.util.HashMap<>();
        //	for (File file : srcRelative)
        //{
        out.putAll(Map.of(getTsFilename(getClass()),
                getClassLocationDirectory(getClass()) + getTsFilename(getClass()))
        );
        //}
        return out;
    }

    ThreadLocal<Map<Class<?>, File>> componentFileReference = ThreadLocal.withInitial(HashMap::new);

    default List<NgImportReference> putRelativeLinkInMap(Class<?> clazz, NgComponentReference moduleRef)
    {
        List<NgImportReference> refs = new ArrayList<>();
        var baseDir = IComponent.getCurrentAppFile();
        try
        {
            String canonicalPath = (baseDir.get()
                    .getCanonicalPath() + "/src/app/").replace('\\', '/');

            File me = new File(getFileReference(canonicalPath, clazz));
            /*File destination = new File(getFileReference(baseDir.get()
                    .getCanonicalPath(), moduleRef.value()));
*/
            String location = clazz.getCanonicalName().replace('.', '/');
            var f = new File(FilenameUtils.concat(canonicalPath, location));
            f.mkdirs();

            var destination = getFileReference(canonicalPath, moduleRef.value());
            String destinationLocation = FilenameUtils.concat(canonicalPath, destination
                    .replace('.', '/')
                    .replace('\\', '/'));
            destinationLocation = canonicalPath + destination.replace('.', '/').replace('\\', '/');
            var d = new File(destinationLocation);

            String importName = getTsFilename(moduleRef.value());
            String reference = getRelativePath(f, d, null);
            reference = reference.replace('\\', '/');
            //reference = removeFirstParentDirectoryAsString(reference);
            NgImportReference importReference = AnnotationUtils.getNgImportReference(importName, reference);
            refs.add(importReference);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return refs;
    }

    default List<NgImportReference> putRelativeLinkInMap(File sourceDirectory, Class<?> componentClass)
    {
        List<NgImportReference> refs = new ArrayList<>();
        var baseDir = IComponent.getCurrentAppFile();
        try
        {
            File me = sourceDirectory.isFile() ? sourceDirectory.getParentFile() : sourceDirectory;
            String canonicalPath = me.getCanonicalPath().replace('\\', '/');


            /*File destination = new File(getFileReference(baseDir.get()
                    .getCanonicalPath(), moduleRef.value()));
*/
            String location = "app/" + componentClass.getCanonicalName().replace('.', '/')
                    + "/" + getTsFilename(componentClass);
            var f = new File(FilenameUtils.concat(canonicalPath, location));
            f.mkdirs();

            String reference = getRelativePath(me, f, null);
            reference = reference.replace('\\', '/');
            reference = "./" + reference;
            String importName = getTsFilename(componentClass);
            NgImportReference importReference = AnnotationUtils.getNgImportReference(importName, reference);
            refs.add(importReference);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return refs;
    }


    default String getFileReference(String baseDirectory, Class<?> clazz, String... extension)
    {
        String classLocationDirectory = getClassLocationDirectory(clazz);
        classLocationDirectory = classLocationDirectory.replaceAll("\\\\", "/");
        String baseLocation = baseDirectory;
        //baseLocation = baseLocation.replaceAll("\\\\", "/");
        //  baseLocation += "/src/app/";
        classLocationDirectory = classLocationDirectory + getTsFilename(clazz) + (extension.length > 0 ? extension[0] : "");

        return classLocationDirectory;
    }


    static String getRelativePath(File absolutePath1, File absolutePath2, String extension)
    {
        return getRelativePath(absolutePath1.toPath(), absolutePath2.toPath(), extension);
    }

    static String getRelativePath(Path absolutePath1, Path absolutePath2, String extension)
    {
        return getRelativePath(absolutePath1.toString(), absolutePath2.toString());
    }

    static String getClassLocationDirectory(Class<?> clazz)
    {
        return IComponent.getClassDirectory(clazz) + "/" + getTsFilename(clazz) + "/";
    }

    /**
     * Get the relative path between two directories or files.
     * Does not require the files or directories to exist and supports shared parent directories.
     *
     * @param basePath   The path to start from.
     * @param targetPath The path to resolve relative to the base path.
     * @return The relative path as a string (e.g., "../UWEAppBoot").
     */
    static String getRelativePath(String basePath, String targetPath)
    {
        // Normalize the paths to clean redundant components (e.g., "../" or "./")
        Path base = Paths.get(basePath).normalize();
        Path target = Paths.get(targetPath).normalize();

        // Calculate the relative path between the base and target
        try
        {
            return base.relativize(target).toString();
        }
        catch (IllegalArgumentException e)
        {
            LogManager.getLogger("RelativePathRender").error("Failed to get relative path for " + basePath + " and " + targetPath + " with exception: " + e.getMessage());
            return "ERROR with " + basePath + " and " + targetPath;
        }

    }
}
