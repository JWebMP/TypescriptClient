package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgApp;
import com.jwebmp.core.base.angular.client.annotations.typescript.NgSourceDirectoryReference;
import com.jwebmp.core.base.html.interfaces.children.PageChildren;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import com.jwebmp.core.services.IPage;

import java.util.List;

import static com.jwebmp.core.base.angular.client.annotations.typescript.NgSourceDirectoryReference.SourceDirectories.Main;

@NgSourceDirectoryReference(Main)
public interface INgApp<J extends INgApp<J> & IComponentHierarchyBase<PageChildren, J>> extends IComponent<J>, IPage<J>
{
    default NgApp getAnnotation()
    {
        return getClass().getAnnotation(NgApp.class);
    }

    default String name()
    {
        return getAnnotation().value();
    }

    /**
     * The name of the .ts file for this app
     *
     * @return
     */
    default List<String> assets()
    {
        return List.of();
    }


    /**
     * The name of the .ts file for this app
     *
     * @return
     */
    default List<String> stylesheets()
    {
        return List.of();
    }

    /**
     * The name of the .ts file for this app
     *
     * @return
     */
    default List<String> scripts()
    {
        return List.of();
    }

    /**
     * Include the packages to render
     *
     * @return
     */
    default List<String> includePackages()
    {
        return List.of(getClass().getPackageName());
    }

    List<IComponentHierarchyBase<?, ?>> getRoutes();

}
