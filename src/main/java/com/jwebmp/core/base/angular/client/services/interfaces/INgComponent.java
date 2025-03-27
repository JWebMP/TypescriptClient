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
import com.jwebmp.core.base.angular.client.services.ComponentConfiguration;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;
import static java.nio.charset.StandardCharsets.UTF_8;

@NgImportReference(value = "Component", reference = "@angular/core")
@NgImportReference(value = "CUSTOM_ELEMENTS_SCHEMA", reference = "@angular/core")
//@NgImportReference(value = "Injectable", reference = "@angular/core")
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


public interface INgComponent<J extends INgComponent<J> & IComponentHierarchyBase<?, J>> extends IComponent<J>
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

    default List<String> decorators()
    {
        List<String> list = IComponent.super.decorators();
        if (list == null)
        {
            list = new ArrayList<>();
        }
        if (!getClass().isAnnotationPresent(NgComponent.class))
        {
            LogManager.getLogger("INgComponent").warn("This one doesn't have a ng component");
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
            me().addConfiguration(AnnotationUtils.getNgImportReference("Injectable", "@angular/core"));

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
        templateUrls.append("./")
                .append(getTsFilename(getClass()))
                .append(".html");

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

/*
        StringBuilder cssString = chb.cast()
                .asStyleBase()
                .renderCss(1);
*/

        //CSSComposer cssComposer = new CSSComposer();
        // cssComposer.addComponent(chb);
        //styles.append("\"" + cssComposer.toString() + "\"");
      /*  File cssFile = getFile(getClass(), ".scss");
        try
        {
            FileUtils.writeStringToFile(cssFile, cssString.toString(), UTF_8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }*/
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
        }
        else
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
            importsModules = renderImportModules();
            /*List<NgImportModule> importModules = new ArrayList<>();// IGuiceContext.get(AnnotationHelper.class)
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

            *//*
            for (String customImportModule : moduleImports())
            {
                importsModules.append(customImportModule)
                              .append(",\n");
            }*//*

            if (importsModules.length() > 1)
            {
                importsModules.deleteCharAt(importsModules.length() - 2);
            }*/
        }

        String componentString;
        if (!standalone)
        {
            componentString = String.format(INgComponent.componentString, selector, templateUrls, styles, styleUrls, "", //viewProviders
                    "", //Animations
                    providers, //Directive Providers,
                    hosts //hosts entry
            );
        }
        else
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
        return out;
    }

    @Override
    default StringBuilder renderMethods()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            StringBuilder sb = new StringBuilder();
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            sb.append(config.renderOnInit());
            sb.append(config.renderAfterViewInit());
            sb.append(config.renderAfterContentInit());
            sb.append(config.renderAfterViewChecked());
            sb.append(config.renderAfterContentChecked());
            sb.append(config.renderMethods());
            sb.append(config.renderOnDestroy());
            return sb;
        }
        return new StringBuilder();
    }

    @Override
    default List<String> componentMethods()
    {
        return new ArrayList<>();
    }

    @Override
    default StringBuilder renderFields()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            StringBuilder sb = new StringBuilder();
            sb.append(config.renderInjects());
            sb.append(config.renderModals());
            sb.append(config.renderSignals());
            sb.append(config.renderFields());
            return sb;
        }
        return new StringBuilder();
    }

    @Override
    default StringBuilder renderConstructorParameters()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            return config.renderConstructorParameters();
        }
        return new StringBuilder();
    }

    @Override
    default StringBuilder renderInterfaces()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            return config.renderInterfaces();
        }
        return new StringBuilder();
    }

    @Override
    default StringBuilder renderImports()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            return config.renderImportStatements();
        }
        return new StringBuilder();
    }

    default StringBuilder renderImportModules()
    {
        J me = (J) this;
        if (getClass().getCanonicalName().contains("ComponentRenderingTest"))
        {
            System.out.println("....");
        }
        if (me.asBase().getProperties().containsKey("AngularConfiguration"))
        {
            ComponentConfiguration config = (ComponentConfiguration) me.asBase().getProperties().get("AngularConfiguration");
            return config.renderImportModules();
        }
        return new StringBuilder();
    }


}
