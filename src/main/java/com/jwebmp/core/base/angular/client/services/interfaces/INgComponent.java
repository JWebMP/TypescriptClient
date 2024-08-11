package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentImportReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.spi.OnGetAllModuleImports;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import com.jwebmp.core.databind.IConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;
import static java.nio.charset.StandardCharsets.UTF_8;

@NgImportReference(value = "Component", reference = "@angular/core")
@NgImportReference(value = "AfterViewInit", reference = "@angular/core")
@NgImportReference(value = "AfterViewChecked", reference = "@angular/core")
@NgImportReference(value = "AfterContentInit", reference = "@angular/core")
@NgImportReference(value = "AfterContentChecked", reference = "@angular/core")
@NgImportReference(value = "ChangeDetectorRef", reference = "@angular/core")
@NgImportReference(value = "OnInit", reference = "@angular/core")
@NgImportReference(value = "OnDestroy", reference = "@angular/core")

@NgImportReference(value = "ElementRef", reference = "@angular/core")
@NgImportReference(value = "Input", reference = "@angular/core")
@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "NgForOf", reference = "@angular/common")
@NgImportReference(value = "NgIf", reference = "@angular/common")


@NgImportReference(value = "Router", reference = "@angular/router")

@NgConstructorParameter("private cdref: ChangeDetectorRef")
@NgConstructorParameter("private elementRef: ElementRef")
@NgConstructorParameter("private router: Router")


public interface INgComponent<J extends INgComponent<J>>
        extends IComponent<J>, IConfiguration
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
            \tpreserveWhitespaces:true,
            \thost:%s,
            \timports:[%s],
            \tstandalone:%b
            })""";


    @Override
    default List<String> afterContentChecked()
    {
        List<String> out = IComponent.super.afterContentChecked();
        //out.add("this.cdref.detectChanges();");
        return out;
    }

    @Override
    default List<String> componentMethods()
    {
        List<String> out = IComponent.super.componentMethods();
        out.add("""
                        public routeMe(location : string, tabData? : object, browserData? : object, skipLocationChange : boolean = true, )
                            {
                                if (tabData) {
                                    Object.entries(tabData).forEach(([key, value]) => sessionStorage.setItem(key, value))
                                }
                                if (browserData) {
                                    Object.entries(browserData).forEach(([key, value]) => localStorage.setItem(key, value))
                                }
                                this.router.navigateByUrl(location);
                            }""");
        return out;
    }

    @Override
    default List<NgImportReference> getAllImportAnnotations()
    {
        List<NgImportReference> refs = IComponent.super.getAllImportAnnotations();
        List<NgGlobalComponentImportReference> annos = IGuiceContext.get(AnnotationHelper.class)
                                                                    .getAnnotationFromClass(getClass(), NgGlobalComponentImportReference.class);
        for (NgGlobalComponentImportReference anno : annos)
        {
            refs.add(AnnotationUtils.getNgImportReference(anno.value(), anno.reference()));
        }
        return refs;
    }

    @Override
    default List<String> componentInterfaces()
    {
        List<String> out = IComponent.super.componentInterfaces();

        List<NgAfterContentInit> fAfterContentInit = IGuiceContext.get(AnnotationHelper.class)
                                                                  .getAnnotationFromClass(getClass(), NgAfterContentInit.class);
        if (!(fAfterContentInit.isEmpty() && componentAfterContentInit().isEmpty() && afterContentInit().isEmpty()))
        {
            out.add("AfterContentInit");
        }
        List<NgAfterContentChecked> ngComponent = IGuiceContext.get(AnnotationHelper.class)
                                                               .getAnnotationFromClass(getClass(), NgAfterContentChecked.class);
        if (!(ngComponent.isEmpty() && componentAfterContentChecked().isEmpty() && afterContentChecked().isEmpty()))
        {
            out.add("AfterContentChecked");
        }

        List<NgAfterViewInit> fViewInit = IGuiceContext.get(AnnotationHelper.class)
                                                       .getAnnotationFromClass(getClass(), NgAfterViewInit.class);
        if (!(fViewInit.isEmpty() && afterViewInit().isEmpty() && componentAfterViewInit().isEmpty()))
        {
            out.add("AfterViewInit");
        }

        List<NgAfterViewChecked> fAfterViewVhecked = IGuiceContext.get(AnnotationHelper.class)
                                                                  .getAnnotationFromClass(getClass(), NgAfterViewChecked.class);
        if (!(fAfterViewVhecked.isEmpty() && componentAfterViewChecked().isEmpty() && afterViewChecked().isEmpty()))
        {
            out.add("AfterViewChecked");
        }

        List<NgOnInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                                            .getAnnotationFromClass(getClass(), NgOnInit.class);
        if (!(fInit.isEmpty() && componentOnInit().isEmpty() && onInit().isEmpty()))
        {
            out.add("OnInit");
        }
        List<NgOnDestroy> fDestroy = IGuiceContext.get(AnnotationHelper.class)
                                                  .getAnnotationFromClass(getClass(), NgOnDestroy.class);
        if (!(fDestroy.isEmpty() && componentOnDestroy().isEmpty() && onDestroy().isEmpty()))
        {
            out.add("OnDestroy");
        }
        return out;
    }

    @Override
    default List<String> componentConstructorParameters()
    {
        List<String> out = IComponent.super.componentConstructorParameters();
        List<NgGlobalComponentConstructorParameter> annos = IGuiceContext.get(AnnotationHelper.class)
                                                                         .getAnnotationFromClass(getClass(), NgGlobalComponentConstructorParameter.class);
        for (NgGlobalComponentConstructorParameter anno : annos)
        {
            out.add(anno.value());
        }
        return out;
    }


    @Override
    default String renderOnInitMethod()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderOnInitMethod());
        List<NgOnInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                                            .getAnnotationFromClass(getClass(), NgOnInit.class);
        if (fInit.isEmpty() && componentOnInit().isEmpty() && onInit().isEmpty())
        {
            return out.toString();
        }

        out.append("ngOnInit() {\n");
        for (String s : componentOnInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : onInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        fInit.sort(Comparator.comparingInt(NgOnInit::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgOnInit ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");

        out.append("}\n");
        return out.toString();
    }

    @Override
    default String renderOnDestroyMethod()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderOnDestroyMethod());
        List<NgOnDestroy> fInit = IGuiceContext.get(AnnotationHelper.class)
                                               .getAnnotationFromClass(getClass(), NgOnDestroy.class);
        if (fInit.isEmpty() && componentOnDestroy().isEmpty() && onDestroy().isEmpty())
        {
            return out.toString();
        }

        out.append("ngOnDestroy() {\n");
        for (String s : componentOnDestroy())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : onDestroy())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        fInit.sort(Comparator.comparingInt(NgOnDestroy::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgOnDestroy ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");
        out.append("}\n");
        return out.toString();
    }

    @Override
    default List<String> componentAfterContentChecked()
    {
        Set<String> out = new LinkedHashSet<>(IComponent.super.componentAfterContentChecked());
        List<NgAfterContentChecked> ngComponent = IGuiceContext.get(AnnotationHelper.class)
                                                               .getAnnotationFromClass(getClass(), NgAfterContentChecked.class);
        ngComponent.sort(Comparator.comparingInt(NgAfterContentChecked::sortOrder));
        for (NgAfterContentChecked ngInput : ngComponent)
        {
            out.add(ngInput.value());
        }
        for (String s : afterContentChecked())
        {
            out.add(s);
        }
        return new ArrayList<>(out);
    }

    @Override
    default List<String> componentFields()
    {
        List<String> list = IComponent.super.componentFields();
        if (list == null)
        {
            list = new ArrayList<>();
        }

        List<NgInput> ngComponent = IGuiceContext.get(AnnotationHelper.class)
                                                 .getAnnotationFromClass(getClass(), NgInput.class);
        ngComponent.sort(Comparator.comparingInt(NgInput::sortOrder));
        for (NgInput ngInput : ngComponent)
        {
            list.add("\t@Input(\"" + ngInput.value() + "\") " + ngInput.value() + "? :" + (getTsFilename(ngInput.type())) + ";");
        }

        return list;
    }


    default List<String> componentDecorators()
    {
        List<String> list = IComponent.super.componentDecorators();
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
            list.add("@Injectable ({" +
                             "  providedIn:" +
                             (ngComponent.providedIn()
                                         .startsWith("!") ? "" : "'") +
                             ngComponent.providedIn() +
                             (ngComponent.providedIn()
                                         .startsWith("!") ? "" : "'") +
                             "})");
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
        selector.append(ngComponent.value());

        StringBuilder templateUrls = new StringBuilder();
        String templateHtml = chb.toString(0);

        templateUrls.append("./")
                    .append(getTsFilename(getClass()))
                    .append(".html");
        File htmlFile = getFile(getClass(), ".html");
        try
        {
            FileUtils.writeStringToFile(htmlFile, templateHtml, UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        styleUrls.append("'./")
                 .append(getTsFilename(getClass()))
                 .append(".css")
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
        File cssFile = getFile(getClass(), ".css");
        try
        {
            FileUtils.writeStringToFile(cssFile, cssString.toString(), UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        providers()
                .forEach((key) -> {
                    providers.append(key)
                             .append(",")
                             .append("\n");
                });

        List<NgImportProvider> refs = IGuiceContext.get(AnnotationHelper.class)
                                                   .getAnnotationFromClass(getClass(), NgImportProvider.class);
        for (NgImportProvider ref : refs)
        {
            providers.append(ref.value())
                     .append(",")
                     .append("\n");
        }
        List<NgComponentReference> compRefs = IGuiceContext.get(AnnotationHelper.class)
                                                           .getAnnotationFromClass(getClass(), NgComponentReference.class);
        for (NgComponentReference compRef : compRefs)
        {
            if (compRef.provides())
            {
                providers.append(getTsFilename(compRef.value()))
                         .append(",")
                         .append("\n");
            }
        }

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
            List<NgImportModule> importModules = IGuiceContext.get(AnnotationHelper.class)
                                                              .getAnnotationFromClass(getClass(), NgImportModule.class);
            for (NgImportModule compRef : importModules)
            {
                if (compRef.onSelf())
                {
                    importsModules.append(compRef.value())
                                  .append(",\n");
                }
            }
            for (String customImportModule : importModules())
            {
                importsModules.append(customImportModule)
                              .append(",\n");
            }

            if (importsModules.length() > 1)
            {
                importsModules.deleteCharAt(importsModules.length() - 2);
            }
        }

        String componentString;
        if (!standalone)
        {
            componentString = String.format(INgComponent.componentString, selector, templateUrls, styles, styleUrls,
                                            "", //viewProviders
                                            "", //Animations
                                            providers, //Directive Providers,
                                            hosts //hosts entry
            );
        }
        else
        {
            componentString = String.format(INgComponent.componentStandaloneString, selector, templateUrls, styles, styleUrls,
                                            "", //viewProviders
                                            "", //Animations
                                            providers, //Directive Providers,
                                            hosts, //hosts entry
                                            importsModules,
                                            standalone
            );
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


    @Override
    default String renderAfterViewInit()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderAfterViewInit());
        List<NgAfterViewInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                                                   .getAnnotationFromClass(getClass(), NgAfterViewInit.class);
        if (fInit.isEmpty() && componentAfterViewInit().isEmpty() && afterViewInit().isEmpty())
        {
            return out.toString();
        }

        out.append("ngAfterViewInit() {\n");
        for (String s : componentAfterViewInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : afterViewInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        fInit.sort(Comparator.comparingInt(NgAfterViewInit::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgAfterViewInit ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");

        out.append("}\n");
        return out.toString();
    }

    @Override
    default String renderAfterViewChecked()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderAfterViewChecked());
        List<NgAfterViewChecked> fInit = IGuiceContext.get(AnnotationHelper.class)
                                                      .getAnnotationFromClass(getClass(), NgAfterViewChecked.class);
        if (fInit.isEmpty() && componentAfterViewChecked().isEmpty() && afterViewChecked().isEmpty())
        {
            return out.toString();
        }

        out.append("ngAfterViewChecked() {\n");
        for (String s : componentAfterViewChecked())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : afterViewChecked())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        fInit.sort(Comparator.comparingInt(NgAfterViewChecked::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgAfterViewChecked ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");

        out.append("}\n");
        return out.toString();
    }

    @Override
    default String renderAfterContentInit()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderAfterContentInit());
        List<NgAfterContentInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                                                      .getAnnotationFromClass(getClass(), NgAfterContentInit.class);
        if (fInit.isEmpty() && componentAfterContentInit().isEmpty() && afterContentInit().isEmpty())
        {
            return out.toString();
        }

        out.append("ngAfterContentInit() {\n");
        for (String s : componentAfterContentInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : afterContentInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        fInit.sort(Comparator.comparingInt(NgAfterContentInit::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgAfterContentInit ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");
        out.append("}\n");
        return out.toString();
    }

    @Override
    default String renderAfterContentChecked()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderAfterContentChecked());
        List<NgAfterContentChecked> fInit = IGuiceContext.get(AnnotationHelper.class)
                                                         .getAnnotationFromClass(getClass(), NgAfterContentChecked.class);
        if (fInit.isEmpty() && componentAfterContentChecked().isEmpty() && afterContentChecked().isEmpty())
        {
            return out.toString();
        }

        out.append("ngAfterContentChecked() {\n");
        for (String s : componentAfterContentChecked())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        for (String s : afterContentChecked())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        fInit.sort(Comparator.comparingInt(NgAfterContentChecked::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgAfterContentChecked ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");
        out.append("}\n");

        return out.toString();
    }

    default J routeTo(String location, Map<String, String> tabData, Map<String, String> browserData)
    {

        return (J) this;
    }

    default Boolean standaloneOverride()
    {
        return null;
    }

    default Set<String> importModules()
    {
        List<String> list = new ArrayList<>();
        ServiceLoader<OnGetAllModuleImports> load = ServiceLoader.load(OnGetAllModuleImports.class);
        for (OnGetAllModuleImports onGetAllModuleImports : load)
        {
            onGetAllModuleImports.perform(list, this);
        }
        list.add("NgForOf");
        list.add("NgIf");
        return new HashSet<>(list);
    }
}
