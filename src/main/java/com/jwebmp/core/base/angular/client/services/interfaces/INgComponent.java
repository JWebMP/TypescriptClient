package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentImportReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import com.jwebmp.core.databind.IConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.getAnnotations;
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
        List<NgGlobalComponentImportReference> annos = getAnnotations(getClass(), NgGlobalComponentImportReference.class);
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
        out.add("AfterContentInit");
        out.add("AfterContentChecked");
        out.add("AfterViewInit");
        out.add("AfterViewChecked");
        out.add("OnInit");
        out.add("OnDestroy");
        return out;
    }

    @Override
    default List<String> componentConstructorParameters()
    {
        List<String> out = IComponent.super.componentConstructorParameters();
        List<NgGlobalComponentConstructorParameter> annos = getAnnotations(getClass(), NgGlobalComponentConstructorParameter.class);
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

        List<NgOnInit> fInit = getAnnotations(getClass(), NgOnInit.class);
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
        List<NgOnDestroy> fInit = getAnnotations(getClass(), NgOnDestroy.class);
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
        List<NgAfterContentChecked> ngComponent = getAnnotations(getClass(), NgAfterContentChecked.class);
        ngComponent.sort(Comparator.comparingInt(NgAfterContentChecked::sortOrder));
        for (NgAfterContentChecked ngInput : ngComponent)
        {
            out.add(ngInput.value());
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

        List<NgInput> ngComponent = getAnnotations(getClass(), NgInput.class);
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
        NgComponent ngComponent = getAnnotations(getClass(), NgComponent.class).get(0);
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

        List<NgImportProvider> refs = getAnnotations(getClass(), NgImportProvider.class);
        for (NgImportProvider ref : refs)
        {
            providers.append(ref.value())
                     .append(",")
                     .append("\n");
        }
        List<NgComponentReference> compRefs = getAnnotations(getClass(), NgComponentReference.class);
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

        String componentString = String.format(INgComponent.componentString, selector, templateUrls, styles, styleUrls,
                "", //viewProviders
                "", //Animations
                providers, //Directive Providers,
                hosts //hosts entry
        );

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
        List<NgAfterViewInit> fInit = getAnnotations(getClass(), NgAfterViewInit.class);
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
        List<NgAfterViewChecked> fInit = getAnnotations(getClass(), NgAfterViewChecked.class);
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
        List<NgAfterContentInit> fInit = getAnnotations(getClass(), NgAfterContentInit.class);
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
        List<NgAfterContentChecked> fInit = getAnnotations(getClass(), NgAfterContentChecked.class);
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
}
