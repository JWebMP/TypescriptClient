package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentImportReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;
import static java.nio.charset.StandardCharsets.UTF_8;

@NgImportReference(value = "Component", reference = "@angular/core")
@NgImportReference(value = "CUSTOM_ELEMENTS_SCHEMA", reference = "@angular/core")
//@NgImportReference(value = "AfterViewInit", reference = "@angular/core")
//@NgImportReference(value = "AfterViewChecked", reference = "@angular/core")
//@NgImportReference(value = "AfterContentInit", reference = "@angular/core")
//@NgImportReference(value = "AfterContentChecked", reference = "@angular/core")
//@NgImportReference(value = "ChangeDetectorRef", reference = "@angular/core")

//@NgImportReference(value = "OnInit", reference = "@angular/core")
//@NgImportReference(value = "OnDestroy", reference = "@angular/core")

//@NgImportReference(value = "ElementRef", reference = "@angular/core")
//@NgImportReference(value = "Input", reference = "@angular/core")
//@NgImportReference(value = "Injectable", reference = "@angular/core")

//@NgImportReference(value = "NgForOf", reference = "@angular/common")
//@NgImportReference(value = "NgIf", reference = "@angular/common")
//@NgImportReference(value = "JsonPipe", reference = "@angular/common")


//@NgImportReference(value = "Router", reference = "@angular/router")

//@NgConstructorParameter("private cdref: ChangeDetectorRef")
//@NgConstructorParameter("private elementRef: ElementRef")
//@NgConstructorParameter("private router: Router")


public interface INgComponent<J extends INgComponent<J>> extends IComponent<J>
{
    String componentString = """
            @Component({
            \tselector:'%s',
            \ttemplateUrl:'%s',
            \tstyles: [%s],
            \tstyleUrls:[%s],
            \tviewProviders:[%s],
            \tanimations:[%s],
            \tproviders:[%s],
            \tpreserveWhitespaces:true,
            \thost:%s
            })""";

    String componentStandaloneString = """
            @Component({
            \tselector:'%s',
            \ttemplateUrl:'%s',
            \tstyles: [%s],
            \tstyleUrls:[%s],
            \tviewProviders:[%s],
            \tanimations:[%s],
            \tproviders:[%s],
            \tschemas:[%s],
            \tpreserveWhitespaces:true,
            \thost:%s,
            \timports:[%s],
            \tstandalone:%b
            })""";

    @Override
    default List<NgConstructorBody> getAllConstructorBodies()
    {
        List<NgConstructorBody> out = IComponent.super.getAllConstructorBodies();
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            out.addAll(comp.getConfigurations(NgConstructorBody.class, false));
        }
        return out;
        //return IComponent.super.getAllConstructorBodies();
    }

    @Override
    default List<NgConstructorParameter> getAllConstructorParameters()
    {
        List<NgConstructorParameter> out = IComponent.super.getAllConstructorParameters();
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            if (getClass().getCanonicalName().contains("TasksCreateModal"))
            {
                System.out.println("here");
            }
            out.addAll(comp.getConfigurations(NgConstructorParameter.class, false));
        }
        return out;
        //return IComponent.super.getAllConstructorParameters();
    }

    @Override
    default List<NgImportReference> getAllImportAnnotations()
    {
        List<NgImportReference> refs = IComponent.super.getAllImportAnnotations();
        List<NgGlobalComponentImportReference> annos = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgGlobalComponentImportReference.class);
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            var configs = comp.getConfigurations(NgImportReference.class, false);
            refs.addAll(configs);
        }
        for (NgGlobalComponentImportReference anno : annos)
        {
            refs.add(AnnotationUtils.getNgImportReference(anno.value(), anno.reference()));
        }
        return refs;
    }


    @Override
    default List<String> interfaces()
    {
        List<String> out = IComponent.super.interfaces();

        List<NgAfterContentInit> fAfterContentInit = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgAfterContentInit.class);
        if (!(fAfterContentInit.isEmpty() && afterContentInit().isEmpty()))
        {
            out.add("AfterContentInit");
        }
        List<NgAfterContentChecked> ngComponent = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgAfterContentChecked.class);
        if (!(ngComponent.isEmpty() && afterContentChecked().isEmpty()))
        {
            out.add("AfterContentChecked");
        }

        List<NgAfterViewInit> fViewInit = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgAfterViewInit.class);
        if (!(fViewInit.isEmpty() && afterViewInit().isEmpty()))
        {
            out.add("AfterViewInit");
        }

        List<NgAfterViewChecked> fAfterViewVhecked = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgAfterViewChecked.class);
        if (!(fAfterViewVhecked.isEmpty() && afterViewChecked().isEmpty()))
        {
            out.add("AfterViewChecked");
        }

        List<NgOnInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgOnInit.class);
        if (!(fInit.isEmpty() && onInit().isEmpty()))
        {
            out.add("OnInit");
        }
        List<NgOnDestroy> fDestroy = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgOnDestroy.class);
        if (!(fDestroy.isEmpty() && onDestroy().isEmpty()))
        {
            out.add("OnDestroy");
        }
        return out;
    }


    @Override
    default List<NgField> getAllFields()
    {
        List<NgField> list = IComponent.super.getAllFields();
        if (this instanceof IComponentHierarchyBase<?, ?> comp)
        {
            list.addAll(comp.getConfigurations(NgField.class, false));
        }
        return list;
    }

    default List<String> decorators()
    {
        List<String> list = IComponent.super.decorators();
        if (list == null)
        {
            list = new ArrayList<>();
        }
        if (!getClass().isAnnotationPresent(NgComponent.class))
        {
            System.out.println("This one doesn't have a ng component");
            return list;
        }
        NgComponent ngComponent = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgComponent.class)
                .get(0);
        if (!Strings.isNullOrEmpty(getClass().getAnnotation(NgComponent.class)
                .providedIn()))
        {
            list.add("@Injectable ({" + "  providedIn:" + (ngComponent.providedIn()
                    .startsWith("!") ? "" : "'") + ngComponent.providedIn() + (ngComponent.providedIn()
                    .startsWith("!") ? "" : "'") + "})");
            if (this instanceof IComponentHierarchyBase<?, ?> componentHierarchyBase)
            {
                componentHierarchyBase.addConfiguration(AnnotationUtils.getNgImportReference("Injectable", "@angular/core"));
            }
        }

        StringBuilder selector = new StringBuilder();
        StringBuilder template = new StringBuilder();
        StringBuilder styles = new StringBuilder();
        StringBuilder styleUrls = new StringBuilder();
        StringBuilder viewProviders = new StringBuilder();
        StringBuilder animations = new StringBuilder();
        StringBuilder providers = new StringBuilder();
        StringBuilder hosts = new StringBuilder();

        StringBuilder importsModules = new StringBuilder();
        boolean standalone = false;

        IComponentHierarchyBase<?, ?> chb = (IComponentHierarchyBase<?, ?>) this;
        chb.asTagBase()
                .setRenderTag(true);
        selector.append(ngComponent.value());

        StringBuilder templateUrls = new StringBuilder();
        String templateHtml = chb.toString(0);
        if (Strings.isNullOrEmpty(templateHtml))
        {
            Logger.getLogger("INgComponent")
                    .severe("Empty Template HTML Generated - " + getClass().getCanonicalName());
        }

        templateUrls.append("./")
                .append(getTsFilename(getClass()))
                .append(".html");
        File htmlFile = getFile(getClass(), ".html");
        try
        {
            FileUtils.writeStringToFile(htmlFile, templateHtml, UTF_8);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        styleUrls.append("'./")
                .append(getTsFilename(getClass()))
                .append(".scss")
                .append("',\n");
        for (String styleUrl : styleUrls())
        {
            styleUrls.append("'")
                    .append(styleUrl)
                    .append("',\n");
        }
        if (styleUrls.length() > 0)
        {
            styleUrls.deleteCharAt(styleUrls.length() - 2);
        }

        for (String style : styles())
        {
            styles.append("`")
                    .append(style)
                    .append("`,\n");
        }
        if (styles.length() > 0)
        {
            styles.deleteCharAt(styles.length() - 2);
        }

        StringBuilder cssString = chb.cast()
                .asStyleBase()
                .renderCss(1);

        //CSSComposer cssComposer = new CSSComposer();
        // cssComposer.addComponent(chb);
        //styles.append("\"" + cssComposer.toString() + "\"");
        File cssFile = getFile(getClass(), ".scss");
        try
        {
            FileUtils.writeStringToFile(cssFile, cssString.toString(), UTF_8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
/*

        providers().forEach((key) -> {
            providers.append(key)
                     .append(",")
                     .append("\n");
        });
*/

        if (this instanceof IComponentHierarchyBase<?, ?> componentHierarchyBase)
        {
            var refs = componentHierarchyBase.getConfigurations(NgImportProvider.class, false);
            for (var ref : refs.stream()
                    .map(NgImportProvider::value)
                    .distinct()
                    .toList())
            {
                providers.append(ref)
                        .append(",")
                        .append("\n");
            }
        }

        /*List<NgComponentReference> compRefs = IGuiceContext.get(AnnotationHelper.class)
                                                           .getAnnotationFromClass(getClass(), NgComponentReference.class);
        for (NgComponentReference compRef : compRefs)
        {
            if (compRef.provides())
            {
                providers.append(getTsFilename(compRef.value()))
                         .append(",")
                         .append("\n");
            }
        }*/

        if (providers.length() > 1)
        {
            providers.deleteCharAt(providers.length() - 2);
        }

        if (!host().isEmpty())
        {
            for (String s : host())
            {
                hosts.append(s);
            }
        } else
        {
            hosts.append("{}");
        }
        standalone = ngComponent.standalone();
        var override = standaloneOverride();
        if (override != null)
        {
            standalone = override;
        }

        if (standalone)
        {
            List<NgImportModule> importModules = new ArrayList<>();// IGuiceContext.get(AnnotationHelper.class)
            //             .getAnnotationFromClass(getClass(), NgImportModule.class);
            if (this instanceof IComponentHierarchyBase<?, ?> comp)
            {
                importModules.addAll(comp.getConfigurations(NgImportModule.class, false));
            }
            for (var compRef : importModules.stream()
                    .filter(a -> a.onSelf())
                    .map(NgImportModule::value)
                    .distinct()
                    .toList())
            {
                importsModules.append(compRef)
                        .append(",\n");
            }

            /*
            for (String customImportModule : moduleImports())
            {
                importsModules.append(customImportModule)
                              .append(",\n");
            }*/

            if (importsModules.length() > 1)
            {
                importsModules.deleteCharAt(importsModules.length() - 2);
            }
        }

        String componentString;
        if (!standalone)
        {
            componentString = String.format(INgComponent.componentString, selector, templateUrls, styles, styleUrls, "", //viewProviders
                    "", //Animations
                    providers, //Directive Providers,
                    hosts //hosts entry
            );
        } else
        {

            componentString = String.format(INgComponent.componentStandaloneString, selector, templateUrls, styles, styleUrls, "", //viewProviders
                    "", //Animations
                    providers, //Directive Providers,
                    "CUSTOM_ELEMENTS_SCHEMA", //schemas
                    hosts, //hosts entry
                    importsModules,
                    standalone);
        }

        list.add(componentString);
        return list;
    }

    default List<String> styleUrls()
    {
        return new ArrayList<>();
    }

    default List<String> styles()
    {
        return new ArrayList<>();
    }

    default List<String> animations()
    {
        return new ArrayList<>();
    }

    default List<String> providers()
    {
        return new ArrayList<>();
    }

    default List<String> inputs()
    {
        return new ArrayList<>();
    }

    default List<String> outputs()
    {
        return new ArrayList<>();
    }

    default List<String> host()
    {
        return new ArrayList<>();
    }

    default File getFile(Class<?> clazz, String... extension)
    {
        String baseDir = getFileReference(IComponent.getCurrentAppFile()
                .get()
                .getPath(), clazz, extension);
        File file = new File(baseDir);
        return file;
    }

    default J routeTo(String location, Map<String, String> tabData, Map<String, String> browserData)
    {

        return (J) this;
    }

    default Boolean standaloneOverride()
    {
        return null;
    }

    default Set<String> moduleImports()
    {
        Set<String> list = IComponent.super.moduleImports();
/*        list.add("NgForOf");
        list.add("NgIf");
        list.add("JsonPipe");*/
        return list;
    }

    // ================= Component Overrides ====================

    /**
     * @return Takes all the possible method combinations and joins them into actual NgMethods
     */
    @Override
    default List<NgMethod> renderAllMethods()
    {
        List<NgMethod> out = new ArrayList<>();
        if (this instanceof IComponentHierarchyBase<?, ?> comp && getClass().isAnnotationPresent(NgComponent.class))
        {
            Set<NgAfterViewInit> ngAfterViewInits = comp.getConfigurations(NgAfterViewInit.class, false);
            if (!ngAfterViewInits.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngAfterViewInit(){\n");
                for (var s : ngAfterViewInits.stream()
                        .sorted(Comparator.comparingInt(NgAfterViewInit::sortOrder))
                        .map(NgAfterViewInit::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }
            var ngAfterViewChecked = comp.getConfigurations(NgAfterViewChecked.class, false);
            if (!ngAfterViewChecked.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngAfterViewChecked(){\n");
                for (var s : ngAfterViewChecked.stream()
                        .map(NgAfterViewChecked::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }
            var ngAfterContentInit = comp.getConfigurations(NgAfterContentInit.class, false);
            if (!ngAfterContentInit.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngAfterContentInit(){\n");
                for (var s : comp.getConfigurations(NgAfterContentInit.class, false)
                        .stream()
                        .map(NgAfterContentInit::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }
            var ngAfterContentChecked = comp.getConfigurations(NgAfterContentInit.class, false);
            if (!ngAfterContentChecked.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngAfterContentChecked(){\n");
                for (var s : comp.getConfigurations(NgAfterContentChecked.class, false)
                        .stream()
                        .map(NgAfterContentChecked::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }

            var ngOnInit = comp.getConfigurations(NgOnInit.class, false);
            if (!ngOnInit.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngOnInit(){\n");
                for (var s : comp.getConfigurations(NgOnInit.class, false)
                        .stream()
                        .map(NgOnInit::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }

            var ngOnDestroy = comp.getConfigurations(NgOnDestroy.class, false);
            if (!ngOnDestroy.isEmpty())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\tngOnDestroy(){\n");
                for (var s : comp.getConfigurations(NgOnDestroy.class, false)
                        .stream()
                        .map(NgOnDestroy::value)
                        .distinct()
                        .toList())
                {
                    sb.append("\t\t")
                            .append(s)
                            .append("\n");
                }
                sb.append("\t}\n");
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }

            for (var s : comp.getConfigurations(NgMethod.class, false)
                    .stream()
                    .map(NgMethod::value)
                    .distinct()
                    .toList())
            {
                var ss = s;
                StringBuilder sb = new StringBuilder();
                ss.lines()
                        .forEach(a -> {
                            sb.append("\t")
                                    .append(a)
                                    .append("\n");
                        });
                out.add(AnnotationUtils.getNgMethod(sb.toString()));
            }
            //   out.addAll(comp.getConfigurations(NgMethod.class, false));
        }
        return out;
    }

    @Override
    default StringBuilder renderMethods()
    {
        StringBuilder sb = new StringBuilder();
        renderAllMethods().stream()
                .distinct()
                .map(NgMethod::value)
                .distinct()
                .forEach(sb::append);
        return sb;
    }

}
