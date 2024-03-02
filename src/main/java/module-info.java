import com.guicedee.guicedinjection.interfaces.IGuiceScanModuleInclusions;
import com.jwebmp.core.base.angular.client.implementations.AngularTypeScriptClientModuleInclusion;
import com.jwebmp.core.base.angular.client.services.spi.*;

module com.jwebmp.core.base.angular.client {
    requires com.guicedee.client;
    requires com.jwebmp.client;
    //requires transitive com.jwebmp.core;
    //requires org.apache.commons.io;

    requires static lombok;
    requires com.guicedee.jsonrepresentation;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;

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

    uses OnGetAllConstructorParameters;
    uses OnGetAllConstructorBodies;
    uses OnGetAllFields;
    uses OnGetAllImports;
    uses OnGetAllMethods;

    opens com.jwebmp.core.base.angular.client.services.spi to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.core.base.angular.client to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.core.base.angular.client.services to com.google.guice, com.fasterxml.jackson.databind;
}