package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.spi.OnGetAllImports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
        Set<String> names = new HashSet<>();
        for (NgImportReference ref : refs)
        {
            if (ref.value()
                   .contains(","))
            {
                for (String name : ref.value()
                                      .split(","))
                {
                    NgImportReference importReference = AnnotationUtils.getNgImportReference(name.trim(), ref.reference()
                                                                                                             .trim());
                    workable.add(importReference);
                }

            }
            else
            {
                workable.add(ref);
            }
        }
        workable.removeIf(a -> !a.onSelf());
        workable.removeIf(a -> a.value()
                                .equals(getTsFilename(getClass())));
        List<NgImportReference> cleanedRefs = new ArrayList<>();
        for (NgImportReference importReference : workable)
        {
            if (names.contains(importReference.value()
                                              .trim()))
            {
                continue;
            }
            names.add(importReference.value()
                                     .trim());
            cleanedRefs.add(importReference);
        }
        return cleanedRefs;
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

    default List<NgImportReference> putRelativeLinkInMap(Class<?> clazz, NgComponentReference moduleRef)
    {
        List<NgImportReference> refs = new ArrayList<>();
        var baseDir = IComponent.getCurrentAppFile();
        try
        {
            File me = new File(getFileReference(baseDir.get()
                                                       .getCanonicalPath(), clazz));
            File destination = new File(getFileReference(baseDir.get()
                                                                .getCanonicalPath(), moduleRef.value()));
            String importName = getTsFilename(moduleRef.value());
            String reference = getRelativePath(me, destination, null);
            if (moduleRef.value()
                         .getSimpleName()
                         .contains("PackInstructionsListProvider"))
            {
                //   System.out.println("SOMETHING HERE");
            }
            NgImportReference importReference = AnnotationUtils.getNgImportReference(importName, reference);
            refs.add(importReference);
            //out.putIfAbsent(getTsFilename(moduleRef.value()), getRelativePath(me, destination, null));
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
        baseLocation.replaceAll("\\\\", "/");
        baseLocation += "/src/app/";
        classLocationDirectory = baseLocation + classLocationDirectory + getTsFilename(clazz) + (extension.length > 0 ? extension[0] : "");

        return classLocationDirectory;
    }


    static String getRelativePath(File absolutePath1, File absolutePath2, String extension)
    {
        return getRelativePath(absolutePath1.toPath(), absolutePath2.toPath(), extension);
    }

    static String getRelativePath(Path absolutePath1, Path absolutePath2, String extension)
    {
        //get the directories of each to compare them
        File original = new File(absolutePath1.toString());
        if (original.isFile())
        {
            original = original.getParentFile();
        }
        File requestedForPath = new File(absolutePath2.toString());
        File requestedForFile = new File(absolutePath2.toString() + ".ts");
        if (requestedForPath.isFile())
        {
            //    requestedForPath = requestedForPath.getParentFile();
        }
        if (absolutePath2.toString()
                         .contains("!"))
        {
            String result = absolutePath2.toString()
                                         .substring(absolutePath2.toString()
                                                                 .indexOf('!') + 1);
            return result.replace('\\', '/');
        }

        try
        {
            if (!original.isDirectory())
            {
                original = original.getParentFile();
            }
            String path = original.toPath()
                                  .relativize(requestedForPath.toPath())
                                  .toString()
                                  .replaceAll("\\\\", "/");
            if (!path.startsWith("..") && !path.startsWith("./") && !path.startsWith("/"))
            {
                path = "./" + path;
            }
            return path;
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
        try
        {
            requestedForPath = absolutePath2.toFile();
            return requestedForPath.getCanonicalPath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    static String getClassLocationDirectory(Class<?> clazz)
    {
        return IComponent.getClassDirectory(clazz) + "/" + getTsFilename(clazz) + "/";
    }
}
