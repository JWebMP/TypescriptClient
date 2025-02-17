package com.jwebmp.core.base.angular.client.services;


import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterViewInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;

@NgAfterViewInit("this.calendarApi = this.calendarComponent?.getApi();")
@NgAfterViewInit("let currentDate = this.calendarApi?.view.currentStart;")
@NgAfterViewInit("this.fetchData();")


@NgField("currentEvents: EventApi[] = [];")
@NgField("@ViewChild('calendar')\n" +
        "    calendarComponent?: FullCalendarComponent;")
@NgField("private calendarApi? : CalendarApi;")

@NgImportReference(value = "CalendarOptions, DateSelectArg, EventClickArg, EventApi, EventDropArg,EventInput,CalendarApi", reference = "@fullcalendar/core")
@NgImportReference(value = "FullCalendarComponent", reference = "@fullcalendar/angular")
@NgImportReference(value = "DateClickArg, DropArg, EventReceiveArg, EventResizeDoneArg", reference = "@fullcalendar/interaction")
@NgImportReference(value = "FullCalendarModule ", reference = "@fullcalendar/angular")

@NgMethod("handleWeekendsToggle() {\n" +
        "    const { calendarOptions } = this;\n" +
        "    calendarOptions.weekends = !calendarOptions.weekends;\n" +
        "  }")

@NgComponentReference(EventBusService.class)

public class AnnotationTestClass extends AnnotationTestClassParent implements AnnotationTestClassInterface
{

}
