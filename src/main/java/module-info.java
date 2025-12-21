import com.guicedee.client.services.lifecycle.IGuiceConfigurator;
import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.guicedee.client.services.lifecycle.IGuicePostStartup;
import com.guicedee.client.services.config.IGuiceScanModuleInclusions;
import com.jwebmp.core.base.angular.client.implementations.AngularClientModule;
import com.jwebmp.core.base.angular.client.implementations.AngularTypeScriptClientModuleInclusion;
import com.jwebmp.core.base.angular.client.implementations.AngularTypeScriptPostStartup;
import com.jwebmp.core.base.angular.client.implementations.GuicedConfig;
import com.jwebmp.core.base.angular.client.services.spi.*;

module com.jwebmp.core.base.angular.client {
    requires transitive com.guicedee.client;
    requires transitive com.jwebmp.client;
    //requires transitive com.jwebmp.core;
    //requires org.apache.commons.io;

    requires transitive com.guicedee.guicedinjection;

    requires static lombok;
    requires transitive com.guicedee.jsonrepresentation;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.commons.io;
    requires transitive jakarta.validation;
    requires transitive org.apache.logging.log4j.core;

    exports com.jwebmp.core.base.angular.client.annotations.angular;
    exports com.jwebmp.core.base.angular.client.annotations.components;
    exports com.jwebmp.core.base.angular.client.annotations.functions;
    exports com.jwebmp.core.base.angular.client.annotations.globals;
    exports com.jwebmp.core.base.angular.client.annotations.references;
    exports com.jwebmp.core.base.angular.client.annotations.constructors;
    exports com.jwebmp.core.base.angular.client.annotations.angularconfig;
    exports com.jwebmp.core.base.angular.client.annotations.structures;
    exports com.jwebmp.core.base.angular.client.annotations.routing;
    exports com.jwebmp.core.base.angular.client.annotations.typescript;

    exports com.jwebmp.core.base.angular.client;
    exports com.jwebmp.core.base.angular.client.services.spi;

    exports com.jwebmp.core.base.angular.client.services;
    exports com.jwebmp.core.base.angular.client.services.interfaces;
    exports com.jwebmp.core.base.angular.client.annotations.boot;


    provides IGuiceScanModuleInclusions with AngularTypeScriptClientModuleInclusion;

    provides IGuiceModule with AngularClientModule;

    uses OnGetAllConstructorParameters;
    uses OnGetAllConstructorBodies;
    uses OnGetAllFields;
    uses OnGetAllImports;
    uses OnGetAllMethods;
    uses OnGetAllModuleImports;

    provides IGuiceConfigurator with GuicedConfig;
    provides IGuicePostStartup with AngularTypeScriptPostStartup;

    opens com.jwebmp.core.base.angular.client.services.spi to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.core.base.angular.client to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.core.base.angular.client.implementations to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.core.base.angular.client.services to com.google.guice, com.fasterxml.jackson.databind;
    exports com.jwebmp.core.base.angular.client.services.tstypes;
    opens com.jwebmp.core.base.angular.client.services.tstypes to com.fasterxml.jackson.databind, com.google.guice;
}